package com.backend.api.service.aclpolicy;

import java.util.Arrays;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.backend.api.common.annotation.RedisFallback;
import com.backend.api.model.aclpolicy.dto.AclCheckRequest;
import com.backend.api.repository.aclpolicy.IAclPolicyRoleRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AclPolicyServiceImpl implements IAclPolicyService {

    private final IAclPolicyRoleRepo aclPolicyRoleRepo;

    /**
     * 특정 URI + RoleType 조합이 해당 HTTP Method를 허용하는지 검사
     * Redis 연결 실패 시 보수적으로 false를 반환하여 보안을 유지
     */
    @Override
    @Cacheable(cacheNames = "methodAllowed", key = "#request")
    @RedisFallback(fallbackMethod = "isMethodAllowedFallback")
    public boolean isMethodAllowed(AclCheckRequest request) {
        return aclPolicyRoleRepo.findAllowedMethods(request.getUri(), request.getRoleType())
                .map(methods -> Arrays.stream(methods.split(","))
                        .map(String::trim)
                        .anyMatch(m -> m.equalsIgnoreCase(request.getHttpMethod())))
                .orElse(false);
    }

    /**
     * isMethodAllowed 메서드의 Redis Fallback 처리 메서드
     * Redis 캐시가 실패한 경우 DB에서 직접 조회하여 결과 반환
     */
    public boolean isMethodAllowedFallback(AclCheckRequest request) {
        log.warn("🟡 Redis Fallback 실행 - DB에서 직접 ACL 정책 확인: {}", request);
        
        try {
            // Redis 없이 DB에서 직접 조회
            return aclPolicyRoleRepo.findAllowedMethods(request.getUri(), request.getRoleType())
                    .map(methods -> Arrays.stream(methods.split(","))
                            .map(String::trim)
                            .anyMatch(m -> m.equalsIgnoreCase(request.getHttpMethod())))
                    .orElse(false);
        } catch (Exception e) {
            log.error("🔴 DB 조회도 실패 - 보안을 위해 false 반환: {}", e.getMessage());
            // DB 조회마저 실패한 경우 보안을 위해 false 반환
            return false;
        }
    }
}