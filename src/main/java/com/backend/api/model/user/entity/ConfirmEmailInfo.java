package com.backend.api.model.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmEmailInfo {
    private String id;
    private String userEmail;
    private String code;
    private String enable;
    private Date timestamp;
}
