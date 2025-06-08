package com.backend.api.common.aspect;

import com.backend.api.common.annotation.RedisFallback;
import com.backend.api.common.aspect.fallback.FallbackStrategy;
import com.backend.api.common.utils.MessageLogger;
import com.backend.api.common.utils.Utils;
import com.backend.api.service.message.MessageProducerService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Redis Fallback 처리를 위한 AOP Aspect
 * 
 * @author backend-api
 */
@Aspect
@Component
@RequiredArgsConstructor
public class RedisFallbackAspect {

    private static final MessageLogger logger = new MessageLogger(LoggerFactory.getLogger(RedisFallbackAspect.class));
    
    private final List<FallbackStrategy> fallbackStrategies;
    private final Utils utils;
    private final Environment env;
    private final MessageProducerService messageProducerService;

    /**
     * @RedisFallback 어노테이션이 적용된 메서드에 대한 Around Advice
     */
    @Around("@annotation(redisFallback)")
    public Object handleRedisFallback(ProceedingJoinPoint joinPoint, RedisFallback redisFallback) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        Object[] args = joinPoint.getArgs();
        
        try {
            // 원본 메서드 실행 시도
            return joinPoint.proceed();
            
        } catch (Throwable throwable) {
            // 예외가 RedisFallback 처리 대상인지 확인
            if (isTargetException(throwable, redisFallback.exceptions())) {
                
                // 로깅 처리
                if (redisFallback.enableLogging()) {
                    logger.errorLog("🔴 Redis Fallback 실행 - 메서드: {}, 예외: {}", 
                        methodName, throwable.getMessage());
                }
                
                // Slack 알림 처리
                if (redisFallback.enableSlackNotification()) {
                    sendSlackNotification(methodName, throwable);
                }
                
                // RabbitMQ로 Redis Fallback 메시지 전송
                sendRedisFallbackMessage(methodName, args, throwable, joinPoint.getTarget().getClass().getSimpleName());
                
                // Fallback 메서드가 지정된 경우
                if (!redisFallback.fallbackMethod().isEmpty()) {
                    return executeFallbackMethod(joinPoint, redisFallback.fallbackMethod(), args, throwable);
                }
                
                // 기본 Fallback 전략 실행
                return executeDefaultFallback(methodName, args, throwable);
            }
            
            // 대상 예외가 아닌 경우 원본 예외를 그대로 던짐
            throw throwable;
        }
    }

    /**
     * 예외가 Fallback 처리 대상인지 확인
     */
    private boolean isTargetException(Throwable throwable, Class<? extends Throwable>[] targetExceptions) {
        return Arrays.stream(targetExceptions)
                .anyMatch(exceptionClass -> exceptionClass.isAssignableFrom(throwable.getClass()) ||
                        isNestedException(throwable, exceptionClass));
    }

    /**
     * 중첩된 예외까지 확인
     */
    private boolean isNestedException(Throwable throwable, Class<? extends Throwable> targetException) {
        Throwable cause = throwable.getCause();
        while (cause != null) {
            if (targetException.isAssignableFrom(cause.getClass())) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    /**
     * 지정된 Fallback 메서드 실행
     */
    private Object executeFallbackMethod(ProceedingJoinPoint joinPoint, String fallbackMethodName, 
                                       Object[] args, Throwable throwable) {
        try {
            Object target = joinPoint.getTarget();
            Class<?> targetClass = target.getClass();
            
            // Fallback 메서드 찾기 (원본 메서드와 동일한 파라미터 타입)
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method fallbackMethod = targetClass.getDeclaredMethod(fallbackMethodName, signature.getParameterTypes());
            fallbackMethod.setAccessible(true);
            
            logger.infoLog("🟡 커스텀 Fallback 메서드 실행: {}", fallbackMethodName);
            return fallbackMethod.invoke(target, args);
            
        } catch (Exception e) {
            logger.errorLog("⚠️ Fallback 메서드 실행 실패: {}, 기본 Fallback으로 전환", fallbackMethodName);
            return executeDefaultFallback(joinPoint.getSignature().getName(), args, throwable);
        }
    }

    /**
     * 기본 Fallback 전략 실행
     */
    private Object executeDefaultFallback(String methodName, Object[] args, Throwable throwable) {
        // 적용 가능한 Fallback 전략 찾기
        FallbackStrategy strategy = fallbackStrategies.stream()
                .filter(s -> s.isApplicable(methodName))
                .findFirst()
                .orElse(fallbackStrategies.get(0)); // 기본 전략 사용
        
        logger.infoLog("🟡 기본 Fallback 전략 실행: {}", strategy.getClass().getSimpleName());
        return strategy.executeFallback(methodName, args, throwable);
    }

    /**
     * Slack 알림 전송
     */
    private void sendSlackNotification(String methodName, Throwable throwable) {
        try {
            String webhookUrl = env.getProperty("logging.slack.webhook-redis");
            if (webhookUrl != null && !webhookUrl.isEmpty()) {
                String message = String.format(
                    "🔴 Redis Fallback 발생!\n" +
                    "• 메서드: %s\n" +
                    "• 예외: %s\n" +
                    "• 메시지: %s",
                    methodName,
                    throwable.getClass().getSimpleName(),
                    throwable.getMessage()
                );
                utils.sendSlackMessage(webhookUrl, message);
            }
        } catch (Exception e) {
            logger.errorLog("Slack 알림 전송 실패: {}", e.getMessage());
        }
    }
    
    /**
     * RabbitMQ로 Redis Fallback 메시지 전송
     */
    private void sendRedisFallbackMessage(String methodName, Object[] args, Throwable throwable, String sourceClass) {
        try {
            // Redis 작업 타입 추론
            String operationType = "UNKNOWN";
            String redisKey = "unknown";
            String redisValue = "";
            
            // 메서드명으로부터 작업 타입 추론
            if (methodName.contains("set") || methodName.contains("Set") || methodName.contains("save")) {
                operationType = "SET";
            } else if (methodName.contains("get") || methodName.contains("Get") || methodName.contains("find")) {
                operationType = "GET";
            } else if (methodName.contains("delete") || methodName.contains("Delete") || methodName.contains("remove")) {
                operationType = "DELETE";
            } else if (methodName.contains("expire") || methodName.contains("Expire") || methodName.contains("ttl")) {
                operationType = "EXPIRE";
            }
            
            // 인자로부터 Redis 키 추출 시도
            if (args != null && args.length > 0) {
                for (Object arg : args) {
                    if (arg instanceof String) {
                        String argStr = (String) arg;
                        if (argStr.contains(":") || argStr.startsWith("session") || argStr.startsWith("cache")) {
                            redisKey = argStr;
                            break;
                        }
                    }
                }
                
                // 값 추출 시도 (JSON 형태로 변환)
                if (args.length > 1 && args[1] != null) {
                    redisValue = args[1].toString();
                }
            }
            
            // RabbitMQ로 메시지 전송
            messageProducerService.sendRedisFallback(
                operationType,
                redisKey,
                redisValue,
                methodName,
                throwable.getMessage()
            );
            
            logger.infoLog("🚀 Redis Fallback 메시지를 RabbitMQ로 전송 완료: method={}, key={}", methodName, redisKey);
            
        } catch (Exception e) {
            logger.errorLog("⚠️ Redis Fallback 메시지 전송 실패: {}", e.getMessage());
            // RabbitMQ 전송 실패는 원본 Fallback 처리에 영향을 주지 않음
        }
    }
} 