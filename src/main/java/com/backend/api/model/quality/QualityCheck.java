package com.backend.api.model.quality;

import com.backend.api.model.inventory.Product;
import com.backend.api.model.workorder.WorkOrder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "quality_checks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualityCheck {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String checkNumber; // 검사번호
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder; // 작업 지시서
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 검사 대상 제품
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckType checkType; // 검사 유형
    
    @Column(nullable = false)
    private Integer sampleSize; // 샘플 크기
    
    @Column(nullable = false)
    private Integer passedCount; // 합격 수량
    
    @Column(nullable = false)
    private Integer failedCount; // 불합격 수량
    
    @Column
    private Double passRate; // 합격률 (%)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QualityResult result; // 검사 결과
    
    @Column(nullable = false)
    private LocalDateTime checkDateTime; // 검사일시
    
    @Column(length = 100)
    private String inspector; // 검사자
    
    @Column(length = 100)
    private String equipment; // 검사 장비
    
    @Column(length = 1000)
    private String checkCriteria; // 검사 기준
    
    @Column(length = 1000)
    private String defectDetails; // 불량 상세내용
    
    @Column(length = 500)
    private String correctiveAction; // 시정조치
    
    @Column(length = 500)
    private String remarks; // 비고
    
    @Column(length = 100)
    private String batchNumber; // 배치번호
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum CheckType {
        INCOMING,        // 입고 검사
        IN_PROCESS,      // 공정 검사
        FINAL,           // 최종 검사
        OUTGOING,        // 출하 검사
        RANDOM,          // 무작위 검사
        CUSTOMER_COMPLAINT // 고객 불만 검사
    }
    
    public enum QualityResult {
        PASS,            // 합격
        FAIL,            // 불합격
        CONDITIONAL_PASS, // 조건부 합격
        REWORK_REQUIRED,  // 재작업 필요
        HOLD             // 보류
    }
} 