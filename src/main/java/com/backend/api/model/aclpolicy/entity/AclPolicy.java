package com.backend.api.model.aclpolicy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "acl_policy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AclPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "uri", nullable = false)
    private String uri;

    /**
     * 이 URI에 연결된 역할별 정책들
     */
    @Builder.Default
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AclPolicyRole> roles = new ArrayList<>();
//
//    // 양방향 연관관계 편의 메서드
//    public void addRole(AclPolicyRole role) {
//        roles.add(role);
//        role.setPolicy(this);
//    }
//
//    public void removeRole(AclPolicyRole role) {
//        roles.remove(role);
//        role.setPolicy(null);
//    }

}