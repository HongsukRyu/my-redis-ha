package com.backend.api.model.role.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Data
@Table(name="role_info", indexes = @Index(name="idx_RoleInfo_createDate", columnList = "createDate"))
public class RoleInfo {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="roleId")
    private int roleId;

    @Column(name="name", columnDefinition = "NVARCHAR(32)")
    private String name;

    @Column(name="description", columnDefinition = "NVARCHAR(32)")
    private String description;

    @Column(name="useYn", columnDefinition = "NVARCHAR(1)")
    private String useYn;

    @Column(name="prefix", columnDefinition = "NVARCHAR(10)")
    private String prefix;

    @Column(name = "createDate")
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(name = "updatedDate")
    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @Column(name="aclColumnName", columnDefinition = "NVARCHAR(50)")
    private String aclColumnName;
}