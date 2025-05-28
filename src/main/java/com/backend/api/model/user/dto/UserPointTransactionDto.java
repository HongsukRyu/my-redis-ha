package com.backend.api.model.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.backend.api.model.user.entity.PointType;
import lombok.*;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserPointTransactionDto {

    private Long trnId;
    private String userId;
    private Long pointCategoryId;
    private String categoryName;
    private int point;
    @Enumerated(EnumType.STRING)
    private PointType type; // ENUM EARN, SPEND
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createDate;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime expireDate;
    private String assignedBy;
}
