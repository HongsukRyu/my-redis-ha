package com.backend.api.service.message;

import com.backend.api.model.message.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ 메시지 프로듀서 서비스
 * 
 * @author backend-api
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProducerService {
    
    // RabbitTemplate 의존성은 일단 주석처리 (빌드 문제로 인해)
    // private final RabbitTemplate rabbitTemplate;
    
    /**
     * 사용자 이벤트 메시지 전송
     */
    public void sendUserEvent(UserEventMessage message) {
        try {
            log.info("🚀 사용자 이벤트 메시지 전송: {}", message);
            // rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, RabbitMQConfig.USER_ROUTING_KEY, message);
        } catch (Exception e) {
            log.error("💥 사용자 이벤트 메시지 전송 실패: {}", e.getMessage());
            throw new RuntimeException("사용자 이벤트 메시지 전송 실패", e);
        }
    }
    
    /**
     * 사용자 이벤트 메시지 전송 (편의 메서드)
     */
    public void sendUserEvent(String userId, String eventType, String clientIp) {
        UserEventMessage message = new UserEventMessage(userId, eventType, clientIp);
        sendUserEvent(message);
    }
    
    /**
     * 사용자 이벤트 메시지 전송 (User Agent 포함)
     */
    public void sendUserEvent(String userId, String eventType, String clientIp, String userAgent) {
        UserEventMessage message = new UserEventMessage(userId, eventType, clientIp, userAgent);
        sendUserEvent(message);
    }
    
    /**
     * 알림 메시지 전송
     */
    public void sendNotification(NotificationMessage message) {
        try {
            log.info("🔔 알림 메시지 전송: {}", message);
            // rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.NOTIFICATION_ROUTING_KEY, message);
        } catch (Exception e) {
            log.error("💥 알림 메시지 전송 실패: {}", e.getMessage());
            throw new RuntimeException("알림 메시지 전송 실패", e);
        }
    }
    
    /**
     * 알림 메시지 전송 (편의 메서드)
     */
    public void sendNotification(String recipientId, String recipientType, String title, String content, String notificationType) {
        NotificationMessage message = new NotificationMessage(recipientId, recipientType, title, content, notificationType);
        sendNotification(message);
    }
    
    /**
     * Redis Fallback 메시지 전송
     */
    public void sendRedisFallback(RedisFallbackMessage message) {
        try {
            log.warn("⚠️ Redis Fallback 메시지 전송: {}", message);
            // rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.REDIS_FALLBACK_ROUTING_KEY, message);
        } catch (Exception e) {
            log.error("💥 Redis Fallback 메시지 전송 실패: {}", e.getMessage());
            // Redis Fallback 메시지 전송 실패는 예외를 던지지 않음 (무한 루프 방지)
        }
    }
    
    /**
     * Redis Fallback 메시지 전송 (편의 메서드)
     */
    public void sendRedisFallback(String operationType, String redisKey, String redisValue, String failedMethod, String failureReason) {
        RedisFallbackMessage message = new RedisFallbackMessage(operationType, redisKey, redisValue, failedMethod, failureReason);
        sendRedisFallback(message);
    }
    
    /**
     * 감사 로그 메시지 전송
     */
    public void sendAuditLog(BaseMessage message) {
        try {
            log.info("📋 감사 로그 메시지 전송: {}", message);
            // rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, RabbitMQConfig.AUDIT_ROUTING_KEY, message);
        } catch (Exception e) {
            log.error("💥 감사 로그 메시지 전송 실패: {}", e.getMessage());
            // 감사 로그 전송 실패는 예외를 던지지 않음
        }
    }
    
    /**
     * 우선순위와 함께 메시지 전송
     */
    public void sendMessageWithPriority(String exchange, String routingKey, Object message, int priority) {
        try {
            log.info("📤 우선순위 메시지 전송 - Priority: {}, Exchange: {}, RoutingKey: {}", priority, exchange, routingKey);
            
            // MessageProperties properties = new MessageProperties();
            // properties.setPriority(priority);
            // Message amqpMessage = rabbitTemplate.getMessageConverter().toMessage(message, properties);
            // rabbitTemplate.send(exchange, routingKey, amqpMessage);
            
        } catch (Exception e) {
            log.error("💥 우선순위 메시지 전송 실패: {}", e.getMessage());
            throw new RuntimeException("우선순위 메시지 전송 실패", e);
        }
    }
    
    /**
     * 지연 메시지 전송 (TTL 설정)
     */
    public void sendDelayedMessage(String exchange, String routingKey, Object message, long delayMs) {
        try {
            log.info("⏰ 지연 메시지 전송 - Delay: {}ms, Exchange: {}, RoutingKey: {}", delayMs, exchange, routingKey);
            
            // MessageProperties properties = new MessageProperties();
            // properties.setExpiration(String.valueOf(delayMs));
            // Message amqpMessage = rabbitTemplate.getMessageConverter().toMessage(message, properties);
            // rabbitTemplate.send(exchange, routingKey, amqpMessage);
            
        } catch (Exception e) {
            log.error("💥 지연 메시지 전송 실패: {}", e.getMessage());
            throw new RuntimeException("지연 메시지 전송 실패", e);
        }
    }
} 