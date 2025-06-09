package com.backend.api.model.production;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "production_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionLine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String lineCode; // 라인 코드
    
    @Column(nullable = false, length = 200)
    private String lineName; // 라인명
    
    @Column(length = 500)
    private String description; // 설명
    
    @Column(length = 100)
    private String location; // 위치
    
    @Column(nullable = false)
    private Integer capacity; // 생산 능력 (시간당)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LineStatus status; // 라인 상태
    
    @Column(length = 100)
    private String supervisor; // 책임자
    
    @Column(length = 500)
    private String equipment; // 장비 정보
    
    @Column
    private Double efficiency; // 효율성 (%)
    
    @Column
    private LocalDateTime lastMaintenanceDate; // 마지막 정비일
    
    @Column
    private LocalDateTime nextMaintenanceDate; // 다음 정비 예정일
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum LineStatus {
        ACTIVE,          // 가동중
        IDLE,            // 대기중
        MAINTENANCE,     // 정비중
        BREAKDOWN,       // 고장
        SHUTDOWN         // 정지
    }
} 