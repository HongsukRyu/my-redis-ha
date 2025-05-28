package com.backend.api.model.tenant.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="allowed_tenant_info")
public class AllowedTenantInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="tenant_id", columnDefinition = "NVARCHAR(50)")
    private String tenantId;

    @Column(name="user_group_id")
    private Integer userGroupId;

    @CreationTimestamp
    @Column(name="created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name="updated_date")
    private LocalDateTime updatedDate;
}