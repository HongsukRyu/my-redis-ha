package com.backend.api.model.opcua;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "opcua_data_points", indexes = {
    @Index(name = "idx_node_timestamp", columnList = "node_id, timestamp"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpcUaDataPoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false)
    private OpcUaNode node;
    
    @Column(nullable = false)
    private String value;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataQuality quality;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    private LocalDateTime sourceTimestamp;
    
    @Column(nullable = false)
    private LocalDateTime serverTimestamp;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    // 숫자형 데이터를 위한 필드
    private Double numericValue;
    
    // 부울형 데이터를 위한 필드
    private Boolean booleanValue;
    
    // 원시 바이트 데이터를 위한 필드
    @Lob
    private byte[] rawData;
    
    public enum DataQuality {
        GOOD,
        UNCERTAIN,
        BAD,
        UNKNOWN
    }
} 