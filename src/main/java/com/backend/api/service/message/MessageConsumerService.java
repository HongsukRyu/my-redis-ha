package com.backend.api.service.message;

import com.backend.api.model.message.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ 메시지 컨슈머 서비스
 * 
 * @author backend-api
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageConsumerService {
    
    // 일단 RabbitListener 어노테이션은 주석처리 (빌드 문제로 인해)
    
    /**
     * 사용자 이벤트 메시지 처리
     */
    // @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUserEvent(UserEventMessage message) {
        try {
            log.info("🎯 사용자 이벤트 메시지 수신: {}", message);
            
            // 사용자 이벤트 처리 로직
            switch (message.getEventType()) {
                case "LOGIN":
                    handleUserLogin(message);
                    break;
                case "LOGOUT":
                    handleUserLogout(message);
                    break;
                case "REGISTER":
                    handleUserRegister(message);
                    break;
                case "UPDATE_PROFILE":
                    handleUserProfileUpdate(message);
                    break;
                default:
                    log.warn("⚠️ 알 수 없는 사용자 이벤트 타입: {}", message.getEventType());
            }
            
            log.info("✅ 사용자 이벤트 처리 완료: {}", message.getMessageId());
            
        } catch (Exception e) {
            log.error("💥 사용자 이벤트 처리 실패: {}", e.getMessage());
            throw new RuntimeException("사용자 이벤트 처리 실패", e);
        }
    }
    
    /**
     * 알림 메시지 처리
     */
    // @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(NotificationMessage message) {
        try {
            log.info("🔔 알림 메시지 수신: {}", message);
            
            // 알림 타입별 처리
            switch (message.getNotificationType()) {
                case "EMAIL":
                    sendEmailNotification(message);
                    break;
                case "SMS":
                    sendSmsNotification(message);
                    break;
                case "PUSH":
                    sendPushNotification(message);
                    break;
                case "SLACK":
                    sendSlackNotification(message);
                    break;
                default:
                    log.warn("⚠️ 알 수 없는 알림 타입: {}", message.getNotificationType());
            }
            
            log.info("✅ 알림 메시지 처리 완료: {}", message.getMessageId());
            
        } catch (Exception e) {
            log.error("💥 알림 메시지 처리 실패: {}", e.getMessage());
            throw new RuntimeException("알림 메시지 처리 실패", e);
        }
    }
    
    /**
     * Redis Fallback 메시지 처리
     */
    // @RabbitListener(queues = RabbitMQConfig.REDIS_FALLBACK_QUEUE)
    public void handleRedisFallback(RedisFallbackMessage message) {
        try {
            log.warn("⚠️ Redis Fallback 메시지 수신: {}", message);
            
            // Redis 복구 후 재처리 로직
            switch (message.getOperationType()) {
                case "SET":
                    handleRedisSetFallback(message);
                    break;
                case "GET":
                    handleRedisGetFallback(message);
                    break;
                case "DELETE":
                    handleRedisDeleteFallback(message);
                    break;
                case "EXPIRE":
                    handleRedisExpireFallback(message);
                    break;
                default:
                    log.warn("⚠️ 알 수 없는 Redis 작업 타입: {}", message.getOperationType());
            }
            
            log.info("✅ Redis Fallback 처리 완료: {}", message.getMessageId());
            
        } catch (Exception e) {
            log.error("💥 Redis Fallback 처리 실패: {}", e.getMessage());
            // Redis Fallback 처리 실패 시에도 예외를 던지지 않음 (무한 루프 방지)
        }
    }
    
    /**
     * 감사 로그 메시지 처리
     */
    // @RabbitListener(queues = RabbitMQConfig.AUDIT_QUEUE)
    public void handleAuditLog(BaseMessage message) {
        try {
            log.info("📋 감사 로그 메시지 수신: {}", message);
            
            // 감사 로그를 데이터베이스나 파일에 저장
            saveAuditLogToDatabase(message);
            
            log.info("✅ 감사 로그 처리 완료: {}", message.getMessageId());
            
        } catch (Exception e) {
            log.error("💥 감사 로그 처리 실패: {}", e.getMessage());
            // 감사 로그 처리 실패는 예외를 던지지 않음
        }
    }
    
    /**
     * Dead Letter Queue 메시지 처리
     */
    // @RabbitListener(queues = RabbitMQConfig.DLQ_QUEUE)
    public void handleDeadLetterMessage(Object message) {
        try {
            log.error("💀 Dead Letter Queue 메시지 수신: {}", message);
            
            // DLQ 메시지 처리 (알림, 로그 등)
            handleDlqMessage(message);
            
        } catch (Exception e) {
            log.error("💥 DLQ 메시지 처리 실패: {}", e.getMessage());
        }
    }
    
    // ==================== 개별 처리 메서드들 ====================
    
    private void handleUserLogin(UserEventMessage message) {
        log.info("👤 사용자 로그인 처리: userId={}, ip={}", message.getUserId(), message.getClientIp());
        // 로그인 통계 업데이트, 세션 관리 등
    }
    
    private void handleUserLogout(UserEventMessage message) {
        log.info("🚪 사용자 로그아웃 처리: userId={}", message.getUserId());
        // 세션 정리, 로그아웃 통계 등
    }
    
    private void handleUserRegister(UserEventMessage message) {
        log.info("🆕 사용자 가입 처리: userId={}", message.getUserId());
        // 환영 메시지, 초기 설정 등
    }
    
    private void handleUserProfileUpdate(UserEventMessage message) {
        log.info("✏️ 사용자 프로필 업데이트 처리: userId={}", message.getUserId());
        // 프로필 변경 알림 등
    }
    
    private void sendEmailNotification(NotificationMessage message) {
        log.info("📧 이메일 알림 전송: recipient={}, title={}", message.getRecipientId(), message.getTitle());
        // 실제 이메일 전송 로직
    }
    
    private void sendSmsNotification(NotificationMessage message) {
        log.info("📱 SMS 알림 전송: recipient={}, content={}", message.getRecipientId(), message.getContent());
        // 실제 SMS 전송 로직
    }
    
    private void sendPushNotification(NotificationMessage message) {
        log.info("🔔 푸시 알림 전송: recipient={}, title={}", message.getRecipientId(), message.getTitle());
        // 실제 푸시 알림 전송 로직
    }
    
    private void sendSlackNotification(NotificationMessage message) {
        log.info("💬 Slack 알림 전송: recipient={}, content={}", message.getRecipientId(), message.getContent());
        // 실제 Slack 알림 전송 로직
    }
    
    private void handleRedisSetFallback(RedisFallbackMessage message) {
        log.info("🔄 Redis SET 재처리: key={}, value={}", message.getRedisKey(), message.getRedisValue());
        // Redis 복구 후 SET 작업 재수행
    }
    
    private void handleRedisGetFallback(RedisFallbackMessage message) {
        log.info("🔄 Redis GET 재처리: key={}", message.getRedisKey());
        // Redis 복구 후 GET 작업 재수행 (필요시)
    }
    
    private void handleRedisDeleteFallback(RedisFallbackMessage message) {
        log.info("🔄 Redis DELETE 재처리: key={}", message.getRedisKey());
        // Redis 복구 후 DELETE 작업 재수행
    }
    
    private void handleRedisExpireFallback(RedisFallbackMessage message) {
        log.info("🔄 Redis EXPIRE 재처리: key={}, ttl={}", message.getRedisKey(), message.getTtlSeconds());
        // Redis 복구 후 EXPIRE 작업 재수행
    }
    
    private void saveAuditLogToDatabase(BaseMessage message) {
        log.info("💾 감사 로그 데이터베이스 저장: messageId={}, type={}", message.getMessageId(), message.getMessageType());
        // 실제 데이터베이스 저장 로직
    }
    
    private void handleDlqMessage(Object message) {
        log.error("🚨 DLQ 메시지 처리 - 관리자 알림 필요: {}", message);
        // 관리자 알림, 수동 처리 큐 이동 등
    }
} 