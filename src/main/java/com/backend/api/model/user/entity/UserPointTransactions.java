package com.backend.api.model.user.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_point_transactions")
public class UserPointTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trnId;
    private String userId;
    private Long pointCategoryId;
    private int point;

    @Enumerated(EnumType.STRING)
    private PointType type; // ENUM EARN, SPEND

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createDate;

    private LocalDateTime expireDate;
    private String assignedBy;
}