package com.backend.api.model.aclpolicy.dto;

import lombok.Data;

@Data
public class AclCheckRequest {
    private String uri;
    private String roleType;
    private String httpMethod;

    public AclCheckRequest(String uri, String stringRoleType, String method) {
        this.uri = uri;
        this.roleType = stringRoleType;
        this.httpMethod = method;
    }
}
