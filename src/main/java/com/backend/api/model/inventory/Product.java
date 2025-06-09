package com.backend.api.model.inventory;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String productCode; // 제품 코드
    
    @Column(nullable = false, length = 200)
    private String productName; // 제품명
    
    @Column(length = 500)
    private String description; // 제품 설명
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category; // 제품 카테고리
    
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal unitPrice; // 단가
    
    @Column(length = 20)
    private String unit; // 단위 (개, kg, m 등)
    
    @Column(nullable = false)
    private Integer minStockLevel; // 최소 재고 수준
    
    @Column(nullable = false)
    private Integer maxStockLevel; // 최대 재고 수준
    
    @Column(nullable = false)
    private Integer currentStock; // 현재 재고량
    
    @Column(nullable = false)
    private Integer reorderPoint; // 재주문 시점
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status; // 제품 상태
    
    @Column(length = 100)
    private String supplier; // 공급업체
    
    @Column(length = 50)
    private String location; // 저장 위치
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum ProductCategory {
        RAW_MATERIAL,    // 원자재
        SEMI_FINISHED,   // 반제품
        FINISHED_GOODS,  // 완제품
        CONSUMABLES,     // 소모품
        SPARE_PARTS      // 부품
    }
    
    public enum ProductStatus {
        ACTIVE,          // 활성
        INACTIVE,        // 비활성
        DISCONTINUED,    // 단종
        OUT_OF_STOCK     // 재고없음
    }
} 