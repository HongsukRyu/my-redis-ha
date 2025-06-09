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
@Table(name = "opcua_servers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpcUaServer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String serverName;
    
    @Column(nullable = false)
    private String endpointUrl;
    
    private String username;
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ServerStatus status = ServerStatus.DISCONNECTED;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer connectionTimeout = 5000; // milliseconds
    
    @Column(nullable = false)
    @Builder.Default
    private Integer requestTimeout = 10000; // milliseconds
    
    @Column(nullable = false)
    @Builder.Default
    private Integer subscriptionInterval = 1000; // milliseconds
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private LocalDateTime lastConnectedAt;
    
    public enum ServerStatus {
        CONNECTED,
        DISCONNECTED,
        CONNECTING,
        ERROR
    }
} 