package com.backend.api.model.tenant.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="tenant_info")
public class TenantInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="tenant_id", columnDefinition = "NVARCHAR(10)")
    private String tenantId;

    @Column(name="tenant_name", columnDefinition = "NVARCHAR(50)")
    private String tenantName;

    @Column(name="tenant_desc", columnDefinition = "NVARCHAR(100)")
    private String tenantDesc;

    @Size(max = 1)
    @NotNull
    @Column(name = "use_yn", nullable = false, length = 1)
    private String useYn;

    @CreationTimestamp
    @Column(name="create_date")
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(name="updated_date")
    private LocalDateTime updatedDate;
}