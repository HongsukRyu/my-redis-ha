package com.backend.api.repository.user;

import com.backend.api.model.user.entity.UserInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@EnableJpaRepositories
public interface IUserInfoRepo extends JpaRepository<UserInfo, Long>, JpaSpecificationExecutor<Object> {

    Page<UserInfo> findAll(Pageable pageable);

    Page<UserInfo> findAllByUserGroupId(Pageable pageable, int userGroupId);

    UserInfo findByUserId(String userId);

    UserInfo findByEmail(String email);

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);

    @Transactional
    void deleteByUserId(String userId);

    @Query("select u.userId          as userId,\n" +
            "               u.name,\n" +
            "               u.email,\n" +
            "               u.encodedPassword as encPassword,\n" +
            "               u.type,\n" +
            "               u.phone,\n" +
            "               u.createDate      as createDate,\n" +
            "               u.status           as status,\n" +
            "               u.userGroup.id    as userGroupId \n" +
            "        from UserInfo u\n" +
            "                 left join UserGroupInfo ugi \n" +
            "                           on u.userGroup.id = ugi.id\n" +
            "        where u.email = :email ")
    UserInfo findUserDetailsByEmail(@Param("email") String email);
}