package com.backend.api.model.user.dto;

import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsersDto {

    private String userId;
    private int userGroupId;
    private Long type;
    private String name;
    private String email;
    private String encPassword;
    private String phone;
    private int status;
    private Date createDate;
    private Long totalPoint;
}
