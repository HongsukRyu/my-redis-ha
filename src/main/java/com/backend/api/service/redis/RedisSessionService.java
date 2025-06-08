package com.backend.api.service.redis;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

/**
 * Redis 기반 세션 관리 서비스 인터페이스
 * 
 * @author backend-api
 */
public interface RedisSessionService {
    
    /**
     * 세션 데이터 저장
     */
    void setSessionData(String sessionId, String key, Object value);
    
    /**
     * TTL과 함께 세션 데이터 저장
     */
    void setSessionData(String sessionId, String key, Object value, Duration ttl);
    
    /**
     * 세션 데이터 조회
     */
    Object getSessionData(String sessionId, String key);
    
    /**
     * 세션의 모든 데이터 조회
     */
    Map<String, Object> getAllSessionData(String sessionId);
    
    /**
     * 세션 데이터 삭제
     */
    void removeSessionData(String sessionId, String key);
    
    /**
     * 전체 세션 삭제
     */
    void removeSession(String sessionId);
    
    /**
     * 세션 존재 여부 확인
     */
    boolean sessionExists(String sessionId);
    
    /**
     * 활성 세션 목록 조회
     */
    Set<String> getActiveSessions();
    
    /**
     * 세션 TTL 설정
     */
    void setSessionTtl(String sessionId, Duration ttl);
} 