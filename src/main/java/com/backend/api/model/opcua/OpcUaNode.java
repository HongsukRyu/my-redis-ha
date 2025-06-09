package com.backend.api.model.opcua;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "opcua_nodes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpcUaNode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private OpcUaServer server;
    
    @Column(nullable = false)
    private String nodeName;
    
    @Column(nullable = false)
    private String nodeId;
    
    @Column(nullable = false)
    private Integer namespaceIndex;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NodeDataType dataType;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isSubscribed = false;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer samplingInterval = 1000; // milliseconds
    
    private String unit;
    private Double minValue;
    private Double maxValue;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum NodeDataType {
        BOOLEAN,
        BYTE,
        INT16,
        INT32,
        INT64,
        UINT16,
        UINT32,
        UINT64,
        FLOAT,
        DOUBLE,
        STRING,
        DATETIME,
        BYTESTRING
    }
} 