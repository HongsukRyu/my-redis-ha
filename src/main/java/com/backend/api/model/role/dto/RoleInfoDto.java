package com.backend.api.model.role.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RoleInfo entity to DTO conversion for a spring REST API
 *
 * @author hs.ryu
 * @since 2023.04
 */

@Data
public class RoleInfoDto {

    private int id;
    private String domain;
    private String roleId;
    private String roleName;
    private String description;
    private String useYn;
    private String prefix;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedDate;
    private String aclColumnName;
}