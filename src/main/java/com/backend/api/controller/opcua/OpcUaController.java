package com.backend.api.controller.opcua;

import com.backend.api.model.opcua.OpcUaDataPoint;
import com.backend.api.model.opcua.OpcUaNode;
import com.backend.api.model.opcua.OpcUaServer;
import com.backend.api.service.opcua.OpcUaManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/opcua")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OPC-UA Management", description = "OPC-UA 서버 연결 및 데이터 수집 관리")
public class OpcUaController {
    
    private final OpcUaManagementService opcUaService;
    
    // ==================== Server Management ====================
    
    @Operation(summary = "OPC-UA 서버 목록 조회", description = "등록된 모든 OPC-UA 서버 목록을 조회합니다.")
    @GetMapping("/servers")
    public ResponseEntity<List<OpcUaServer>> getAllServers() {
        List<OpcUaServer> servers = opcUaService.getAllServers();
        return ResponseEntity.ok(servers);
    }
    
    @Operation(summary = "OPC-UA 서버 상세 조회", description = "특정 OPC-UA 서버의 상세 정보를 조회합니다.")
    @GetMapping("/servers/{serverId}")
    public ResponseEntity<OpcUaServer> getServerById(
            @Parameter(description = "서버 ID") @PathVariable Long serverId) {
        Optional<OpcUaServer> server = opcUaService.getServerById(serverId);
        return server.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "OPC-UA 서버 생성", description = "새로운 OPC-UA 서버를 등록합니다.")
    @PostMapping("/servers")
    public ResponseEntity<OpcUaServer> createServer(@RequestBody OpcUaServer server) {
        try {
            OpcUaServer createdServer = opcUaService.createServer(server);
            return ResponseEntity.ok(createdServer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "OPC-UA 서버 수정", description = "기존 OPC-UA 서버 정보를 수정합니다.")
    @PutMapping("/servers/{serverId}")
    public ResponseEntity<OpcUaServer> updateServer(
            @Parameter(description = "서버 ID") @PathVariable Long serverId,
            @RequestBody OpcUaServer server) {
        try {
            OpcUaServer updatedServer = opcUaService.updateServer(serverId, server);
            return ResponseEntity.ok(updatedServer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "OPC-UA 서버 삭제", description = "OPC-UA 서버를 삭제합니다.")
    @DeleteMapping("/servers/{serverId}")
    public ResponseEntity<Void> deleteServer(
            @Parameter(description = "서버 ID") @PathVariable Long serverId) {
        try {
            opcUaService.deleteServer(serverId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "활성 서버 목록 조회", description = "활성화된 OPC-UA 서버 목록을 조회합니다.")
    @GetMapping("/servers/active")
    public ResponseEntity<List<OpcUaServer>> getActiveServers() {
        List<OpcUaServer> servers = opcUaService.getActiveServers();
        return ResponseEntity.ok(servers);
    }
    
    // ==================== Connection Management ====================
    
    @Operation(summary = "서버 연결", description = "OPC-UA 서버에 연결합니다.")
    @PostMapping("/servers/{serverId}/connect")
    public ResponseEntity<Map<String, String>> connectToServer(
            @Parameter(description = "서버 ID") @PathVariable Long serverId) {
        try {
            opcUaService.connectToServer(serverId);
            return ResponseEntity.ok(Map.of("message", "Connection initiated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "서버 연결 해제", description = "OPC-UA 서버 연결을 해제합니다.")
    @PostMapping("/servers/{serverId}/disconnect")
    public ResponseEntity<Map<String, String>> disconnectFromServer(
            @Parameter(description = "서버 ID") @PathVariable Long serverId) {
        opcUaService.disconnectFromServer(serverId);
        return ResponseEntity.ok(Map.of("message", "Disconnected"));
    }
    
    @Operation(summary = "서버 연결 상태 확인", description = "OPC-UA 서버의 연결 상태를 확인합니다.")
    @GetMapping("/servers/{serverId}/status")
    public ResponseEntity<Map<String, Object>> getServerConnectionStatus(
            @Parameter(description = "서버 ID") @PathVariable Long serverId) {
        boolean isConnected = opcUaService.isServerConnected(serverId);
        return ResponseEntity.ok(Map.of(
            "serverId", serverId,
            "connected", isConnected
        ));
    }
    
    @Operation(summary = "전체 연결 상태", description = "모든 활성 연결의 상태를 조회합니다.")
    @GetMapping("/connections/status")
    public ResponseEntity<Map<String, Object>> getConnectionStatus() {
        int activeConnections = opcUaService.getActiveConnectionCount();
        return ResponseEntity.ok(Map.of(
            "activeConnections", activeConnections,
            "timestamp", LocalDateTime.now()
        ));
    }
    
    // ==================== Node Management ====================
    
    @Operation(summary = "서버의 노드 목록 조회", description = "특정 서버에 등록된 노드 목록을 조회합니다.")
    @GetMapping("/servers/{serverId}/nodes")
    public ResponseEntity<List<OpcUaNode>> getNodesByServer(
            @Parameter(description = "서버 ID") @PathVariable Long serverId) {
        try {
            List<OpcUaNode> nodes = opcUaService.getNodesByServer(serverId);
            return ResponseEntity.ok(nodes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "노드 생성", description = "새로운 OPC-UA 노드를 등록합니다.")
    @PostMapping("/servers/{serverId}/nodes")
    public ResponseEntity<OpcUaNode> createNode(
            @Parameter(description = "서버 ID") @PathVariable Long serverId,
            @RequestBody OpcUaNode node) {
        try {
            Optional<OpcUaServer> server = opcUaService.getServerById(serverId);
            if (server.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            node.setServer(server.get());
            OpcUaNode createdNode = opcUaService.createNode(node);
            return ResponseEntity.ok(createdNode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "노드 수정", description = "기존 OPC-UA 노드 정보를 수정합니다.")
    @PutMapping("/nodes/{nodeId}")
    public ResponseEntity<OpcUaNode> updateNode(
            @Parameter(description = "노드 ID") @PathVariable Long nodeId,
            @RequestBody OpcUaNode node) {
        try {
            OpcUaNode updatedNode = opcUaService.updateNode(nodeId, node);
            return ResponseEntity.ok(updatedNode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "노드 삭제", description = "OPC-UA 노드를 삭제합니다.")
    @DeleteMapping("/nodes/{nodeId}")
    public ResponseEntity<Void> deleteNode(
            @Parameter(description = "노드 ID") @PathVariable Long nodeId) {
        try {
            opcUaService.deleteNode(nodeId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "노드 구독 토글", description = "노드의 구독 상태를 토글합니다.")
    @PostMapping("/nodes/{nodeId}/toggle-subscription")
    public ResponseEntity<OpcUaNode> toggleNodeSubscription(
            @Parameter(description = "노드 ID") @PathVariable Long nodeId) {
        try {
            OpcUaNode node = opcUaService.toggleNodeSubscription(nodeId);
            return ResponseEntity.ok(node);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "구독 중인 노드 목록", description = "현재 구독 중인 모든 노드 목록을 조회합니다.")
    @GetMapping("/nodes/subscribed")
    public ResponseEntity<List<OpcUaNode>> getSubscribedNodes() {
        List<OpcUaNode> nodes = opcUaService.getSubscribedNodes();
        return ResponseEntity.ok(nodes);
    }
    
    // ==================== Data Point Management ====================
    
    @Operation(summary = "노드 데이터 조회", description = "특정 노드의 데이터 포인트를 조회합니다.")
    @GetMapping("/nodes/{nodeId}/data")
    public ResponseEntity<Page<OpcUaDataPoint>> getDataPointsByNode(
            @Parameter(description = "노드 ID") @PathVariable Long nodeId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<OpcUaDataPoint> dataPoints = opcUaService.getDataPointsByNode(nodeId, pageable);
            return ResponseEntity.ok(dataPoints);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "시간 범위별 노드 데이터 조회", description = "특정 시간 범위의 노드 데이터를 조회합니다.")
    @GetMapping("/nodes/{nodeId}/data/range")
    public ResponseEntity<List<OpcUaDataPoint>> getDataPointsByNodeAndTimeRange(
            @Parameter(description = "노드 ID") @PathVariable Long nodeId,
            @Parameter(description = "시작 시간") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "종료 시간") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<OpcUaDataPoint> dataPoints = opcUaService.getDataPointsByNodeAndTimeRange(nodeId, startTime, endTime);
            return ResponseEntity.ok(dataPoints);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "최신 데이터 조회", description = "노드의 최신 데이터 포인트를 조회합니다.")
    @GetMapping("/nodes/{nodeId}/data/latest")
    public ResponseEntity<OpcUaDataPoint> getLatestDataPoint(
            @Parameter(description = "노드 ID") @PathVariable Long nodeId) {
        try {
            Optional<OpcUaDataPoint> dataPoint = opcUaService.getLatestDataPoint(nodeId);
            return dataPoint.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "서버 데이터 조회", description = "특정 서버의 모든 노드 데이터를 시간 범위로 조회합니다.")
    @GetMapping("/servers/{serverId}/data/range")
    public ResponseEntity<List<OpcUaDataPoint>> getDataPointsByServerAndTimeRange(
            @Parameter(description = "서버 ID") @PathVariable Long serverId,
            @Parameter(description = "시작 시간") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "종료 시간") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<OpcUaDataPoint> dataPoints = opcUaService.getDataPointsByServerAndTimeRange(serverId, startTime, endTime);
        return ResponseEntity.ok(dataPoints);
    }
    
    // ==================== Statistics ====================
    
    @Operation(summary = "노드 데이터 통계", description = "노드의 데이터 수집 통계를 조회합니다.")
    @GetMapping("/nodes/{nodeId}/statistics")
    public ResponseEntity<Map<String, Object>> getNodeStatistics(
            @Parameter(description = "노드 ID") @PathVariable Long nodeId,
            @Parameter(description = "기준 시간") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        try {
            LocalDateTime sinceTime = since != null ? since : LocalDateTime.now().minusHours(24);
            long dataPointCount = opcUaService.getDataPointCount(nodeId, sinceTime);
            
            return ResponseEntity.ok(Map.of(
                "nodeId", nodeId,
                "dataPointCount", dataPointCount,
                "since", sinceTime,
                "generatedAt", LocalDateTime.now()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "서버 노드 통계", description = "서버의 구독 노드 통계를 조회합니다.")
    @GetMapping("/servers/{serverId}/statistics")
    public ResponseEntity<Map<String, Object>> getServerStatistics(
            @Parameter(description = "서버 ID") @PathVariable Long serverId) {
        try {
            long subscribedNodeCount = opcUaService.getSubscribedNodeCountByServer(serverId);
            boolean isConnected = opcUaService.isServerConnected(serverId);
            
            return ResponseEntity.ok(Map.of(
                "serverId", serverId,
                "subscribedNodeCount", subscribedNodeCount,
                "isConnected", isConnected,
                "generatedAt", LocalDateTime.now()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ==================== Maintenance ====================
    
    @Operation(summary = "오래된 데이터 정리", description = "지정된 시간 이전의 데이터 포인트를 삭제합니다.")
    @DeleteMapping("/data/cleanup")
    public ResponseEntity<Map<String, String>> cleanupOldDataPoints(
            @Parameter(description = "삭제할 기준 시간") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
        opcUaService.cleanupOldDataPoints(before);
        return ResponseEntity.ok(Map.of(
            "message", "Data cleanup completed",
            "deletedBefore", before.toString()
        ));
    }
} 