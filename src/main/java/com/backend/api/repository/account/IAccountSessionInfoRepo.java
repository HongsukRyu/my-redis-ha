package com.backend.api.repository.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import com.backend.api.model.account.entity.AccountSessionInfo;

@EnableJpaRepositories
public interface IAccountSessionInfoRepo extends JpaRepository<AccountSessionInfo, String>, JpaSpecificationExecutor<AccountSessionInfo> {

    Page<AccountSessionInfo> findAll(Pageable pageable);

    @Transactional
    void deleteByUserId(String userId);
}