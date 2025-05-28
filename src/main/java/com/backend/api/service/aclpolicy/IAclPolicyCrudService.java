package com.backend.api.service.aclpolicy;

import com.backend.api.model.aclpolicy.dto.AclPolicyRequest;
import com.backend.api.model.aclpolicy.entity.AclPolicy;

import java.util.List;
import java.util.Optional;

public interface IAclPolicyCrudService {

    List<AclPolicy> getAllPolicies();

    Optional<AclPolicy> getPolicyByUri(String uri);

    AclPolicy createOrUpdate(AclPolicyRequest request);

    void deleteByUri(String uri);
}