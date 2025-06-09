package com.backend.api.model.workorder;

import com.backend.api.model.inventory.Product;
import com.backend.api.model.production.ProductionLine;
import com.backend.api.model.production.ProductionPlan;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String workOrderNumber; // 작업지시번호
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_plan_id", nullable = false)
    private ProductionPlan productionPlan; // 생산 계획
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 생산할 제품
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_line_id", nullable = false)
    private ProductionLine productionLine; // 생산 라인
    
    @Column(nullable = false)
    private Integer orderQuantity; // 지시 수량
    
    @Column
    private Integer completedQuantity; // 완료 수량
    
    @Column
    private Integer defectQuantity; // 불량 수량
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderStatus status; // 작업 지시 상태
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderPriority priority; // 우선순위
    
    @Column(nullable = false)
    private LocalDateTime scheduledStartTime; // 예정 시작시간
    
    @Column(nullable = false)
    private LocalDateTime scheduledEndTime; // 예정 완료시간
    
    @Column
    private LocalDateTime actualStartTime; // 실제 시작시간
    
    @Column
    private LocalDateTime actualEndTime; // 실제 완료시간
    
    @Column(length = 100)
    private String assignedOperator; // 담당 작업자
    
    @Column(length = 100)
    private String supervisor; // 감독자
    
    @Column(length = 1000)
    private String workInstructions; // 작업 지시사항
    
    @Column(length = 500)
    private String specialNotes; // 특별 주의사항
    
    @Column(length = 500)
    private String remarks; // 비고
    
    @Column(length = 100)
    private String createdBy; // 작성자
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum WorkOrderStatus {
        PENDING,         // 대기중
        RELEASED,        // 출고됨
        IN_PROGRESS,     // 진행중
        COMPLETED,       // 완료
        ON_HOLD,         // 보류
        CANCELLED        // 취소
    }
    
    public enum WorkOrderPriority {
        URGENT,          // 긴급
        HIGH,            // 높음
        NORMAL,          // 보통
        LOW              // 낮음
    }
} 