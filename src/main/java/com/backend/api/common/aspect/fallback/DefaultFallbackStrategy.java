package com.backend.api.common.aspect.fallback;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * 기본 Redis Fallback 처리 전략 구현체
 * 
 * @author backend-api
 */
@Component
public class DefaultFallbackStrategy implements FallbackStrategy {
    
    @Override
    public Object executeFallback(String methodName, Object[] args, Throwable exception) {
        // 메서드 반환 타입에 따른 기본값 처리
        if (methodName.contains("isMethodAllowed") || methodName.contains("isValid")) {
            // Boolean 메서드들의 경우 보수적으로 false 반환
            return false;
        }
        
        if (methodName.contains("findAll") || methodName.contains("getList") || methodName.contains("List")) {
            // 리스트 관련 메서드들의 경우 빈 리스트 반환
            return Collections.emptyList();
        }
        
        if (methodName.contains("findBy") || methodName.contains("get") || methodName.contains("retrieve")) {
            // 단일 객체 조회의 경우 null 반환
            return null;
        }
        
        if (methodName.contains("count") || methodName.contains("size")) {
            // 카운트 관련 메서드들의 경우 0 반환
            return 0L;
        }
        
        if (methodName.contains("exists")) {
            // 존재 확인 메서드들의 경우 false 반환
            return false;
        }
        
        if (methodName.contains("getMap") || methodName.contains("Map")) {
            // Map 관련 메서드들의 경우 빈 Map 반환
            return Collections.emptyMap();
        }
        
        // 기본적으로 null 반환
        return null;
    }
    
    @Override
    public boolean isApplicable(String methodName) {
        // 모든 메서드에 적용 가능한 기본 전략
        return true;
    }
} 