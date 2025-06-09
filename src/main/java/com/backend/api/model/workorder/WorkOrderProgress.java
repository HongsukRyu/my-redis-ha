package com.backend.api.model.workorder;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_order_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;
    
    @Column(nullable = false)
    private LocalDateTime reportedAt; // 보고 시간
    
    @Column(nullable = false)
    private Integer quantityProduced; // 생산량
    
    @Column
    private Integer quantityDefect; // 불량량
    
    @Column
    private Double progressPercentage; // 진행률 (%)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus status; // 진행 상태
    
    @Column(length = 100)
    private String reportedBy; // 보고자
    
    @Column(length = 500)
    private String issues; // 문제사항
    
    @Column(length = 500)
    private String comments; // 의견
    
    @Column
    private Double setupTime; // 셋업 시간 (분)
    
    @Column
    private Double runTime; // 가동 시간 (분)
    
    @Column
    private Double downTime; // 중단 시간 (분)
    
    @Column(length = 500)
    private String downTimeReason; // 중단 사유
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    public enum ProgressStatus {
        ON_TRACK,        // 정상 진행
        DELAYED,         // 지연
        AHEAD,           // 앞서감
        QUALITY_ISSUE,   // 품질 문제
        EQUIPMENT_ISSUE, // 장비 문제
        MATERIAL_SHORTAGE // 자재 부족
    }
} 