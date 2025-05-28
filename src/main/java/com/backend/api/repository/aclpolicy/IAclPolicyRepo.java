package com.backend.api.repository.aclpolicy;

import com.backend.api.model.aclpolicy.entity.AclPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IAclPolicyRepo extends JpaRepository<AclPolicy, Long> {
    Optional<AclPolicy> findByUri(String uri);
}