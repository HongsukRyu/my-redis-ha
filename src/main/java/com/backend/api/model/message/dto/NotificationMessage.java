package com.backend.api.model.message.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 알림 메시지
 * 
 * @author backend-api
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class NotificationMessage extends BaseMessage {
    
    /**
     * 수신자 ID (사용자 ID 또는 그룹 ID)
     */
    private String recipientId;
    
    /**
     * 수신자 타입 (USER, GROUP, ALL)
     */
    private String recipientType;
    
    /**
     * 알림 제목
     */
    private String title;
    
    /**
     * 알림 내용
     */
    private String content;
    
    /**
     * 알림 타입 (EMAIL, SMS, PUSH, SLACK)
     */
    private String notificationType;
    
    /**
     * 연관 URL (선택사항)
     */
    private String actionUrl;
    
    /**
     * 템플릿 ID (선택사항)
     */
    private String templateId;
    
    public NotificationMessage(String recipientId, String recipientType, String title, String content, String notificationType) {
        super("NOTIFICATION", "SYSTEM");
        this.recipientId = recipientId;
        this.recipientType = recipientType;
        this.title = title;
        this.content = content;
        this.notificationType = notificationType;
        this.setPriority(7); // 알림은 기본적으로 높은 우선순위
    }
} 