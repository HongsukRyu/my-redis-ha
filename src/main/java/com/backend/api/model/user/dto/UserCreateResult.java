package com.backend.api.model.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateResult {
    private String username;
    private boolean success;
    private String userId;     // 성공 시 포함
    private String errorCode;  // 실패 시 포함
    private String message;    // 실패 설명
}
