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
@Table(name="login_history_info", indexes = @Index(name="idx_LoginHistoryInfo_createDate", columnList = "create_date"))
public class LoginHistoryInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
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
