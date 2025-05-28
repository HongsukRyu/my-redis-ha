package com.backend.api.repository.user;

import com.backend.api.model.user.entity.UserGroupInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaRepositories
public interface IUserGroupInfoRepo extends JpaRepository<UserGroupInfo, Long>, JpaSpecificationExecutor<Object> {

    Page<UserGroupInfo> findAll(Pageable pageable);

    UserGroupInfo findById(Integer id);

    @Transactional
    void deleteById(Integer id);

}