package com.backend.api.model.user.model;

import lombok.Data;

@Data
public class ConfirmPwdModel {

    private String userId;
    private String password;
}
