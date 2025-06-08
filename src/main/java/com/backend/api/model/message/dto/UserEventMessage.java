package com.backend.api.model.message.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 사용자 이벤트 메시지
 * 
 * @author backend-api
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserEventMessage extends BaseMessage {
    
    /**
     * 사용자 ID
     */
    private String userId;
    
    /**
     * 이벤트 타입 (LOGIN, LOGOUT, REGISTER, UPDATE_PROFILE, etc.)
     */
    private String eventType;
    
    /**
     * 클라이언트 IP
     */
    private String clientIp;
    
    /**
     * 사용자 에이전트
     */
    private String userAgent;
    
    /**
     * 추가 데이터
     */
    private Map<String, Object> additionalData;
    
    public UserEventMessage(String userId, String eventType, String clientIp) {
        super("USER_EVENT", "SYSTEM");
        this.userId = userId;
        this.eventType = eventType;
        this.clientIp = clientIp;
    }
    
    public UserEventMessage(String userId, String eventType, String clientIp, String userAgent) {
        this(userId, eventType, clientIp);
        this.userAgent = userAgent;
    }
} 