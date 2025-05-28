package com.backend.api.repository.tenant;

import com.backend.api.model.tenant.entity.AllowedTenantInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface IAllowedTenantInfoRepo extends JpaRepository<AllowedTenantInfo, Long>, JpaSpecificationExecutor<Object> {
    Page<AllowedTenantInfo> findAll(Pageable pageable);

    Optional<AllowedTenantInfo> findById(Long id);

    List<AllowedTenantInfo> findAllByUserGroupId(int userGroupId);

    @Transactional
    void deleteById(Long id);

}