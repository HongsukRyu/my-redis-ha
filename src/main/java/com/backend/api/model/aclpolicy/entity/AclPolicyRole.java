package com.backend.api.model.aclpolicy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "acl_policy_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AclPolicyRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "policy_id")
    private AclPolicy policy;

    @Size(max = 50)
    @NotNull
    @Column(name = "role_type", nullable = false, length = 50)
    private String roleType;

    @Size(max = 255)
    @Column(name = "allowed_methods")
    private String allowedMethods;

}