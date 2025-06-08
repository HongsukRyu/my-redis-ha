package com.backend.api.model.message.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RabbitMQ 메시지 기본 모델
 * 
 * @author backend-api
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseMessage {
    
    /**
     * 메시지 고유 ID
     */
    private String messageId;
    
    /**
     * 메시지 타입
     */
    private String messageType;
    
    /**
     * 발송자 정보
     */
    private String sender;
    
    /**
     * 메시지 생성 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * 재시도 횟수
     */
    private int retryCount;
    
    /**
     * 우선순위 (1: 낮음, 5: 보통, 10: 높음)
     */
    private int priority;
    
    /**
     * 메시지 TTL (밀리초)
     */
    private Long ttl;
    
    public BaseMessage(String messageType, String sender) {
        this.messageId = UUID.randomUUID().toString();
        this.messageType = messageType;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
        this.retryCount = 0;
        this.priority = 5; // 기본 우선순위
    }
} 