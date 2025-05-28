package com.backend.api.repository.history;

import com.backend.api.model.history.entity.LoginTryHistoryInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@EnableJpaRepositories
public interface ILoginTryHistoryInfoRepo extends JpaRepository<LoginTryHistoryInfo, Integer>, JpaSpecificationExecutor<Object> {
    Optional<LoginTryHistoryInfo> findById(int id);

    Page<LoginTryHistoryInfo> findAll(Pageable pageable);

    @Transactional
    void deleteById(int id);
}
