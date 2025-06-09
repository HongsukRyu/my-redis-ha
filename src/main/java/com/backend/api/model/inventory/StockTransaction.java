package com.backend.api.model.inventory;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType; // 거래 유형
    
    @Column(nullable = false)
    private Integer quantity; // 수량
    
    @Column(nullable = false)
    private Integer stockBefore; // 거래 전 재고
    
    @Column(nullable = false)
    private Integer stockAfter; // 거래 후 재고
    
    @Column(length = 500)
    private String reason; // 거래 사유
    
    @Column(length = 100)
    private String referenceNumber; // 참조번호 (주문번호, 작업지시번호 등)
    
    @Column(length = 100)
    private String performedBy; // 처리자
    
    @Column(length = 200)
    private String remarks; // 비고
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    public enum TransactionType {
        STOCK_IN,        // 입고
        STOCK_OUT,       // 출고
        ADJUSTMENT,      // 재고 조정
        RETURN,          // 반품
        TRANSFER,        // 이동
        LOSS,            // 손실
        PRODUCTION_IN,   // 생산 입고
        PRODUCTION_OUT   // 생산 출고
    }
} 