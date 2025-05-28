package com.backend.api.repository.aclpolicy;

import com.backend.api.model.aclpolicy.entity.AclPolicyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface IAclPolicyRoleRepo extends JpaRepository<AclPolicyRole, Long> {
    @Query("""
        SELECT r.allowedMethods 
        FROM AclPolicyRole r
        WHERE r.policy.uri = :uri AND r.roleType = :role
    """)
    Optional<String> findAllowedMethods(@Param("uri") String uri, @Param("role") String role);

    /**
     * URI + Role로 단일 AclPolicyRole 조회
     */
    Optional<AclPolicyRole> findByPolicy_UriAndRoleType(String uri, String roleType);

    /**
     * 특정 URI에 대한 모든 역할 정책 삭제
     */
    void deleteAllByPolicy_Uri(String uri);

    /**
     * 특정 URI에 연결된 모든 역할 정책 조회
     */
    List<AclPolicyRole> findAllByPolicy_Uri(String uri);

}