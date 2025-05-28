package com.backend.api.repository.user;

import com.backend.api.model.user.entity.UserPointInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;


@EnableJpaRepositories
public interface IUserPointRepo extends JpaRepository<UserPointInfo, Long>, JpaSpecificationExecutor<Object> {

    UserPointInfo findByUserId(String userId);

    @Transactional
    void deleteByUserId(String userId);

}