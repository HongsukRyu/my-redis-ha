package com.backend.api.model.aclpolicy.dto;

import lombok.Data;

import java.util.Map;

/**
 * uri + role 별 HTTP 메서드 정책
 */
@Data
public class AclPolicyRequest {
    private String uri;
    private Map<String, String> rolePolicies; // 예: {"admin": "GET,POST", "user": "GET"}
}
