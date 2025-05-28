package com.backend.api.repository.role;

import com.backend.api.model.role.entity.RoleInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface IRoleInfoRepo extends JpaRepository<RoleInfo, Long>, JpaSpecificationExecutor<Object> {

    Page<RoleInfo> findAll(Pageable pageable);

    RoleInfo findByRoleId(int roleId);
}