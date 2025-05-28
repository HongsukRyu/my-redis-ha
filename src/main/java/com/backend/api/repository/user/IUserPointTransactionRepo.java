package com.backend.api.repository.user;

import com.backend.api.model.user.entity.UserPointTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@EnableJpaRepositories
public interface IUserPointTransactionRepo extends JpaRepository<UserPointTransactions, Long>, JpaSpecificationExecutor<Object> {

    UserPointTransactions findByTrnId(Long trnId);

    List<UserPointTransactions> findAllByUserId(String userId);

    @Transactional
    void deleteByUserId(String userId);

}