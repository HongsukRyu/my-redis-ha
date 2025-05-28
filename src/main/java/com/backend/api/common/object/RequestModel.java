package com.backend.api.common.object;

import lombok.Data;

@Data
public class RequestModel {
    private String userId;
    private int type;
    private int userGroupId;
}
