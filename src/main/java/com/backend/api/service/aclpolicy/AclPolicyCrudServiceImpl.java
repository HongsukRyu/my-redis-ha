package com.backend.api.service.aclpolicy;

import com.backend.api.model.aclpolicy.dto.AclPolicyRequest;
import com.backend.api.model.aclpolicy.entity.AclPolicy;
import com.backend.api.model.aclpolicy.entity.AclPolicyRole;
import com.backend.api.repository.aclpolicy.IAclPolicyRepo;
import com.backend.api.repository.aclpolicy.IAclPolicyRoleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AclPolicyCrudServiceImpl implements IAclPolicyCrudService {

    private final IAclPolicyRepo aclPolicyRepo;
    private final IAclPolicyRoleRepo aclPolicyRoleRepo;

    @Override
    public List<AclPolicy> getAllPolicies() {
        return aclPolicyRepo.findAll();
    }

    @Override
    public Optional<AclPolicy> getPolicyByUri(String uri) {
        return aclPolicyRepo.findByUri(uri);
    }

    @Transactional
    @Override
    public AclPolicy createOrUpdate(AclPolicyRequest request) {
        AclPolicy policy = aclPolicyRepo.findByUri(request.getUri()).orElseGet(() ->
                AclPolicy.builder().uri(request.getUri()).build()
        );

        // 기존 역할 정책 삭제 후 교체
        aclPolicyRoleRepo.deleteAllByPolicy_Uri(request.getUri());

        List<AclPolicyRole> roles = new ArrayList<>();
        for (Map.Entry<String, String> entry : request.getRolePolicies().entrySet()) {
            roles.add(AclPolicyRole.builder()
                    .policy(policy)
                    .roleType(entry.getKey())
                    .allowedMethods(entry.getValue())
                    .build());
        }

        policy.setRoles(roles);
        return aclPolicyRepo.save(policy);
    }

    @Transactional
    @Override
    public void deleteByUri(String uri) {
        aclPolicyRepo.findByUri(uri).ifPresent(policy -> {
            aclPolicyRoleRepo.deleteAllByPolicy_Uri(uri);
            aclPolicyRepo.delete(policy);
        });
    }
}