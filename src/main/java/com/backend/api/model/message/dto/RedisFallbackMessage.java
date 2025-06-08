package com.backend.api.model.message.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Redis Fallback 메시지
 * Redis 장애 시 처리하지 못한 데이터를 큐를 통해 전달
 * 
 * @author backend-api
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RedisFallbackMessage extends BaseMessage {
    
    /**
     * 원본 작업 타입 (SET, GET, DELETE, EXPIRE, etc.)
     */
    private String operationType;
    
    /**
     * Redis 키
     */
    private String redisKey;
    
    /**
     * Redis 값 (JSON 형태)
     */
    private String redisValue;
    
    /**
     * TTL (초)
     */
    private Long ttlSeconds;
    
    /**
     * 실패한 메서드명
     */
    private String failedMethod;
    
    /**
     * 실패 원인
     */
    private String failureReason;
    
    /**
     * 원본 서비스 클래스명
     */
    private String sourceClass;
    
    public RedisFallbackMessage(String operationType, String redisKey, String redisValue, String failedMethod, String failureReason) {
        super("REDIS_FALLBACK", "REDIS_AOP");
        this.operationType = operationType;
        this.redisKey = redisKey;
        this.redisValue = redisValue;
        this.failedMethod = failedMethod;
        this.failureReason = failureReason;
        this.setPriority(8); // Redis Fallback은 높은 우선순위
    }
    
    public RedisFallbackMessage(String operationType, String redisKey, String redisValue, Long ttlSeconds, String failedMethod, String failureReason) {
        this(operationType, redisKey, redisValue, failedMethod, failureReason);
        this.ttlSeconds = ttlSeconds;
    }
} 