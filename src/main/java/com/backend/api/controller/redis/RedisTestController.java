package com.backend.api.controller.redis;

import com.backend.api.common.object.Success;
import com.backend.api.service.redis.RedisSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

/**
 * Redis AOP Fallback 테스트용 컨트롤러
 * 
 * @author backend-api
 */
@RestController
@RequestMapping("/api/redis-test")
@RequiredArgsConstructor
@Tag(name = "Redis Test", description = "Redis AOP Fallback 테스트용 API")
public class RedisTestController {
    
    private final RedisSessionService redisSessionService;
    
    @PostMapping("/session/{sessionId}")
    @Operation(summary = "세션 데이터 저장", description = "Redis에 세션 데이터를 저장합니다.")
    public ResponseEntity<Success> setSessionData(
            @PathVariable String sessionId,
            @RequestParam String key,
            @RequestParam String value) {
        
        Success success = new Success(true);
        
        try {
            redisSessionService.setSessionData(sessionId, key, value);
            success.setResult("세션 데이터가 저장되었습니다.");
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/session/{sessionId}/ttl")
    @Operation(summary = "TTL과 함께 세션 데이터 저장", description = "TTL과 함께 Redis에 세션 데이터를 저장합니다.")
    public ResponseEntity<Success> setSessionDataWithTtl(
            @PathVariable String sessionId,
            @RequestParam String key,
            @RequestParam String value,
            @RequestParam(defaultValue = "3600") long ttlSeconds) {
        
        Success success = new Success(true);
        
        try {
            redisSessionService.setSessionData(sessionId, key, value, Duration.ofSeconds(ttlSeconds));
            success.setResult("TTL과 함께 세션 데이터가 저장되었습니다.");
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @GetMapping("/session/{sessionId}/{key}")
    @Operation(summary = "세션 데이터 조회", description = "Redis에서 특정 세션 데이터를 조회합니다.")
    public ResponseEntity<Success> getSessionData(
            @PathVariable String sessionId,
            @PathVariable String key) {
        
        Success success = new Success(true);
        
        try {
            Object value = redisSessionService.getSessionData(sessionId, key);
            success.setResult(value);
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @GetMapping("/session/{sessionId}")
    @Operation(summary = "모든 세션 데이터 조회", description = "Redis에서 세션의 모든 데이터를 조회합니다.")
    public ResponseEntity<Success> getAllSessionData(@PathVariable String sessionId) {
        
        Success success = new Success(true);
        
        try {
            Map<String, Object> sessionData = redisSessionService.getAllSessionData(sessionId);
            success.setResult(sessionData);
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @DeleteMapping("/session/{sessionId}/{key}")
    @Operation(summary = "세션 데이터 삭제", description = "Redis에서 특정 세션 데이터를 삭제합니다.")
    public ResponseEntity<Success> removeSessionData(
            @PathVariable String sessionId,
            @PathVariable String key) {
        
        Success success = new Success(true);
        
        try {
            redisSessionService.removeSessionData(sessionId, key);
            success.setResult("세션 데이터가 삭제되었습니다.");
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @DeleteMapping("/session/{sessionId}")
    @Operation(summary = "전체 세션 삭제", description = "Redis에서 전체 세션을 삭제합니다.")
    public ResponseEntity<Success> removeSession(@PathVariable String sessionId) {
        
        Success success = new Success(true);
        
        try {
            redisSessionService.removeSession(sessionId);
            success.setResult("세션이 삭제되었습니다.");
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @GetMapping("/session/{sessionId}/exists")
    @Operation(summary = "세션 존재 여부 확인", description = "Redis에서 세션 존재 여부를 확인합니다.")
    public ResponseEntity<Success> sessionExists(@PathVariable String sessionId) {
        
        Success success = new Success(true);
        
        try {
            boolean exists = redisSessionService.sessionExists(sessionId);
            success.setResult(exists);
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
    
    @GetMapping("/sessions/active")
    @Operation(summary = "활성 세션 목록 조회", description = "Redis에서 활성 세션 목록을 조회합니다.")
    public ResponseEntity<Success> getActiveSessions() {
        
        Success success = new Success(true);
        
        try {
            Set<String> activeSessions = redisSessionService.getActiveSessions();
            success.setResult(activeSessions);
        } catch (Exception e) {
            success.setSuccess(false);
            success.setErrorCode("FAIL");
            success.setErrorMsg(e.getMessage());
        }
        
        return ResponseEntity.ok(success);
    }
} 