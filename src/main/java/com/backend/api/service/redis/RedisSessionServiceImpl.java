package com.backend.api.service.redis;

import com.backend.api.common.annotation.RedisFallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 기반 세션 관리 서비스 구현체
 * 
 * @author backend-api
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSessionServiceImpl implements RedisSessionService {
    
    private static final String SESSION_PREFIX = "session:";
    private static final String ACTIVE_SESSIONS_KEY = "active_sessions";
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 메모리 기반 Fallback 스토리지 (간단한 예제용)
    private final Map<String, Map<String, Object>> fallbackStorage = new HashMap<>();
    
    @Override
    @RedisFallback(fallbackMethod = "setSessionDataFallback")
    public void setSessionData(String sessionId, String key, Object value) {
        String sessionKey = SESSION_PREFIX + sessionId;
        redisTemplate.opsForHash().put(sessionKey, key, value);
        redisTemplate.opsForSet().add(ACTIVE_SESSIONS_KEY, sessionId);
        redisTemplate.expire(sessionKey, Duration.ofHours(24)); // 기본 24시간 TTL
    }
    
    @Override
    @RedisFallback(fallbackMethod = "setSessionDataWithTtlFallback")
    public void setSessionData(String sessionId, String key, Object value, Duration ttl) {
        String sessionKey = SESSION_PREFIX + sessionId;
        redisTemplate.opsForHash().put(sessionKey, key, value);
        redisTemplate.opsForSet().add(ACTIVE_SESSIONS_KEY, sessionId);
        redisTemplate.expire(sessionKey, ttl);
    }
    
    @Override
    @RedisFallback(fallbackMethod = "getSessionDataFallback")
    public Object getSessionData(String sessionId, String key) {
        String sessionKey = SESSION_PREFIX + sessionId;
        return redisTemplate.opsForHash().get(sessionKey, key);
    }
    
    @Override
    @RedisFallback(fallbackMethod = "getAllSessionDataFallback")
    public Map<String, Object> getAllSessionData(String sessionId) {
        String sessionKey = SESSION_PREFIX + sessionId;
        Map<Object, Object> rawEntries = redisTemplate.opsForHash().entries(sessionKey);
        Map<String, Object> sessionData = new HashMap<>();
        for (Map.Entry<Object, Object> entry : rawEntries.entrySet()) {
            sessionData.put(entry.getKey().toString(), entry.getValue());
        }
        return sessionData;
    }
    
    @Override
    @RedisFallback(fallbackMethod = "removeSessionDataFallback")
    public void removeSessionData(String sessionId, String key) {
        String sessionKey = SESSION_PREFIX + sessionId;
        redisTemplate.opsForHash().delete(sessionKey, key);
    }
    
    @Override
    @RedisFallback(fallbackMethod = "removeSessionFallback")
    public void removeSession(String sessionId) {
        String sessionKey = SESSION_PREFIX + sessionId;
        redisTemplate.delete(sessionKey);
        redisTemplate.opsForSet().remove(ACTIVE_SESSIONS_KEY, sessionId);
    }
    
    @Override
    @RedisFallback(fallbackMethod = "sessionExistsFallback")
    public boolean sessionExists(String sessionId) {
        String sessionKey = SESSION_PREFIX + sessionId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey));
    }
    
    @Override
    @RedisFallback(fallbackMethod = "getActiveSessionsFallback")
    public Set<String> getActiveSessions() {
        Set<Object> sessions = redisTemplate.opsForSet().members(ACTIVE_SESSIONS_KEY);
        if (sessions == null) return new HashSet<>();
        return sessions.stream()
                .map(Object::toString)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }
    
    @Override
    @RedisFallback(fallbackMethod = "setSessionTtlFallback")
    public void setSessionTtl(String sessionId, Duration ttl) {
        String sessionKey = SESSION_PREFIX + sessionId;
        redisTemplate.expire(sessionKey, ttl);
    }
    
    // ====== Fallback 메서드들 ======
    
    /**
     * 세션 데이터 저장 Fallback
     */
    public void setSessionDataFallback(String sessionId, String key, Object value) {
        log.warn("🟡 Redis Fallback - 메모리에 세션 데이터 저장: sessionId={}, key={}", sessionId, key);
        fallbackStorage.computeIfAbsent(sessionId, k -> new HashMap<>()).put(key, value);
    }
    
    /**
     * TTL과 함께 세션 데이터 저장 Fallback
     */
    public void setSessionDataWithTtlFallback(String sessionId, String key, Object value, Duration ttl) {
        log.warn("🟡 Redis Fallback - 메모리에 세션 데이터 저장 (TTL 무시): sessionId={}, key={}", sessionId, key);
        fallbackStorage.computeIfAbsent(sessionId, k -> new HashMap<>()).put(key, value);
        // 메모리에서는 TTL 구현 생략 (실제 운영에서는 scheduled task로 처리 가능)
    }
    
    /**
     * 세션 데이터 조회 Fallback
     */
    public Object getSessionDataFallback(String sessionId, String key) {
        log.warn("🟡 Redis Fallback - 메모리에서 세션 데이터 조회: sessionId={}, key={}", sessionId, key);
        Map<String, Object> sessionData = fallbackStorage.get(sessionId);
        return sessionData != null ? sessionData.get(key) : null;
    }
    
    /**
     * 모든 세션 데이터 조회 Fallback
     */
    public Map<String, Object> getAllSessionDataFallback(String sessionId) {
        log.warn("🟡 Redis Fallback - 메모리에서 모든 세션 데이터 조회: sessionId={}", sessionId);
        return fallbackStorage.getOrDefault(sessionId, new HashMap<>());
    }
    
    /**
     * 세션 데이터 삭제 Fallback
     */
    public void removeSessionDataFallback(String sessionId, String key) {
        log.warn("🟡 Redis Fallback - 메모리에서 세션 데이터 삭제: sessionId={}, key={}", sessionId, key);
        Map<String, Object> sessionData = fallbackStorage.get(sessionId);
        if (sessionData != null) {
            sessionData.remove(key);
        }
    }
    
    /**
     * 전체 세션 삭제 Fallback
     */
    public void removeSessionFallback(String sessionId) {
        log.warn("🟡 Redis Fallback - 메모리에서 전체 세션 삭제: sessionId={}", sessionId);
        fallbackStorage.remove(sessionId);
    }
    
    /**
     * 세션 존재 여부 확인 Fallback
     */
    public boolean sessionExistsFallback(String sessionId) {
        log.warn("🟡 Redis Fallback - 메모리에서 세션 존재 여부 확인: sessionId={}", sessionId);
        return fallbackStorage.containsKey(sessionId);
    }
    
    /**
     * 활성 세션 목록 조회 Fallback
     */
    public Set<String> getActiveSessionsFallback() {
        log.warn("🟡 Redis Fallback - 메모리에서 활성 세션 목록 조회");
        return new HashSet<>(fallbackStorage.keySet());
    }
    
    /**
     * 세션 TTL 설정 Fallback
     */
    public void setSessionTtlFallback(String sessionId, Duration ttl) {
        log.warn("🟡 Redis Fallback - TTL 설정 무시 (메모리 모드): sessionId={}", sessionId);
        // 메모리에서는 TTL 구현 생략
    }
} 