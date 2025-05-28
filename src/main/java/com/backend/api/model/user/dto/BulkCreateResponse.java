package com.backend.api.model.user.dto;

import java.util.List;

public class BulkCreateResponse {
    private int totalCount;
    private int successCount;
    private int failCount;
    private List<UserCreateResult> results;
}

