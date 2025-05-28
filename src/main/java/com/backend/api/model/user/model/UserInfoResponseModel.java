package com.backend.api.model.user.model;

import lombok.Data;

import java.util.Date;

@Data
public class UserInfoResponseModel {
    private String userId;
    private String name;
    private String email;
    private String nicName;
    private String personalEmail;
    private String phone;
    private int status;
    private int type;
    private Date createDate;
    private int isOauth;
}