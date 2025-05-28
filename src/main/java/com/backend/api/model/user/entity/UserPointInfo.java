package com.backend.api.model.user.entity;

import com.backend.api.model.user.dto.UserPointDto;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_point_info")
public class UserPointInfo {

    @Id
    private String userId;
    private Long totalPoint;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastUpdatedDate;

    public UserPointInfo() {

    }

    public static UserPointDto of(UserPointInfo entity) {
        return UserPointDto.builder()
                .userId(entity.getUserId())
                .totalPoint(entity.getTotalPoint())
                .lastUpdatedDate(entity.getLastUpdatedDate())
                .build();
    }
}