package com.backend.api.service.aclpolicy;

import com.backend.api.model.aclpolicy.dto.AclCheckRequest;

public interface IAclPolicyService {
    boolean isMethodAllowed(AclCheckRequest request);
}