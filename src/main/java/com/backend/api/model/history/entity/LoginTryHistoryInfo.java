package com.backend.api.model.history.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Getter
@NoArgsConstructor
@Entity
@Table(name="login_try_history_info", indexes = @Index(name="idx_LoginTryHistoryInfo_createDate", columnList = "create_date"))
public class LoginTryHistoryInfo {

    @Id
    @Column(name="id", columnDefinition = "INTEGER")
    private int id;

    @Column(name="user_id", columnDefinition = "NVARCHAR(32)")
    private String userId;

    @Column(name="user_email", columnDefinition = "NVARCHAR(64)")
    private String userEmail;

    @Column(name="user_type", columnDefinition = "BIGINT")
    private Long userType;

    @Column(name="client_ip", columnDefinition = "NVARCHAR(200)")
    private String clientIp;

    @Column(name = "create_date")
    @CreationTimestamp
    private LocalDateTime createDate;
}
