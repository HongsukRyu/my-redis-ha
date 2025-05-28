package com.backend.api.model.user.entity;

import lombok.*;

import java.util.Date;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    private String userId;
    private int userGroupId;
    private Long type;
    private String name;
    private String email;
    private String encPassword;
    private String phone;
    private int status;
    private Date createDate;
}
