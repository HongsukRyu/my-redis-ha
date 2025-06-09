package com.backend.api.repository.inventory;

import com.backend.api.model.inventory.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    
    // 제품별 거래 이력 조회 (최신순)
    List<StockTransaction> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    // 거래 유형별 조회
    List<StockTransaction> findByTransactionType(StockTransaction.TransactionType transactionType);
    
    // 처리자별 조회
    List<StockTransaction> findByPerformedBy(String performedBy);
    
    // 기간별 거래 이력 조회
    @Query("SELECT st FROM StockTransaction st WHERE st.createdAt BETWEEN :startDate AND :endDate ORDER BY st.createdAt DESC")
    List<StockTransaction> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    // 참조번호로 조회
    List<StockTransaction> findByReferenceNumber(String referenceNumber);
    
    // 제품별 최근 거래 이력 조회 (지정된 개수만큼)
    @Query("SELECT st FROM StockTransaction st WHERE st.product.id = :productId ORDER BY st.createdAt DESC")
    List<StockTransaction> findTopNByProductId(@Param("productId") Long productId);
} 