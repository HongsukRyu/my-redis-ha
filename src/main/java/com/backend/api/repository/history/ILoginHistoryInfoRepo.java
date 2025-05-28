package com.backend.api.repository.history;

import com.backend.api.model.history.entity.LoginHistoryInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@EnableJpaRepositories
public interface ILoginHistoryInfoRepo extends JpaRepository<LoginHistoryInfo, Integer>, JpaSpecificationExecutor<Object> {
    Optional<LoginHistoryInfo> findById(int id);

    Page<LoginHistoryInfo> findAll(Pageable pageable);

/*    @Transactional
    void deleteById(int id);*/
}
