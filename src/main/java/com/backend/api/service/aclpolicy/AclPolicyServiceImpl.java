package com.backend.api.service.aclpolicy;

import java.util.Arrays;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
     */
    @Override
    @Cacheable(cacheNames = "methodAllowed", key = "#request")
    public boolean isMethodAllowed(AclCheckRequest request) {
        return aclPolicyRoleRepo.findAllowedMethods(request.getUri(), request.getRoleType())
                .map(methods -> Arrays.stream(methods.split(","))
                        .map(String::trim)
                        .anyMatch(m -> m.equalsIgnoreCase(request.getHttpMethod())))
                .orElse(false);
    }
}