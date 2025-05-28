package com.backend.api.repository.tenant;

import com.backend.api.model.tenant.entity.TenantInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface ITenantInfoRepo extends JpaRepository<TenantInfo, Long>, JpaSpecificationExecutor<Object> {
//    Page<TenantInfo> findAll(Pageable pageable);

    List<TenantInfo> findAll();

    Optional<TenantInfo> findByTenantId(String tenantId);

    @Transactional
    void deleteByTenantId(String tenantId);


}