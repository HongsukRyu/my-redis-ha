package com.backend.api.model.production;

import com.backend.api.model.inventory.Product;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "production_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String planCode; // 계획 코드
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 생산할 제품
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_line_id", nullable = false)
    private ProductionLine productionLine; // 생산 라인
    
    @Column(nullable = false)
    private Integer plannedQuantity; // 계획 수량
    
    @Column(nullable = false)
    private LocalDate plannedStartDate; // 계획 시작일
    
    @Column(nullable = false)
    private LocalDate plannedEndDate; // 계획 완료일
    
    @Column
    private LocalDate actualStartDate; // 실제 시작일
    
    @Column
    private LocalDate actualEndDate; // 실제 완료일
    
    @Column
    private Integer actualQuantity; // 실제 생산량
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status; // 계획 상태
    
    @Column(nullable = false)
    private Integer priority; // 우선순위 (1: 높음, 5: 낮음)
    
    @Column(length = 100)
    private String customerOrder; // 고객 주문번호
    
    @Column(length = 500)
    private String notes; // 비고
    
    @Column(length = 100)
    private String plannerName; // 계획자명
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum PlanStatus {
        DRAFT,           // 초안
        APPROVED,        // 승인됨
        IN_PROGRESS,     // 진행중
        COMPLETED,       // 완료
        CANCELLED,       // 취소
        ON_HOLD          // 보류
    }
} 