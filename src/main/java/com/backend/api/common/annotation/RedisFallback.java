package com.backend.api.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Redis 연결 실패 시 Fallback 처리를 적용하는 어노테이션
 * 
 * @author backend-api
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisFallback {
    
    /**
     * Fallback 메서드명 (같은 클래스 내에 있어야 함)
     * 지정하지 않으면 기본 Fallback 로직 실행
     */
    String fallbackMethod() default "";
    
    /**
     * Fallback을 실행할 예외 클래스들
     */
    Class<? extends Throwable>[] exceptions() default {
        org.springframework.data.redis.RedisConnectionFailureException.class,
        org.springframework.dao.DataAccessException.class,
        java.net.ConnectException.class,
        java.util.concurrent.TimeoutException.class
    };
    
    /**
     * 로깅 여부
     */
    boolean enableLogging() default true;
    
    /**
     * Slack 알림 전송 여부
     */
    boolean enableSlackNotification() default true;
} 