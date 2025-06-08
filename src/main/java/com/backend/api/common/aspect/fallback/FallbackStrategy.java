package com.backend.api.common.aspect.fallback;

/**
 * Redis Fallback 처리 전략 인터페이스
 * 
 * @author backend-api
 */
public interface FallbackStrategy {
    
    /**
     * Fallback 처리 로직 실행
     * 
     * @param methodName 실행된 메서드명
     * @param args 메서드 인자들
     * @param exception 발생한 예외
     * @return Fallback 처리 결과
     */
    Object executeFallback(String methodName, Object[] args, Throwable exception);
    
    /**
     * 해당 전략이 적용 가능한지 확인
     * 
     * @param methodName 메서드명
     * @return 적용 가능 여부
     */
    boolean isApplicable(String methodName);
} 