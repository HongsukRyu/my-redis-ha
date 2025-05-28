package com.backend.api.model.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.backend.api.model.role.entity.RoleInfo;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    @Size(max = 50)
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_group_id")
    private UserGroupInfo userGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type", nullable = false)
    private RoleInfo type;

    @Size(max = 32)
    @NotNull
    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Size(max = 50)
    @NotNull
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Size(max = 100)
    @Column(name = "encoded_password", length = 100)
    private String encodedPassword;

    @Size(max = 50)
    @ColumnDefault("'010-0000-0000'")
    @Column(name = "phone", length = 50)
    private String phone;

    @ColumnDefault("0")
    @Column(name = "status")
    private Integer status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "create_date")
    private Instant createDate;

    @Column(name = "expire_date")
    private Instant expireDate;

    @Size(max = 255)
    @Column(name = "expire_yn")
    private String expireYn;

}