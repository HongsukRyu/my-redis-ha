package com.backend.api.model.user.model;

import lombok.Data;

@Data
public class LoginRequestModel {
    private String userId;
    private String password;
    private String email;
    private int type;
    private String accessToken;
}
