package com.backend.api.model.tenant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.backend.api.model.tenant.entity.TenantInfo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AllowedTenantInfoDto {

    private Long id;
    private String tenantId;
    private String tenantName;
//    private Integer userGroupId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedDate;

    public void setTenantName(TenantInfo tenantInfo) {
        this.tenantName = tenantInfo.getTenantName();
    }
}