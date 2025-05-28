package com.backend.api.repository.user;

import com.backend.api.model.user.entity.UserPointCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface IUserPointCategoryRepo extends JpaRepository<UserPointCategory, Long>, JpaSpecificationExecutor<Object> {

    UserPointCategory findByCategoryId(Long categoryId);

}