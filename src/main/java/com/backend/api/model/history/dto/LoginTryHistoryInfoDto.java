package com.backend.api.model.history.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Login Try history entity to DTO conversion for a spring REST API
 *
 */

@Data
public class LoginTryHistoryInfoDto {
    private int id;
    private String userId;
    private Long userType;
    private String userEmail;
    private String clientIp;
//    private String clientDevice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;
}
