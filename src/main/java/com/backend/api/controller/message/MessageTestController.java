package com.backend.api.controller.message;

import com.backend.api.common.object.Success;
import com.backend.api.model.message.dto.*;
import com.backend.api.service.message.MessageProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 메시지 테스트용 컨트롤러
 * 
 * @author backend-api
 */
@RestController
@RequestMapping("/api/message-test")
@RequiredArgsConstructor
@Tag(name = "Message Test", description = "RabbitMQ 메시지 테스트용 API")
public class MessageTestController {
    
    private final MessageProducerService messageProducerService;
    
    @PostMapping("/user-event")
    @Operation(summary = "사용자 이벤트 메시지 전송", description = "사용자 이벤트 메시지를 큐에 전송합니다.")
    public ResponseEntity<Success> sendUserEvent(
            @RequestParam String userId,
            @RequestParam String eventType,
            @RequestParam String clientIp,
            @RequestParam(required = false) String userAgent) {
        
        Success success = new Success(true);
        
        try {
            if (userAgent != null) {
                messageProducerService.sendUserEvent(userId, eventType, clientIp, userAgent);
            } else {
                messageProducerService.sendUserEvent(userId, eventType, clientIp);
            }
            
            success.setResult("사용자 이벤트 메시지가 전송되었습니다.");
            
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/user-event/batch")
    @Operation(summary = "사용자 이벤트 배치 전송", description = "여러 사용자 이벤트를 한 번에 전송합니다.")
    public ResponseEntity<Success> sendUserEventBatch(@RequestParam String userId) {
        
        Success success = new Success(true);
        
        try {
            // 로그인 이벤트
            messageProducerService.sendUserEvent(userId, "LOGIN", "192.168.1.100", "Mozilla/5.0");
            
            // 프로필 업데이트 이벤트
            UserEventMessage profileUpdate = new UserEventMessage(userId, "UPDATE_PROFILE", "192.168.1.100");
            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("updatedField", "email");
            additionalData.put("oldValue", "old@example.com");
            additionalData.put("newValue", "new@example.com");
            profileUpdate.setAdditionalData(additionalData);
            messageProducerService.sendUserEvent(profileUpdate);
            
            success.setResult("배치 사용자 이벤트 메시지가 전송되었습니다.");
            
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/notification")
    @Operation(summary = "알림 메시지 전송", description = "알림 메시지를 큐에 전송합니다.")
    public ResponseEntity<Success> sendNotification(
            @RequestParam String recipientId,
            @RequestParam String recipientType,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String notificationType,
            @RequestParam(required = false) String actionUrl) {
        
        Success success = new Success(true);
        
        try {
            NotificationMessage message = new NotificationMessage(recipientId, recipientType, title, content, notificationType);
            if (actionUrl != null) {
                message.setActionUrl(actionUrl);
            }
            
            messageProducerService.sendNotification(message);
            success.setResult("알림 메시지가 전송되었습니다.");
            
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/notification/batch")
    @Operation(summary = "알림 배치 전송", description = "다양한 타입의 알림을 한 번에 전송합니다.")
    public ResponseEntity<Success> sendNotificationBatch(@RequestParam String userId) {
        
        Success success = new Success(true);
        
        try {
            // 이메일 알림
            messageProducerService.sendNotification(
                userId, "USER", "환영합니다!", "회원가입을 축하합니다.", "EMAIL"
            );
            
            // SMS 알림
            messageProducerService.sendNotification(
                userId, "USER", "인증 코드", "인증 코드: 123456", "SMS"
            );
            
            // 푸시 알림
            NotificationMessage pushMessage = new NotificationMessage(
                userId, "USER", "새로운 메시지", "새로운 메시지가 도착했습니다.", "PUSH"
            );
            pushMessage.setActionUrl("/messages/inbox");
            messageProducerService.sendNotification(pushMessage);
            
            success.setResult("배치 알림 메시지가 전송되었습니다.");
            
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/redis-fallback")
    @Operation(summary = "Redis Fallback 메시지 전송", description = "Redis Fallback 메시지를 큐에 전송합니다.")
    public ResponseEntity<Success> sendRedisFallback(
            @RequestParam String operationType,
            @RequestParam String redisKey,
            @RequestParam String redisValue,
            @RequestParam String failedMethod,
            @RequestParam String failureReason,
            @RequestParam(required = false) Long ttlSeconds) {
        
        Success success = new Success(true);
        
        try {
            RedisFallbackMessage message;
            if (ttlSeconds != null) {
                message = new RedisFallbackMessage(operationType, redisKey, redisValue, ttlSeconds, failedMethod, failureReason);
            } else {
                message = new RedisFallbackMessage(operationType, redisKey, redisValue, failedMethod, failureReason);
            }
            
            messageProducerService.sendRedisFallback(message);
            success.setResult("Redis Fallback 메시지가 전송되었습니다.");
            
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/audit-log")
    @Operation(summary = "감사 로그 메시지 전송", description = "감사 로그 메시지를 큐에 전송합니다.")
    public ResponseEntity<Success> sendAuditLog(
            @RequestParam String messageType,
            @RequestParam String sender,
            @RequestParam(required = false) String details) {
        
        Success success = new Success(true);
        
        try {
            BaseMessage auditMessage = new BaseMessage(messageType, sender);
            auditMessage.setPriority(6); // 감사 로그는 중간 우선순위
            
            messageProducerService.sendAuditLog(auditMessage);
            success.setResult("감사 로그 메시지가 전송되었습니다.");
            
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/priority-test")
    @Operation(summary = "우선순위 메시지 테스트", description = "다양한 우선순위의 메시지를 전송합니다.")
    public ResponseEntity<Success> testPriorityMessages() {
        
        Success success = new Success(true);
        
        try {
            // 낮은 우선순위 메시지
            BaseMessage lowPriority = new BaseMessage("LOW_PRIORITY", "SYSTEM");
            lowPriority.setPriority(1);
            messageProducerService.sendAuditLog(lowPriority);
            
            // 높은 우선순위 메시지  
            NotificationMessage highPriority = new NotificationMessage(
                "admin", "USER", "긴급 알림", "시스템 점검이 필요합니다.", "EMAIL"
            );
            highPriority.setPriority(10);
            messageProducerService.sendNotification(highPriority);
            
            // 보통 우선순위 메시지
            UserEventMessage normalPriority = new UserEventMessage("user123", "LOGIN", "192.168.1.1");
            normalPriority.setPriority(5);
            messageProducerService.sendUserEvent(normalPriority);
            
            success.setResult("우선순위 테스트 메시지들이 전송되었습니다.");
            
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @GetMapping("/status")
    @Operation(summary = "메시지 시스템 상태", description = "메시지 시스템의 현재 상태를 조회합니다.")
    public ResponseEntity<Success> getMessageSystemStatus() {
        
        Success success = new Success(true);
        
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("producer_status", "ACTIVE");
            status.put("consumer_status", "ACTIVE");
            status.put("rabbitmq_connection", "CONNECTED");
            status.put("message_count_today", 0); // 실제로는 Redis나 DB에서 조회
            status.put("failed_message_count", 0);
            
            success.setResult(status);
            
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
} 