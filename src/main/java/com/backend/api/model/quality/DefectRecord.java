package com.backend.api.model.quality;

import com.backend.api.model.inventory.Product;
import com.backend.api.model.workorder.WorkOrder;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "defect_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefectRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quality_check_id", nullable = false)
    private QualityCheck qualityCheck; // 품질 검사
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder; // 작업 지시서
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 제품
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DefectType defectType; // 불량 유형
    
    @Column(nullable = false, length = 200)
    private String defectDescription; // 불량 설명
    
    @Column(nullable = false)
    private Integer defectQuantity; // 불량 수량
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity; // 심각도
    
    @Column(length = 100)
    private String rootCause; // 근본 원인
    
    @Column(length = 500)
    private String correctiveAction; // 시정 조치
    
    @Column(length = 500)
    private String preventiveAction; // 예방 조치
    
    @Column(length = 100)
    private String detectedBy; // 발견자
    
    @Column(nullable = false)
    private LocalDateTime detectedAt; // 발견일시
    
    @Column(length = 100)
    private String assignedTo; // 담당자
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DefectStatus status; // 불량 처리 상태
    
    @Column
    private LocalDateTime resolvedAt; // 해결일시
    
    @Column(length = 100)
    private String resolvedBy; // 해결자
    
    @Column(length = 500)
    private String remarks; // 비고
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    public enum DefectType {
        DIMENSIONAL,     // 치수 불량
        VISUAL,          // 외관 불량
        FUNCTIONAL,      // 기능 불량
        MATERIAL,        // 재질 불량
        ASSEMBLY,        // 조립 불량
        PACKAGING,       // 포장 불량
        CONTAMINATION,   // 오염
        DAMAGE           // 손상
    }
    
    public enum Severity {
        CRITICAL,        // 치명적
        MAJOR,           // 주요
        MINOR,           // 경미
        COSMETIC         // 외관상
    }
    
    public enum DefectStatus {
        REPORTED,        // 보고됨
        INVESTIGATING,   // 조사중
        IN_PROGRESS,     // 처리중
        RESOLVED,        // 해결됨
        CLOSED           // 종료
    }
} 