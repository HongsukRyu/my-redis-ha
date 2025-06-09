package com.backend.api.service.opcua;

import com.backend.api.model.opcua.OpcUaDataPoint;
import com.backend.api.model.opcua.OpcUaNode;
import com.backend.api.model.opcua.OpcUaServer;
import com.backend.api.repository.opcua.OpcUaDataPointRepository;
import com.backend.api.repository.opcua.OpcUaNodeRepository;
import com.backend.api.repository.opcua.OpcUaServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.sdk.client.api.UaSession;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpcUaClientService {
    
    private final OpcUaServerRepository serverRepository;
    private final OpcUaNodeRepository nodeRepository;
    private final OpcUaDataPointRepository dataPointRepository;
    
    // 활성 클라이언트 연결 관리
    private final Map<Long, OpcUaClient> activeClients = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> connectionStatus = new ConcurrentHashMap<>();
    private final Map<Long, UaSubscription> subscriptions = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void initializeConnections() {
        log.info("Initializing OPC-UA client connections...");
        List<OpcUaServer> activeServers = serverRepository.findByIsActiveTrue();
        for (OpcUaServer server : activeServers) {
            connectToServerAsync(server);
        }
    }
    
    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up OPC-UA client connections...");
        for (Map.Entry<Long, OpcUaClient> entry : activeClients.entrySet()) {
            disconnectFromServer(entry.getKey());
        }
    }
    
    @Async("opcUaTaskExecutor")
    public CompletableFuture<Boolean> connectToServerAsync(OpcUaServer server) {
        try {
            log.info("Connecting to OPC-UA server: {}", server.getServerName());
            
            // 서버 상태를 CONNECTING으로 업데이트
            updateServerStatus(server.getId(), OpcUaServer.ServerStatus.CONNECTING);
            
            // Eclipse Milo 클라이언트 생성 및 연결
            OpcUaClient client = createOpcUaClient(server);
            client.connect().get();
            
            activeClients.put(server.getId(), client);
            connectionStatus.put(server.getId(), true);
            updateServerStatus(server.getId(), OpcUaServer.ServerStatus.CONNECTED);
            
            // 구독 설정
            setupSubscriptions(server, client);
            
            log.info("Successfully connected to OPC-UA server: {}", server.getServerName());
            return CompletableFuture.completedFuture(true);
            
        } catch (Exception e) {
            log.error("Error connecting to OPC-UA server {}: {}", server.getServerName(), e.getMessage(), e);
            updateServerStatus(server.getId(), OpcUaServer.ServerStatus.ERROR);
            return CompletableFuture.completedFuture(false);
        }
    }
    
    public void disconnectFromServer(Long serverId) {
        try {
            OpcUaClient client = activeClients.remove(serverId);
            connectionStatus.put(serverId, false);
            
            // 구독 정리
            UaSubscription subscription = subscriptions.remove(serverId);
            if (subscription != null) {
                try {
                    OpcUaClient client1 = activeClients.get(serverId);
                    if (client1 != null) {
                        client1.getSubscriptionManager()
                                .deleteSubscription(subscription.getSubscriptionId()).get();
                    }
                    log.info("Subscription deleted");

                } catch (Exception e) {
                    log.warn("Error deleting subscription for server {}: {}", serverId, e.getMessage());
                }
            }

            if (client != null) {
                try {
                    client.disconnect().get();
                    log.info("Disconnected from OPC-UA server ID: {}", serverId);
                } catch (Exception e) {
                    log.warn("Error during disconnect for server {}: {}", serverId, e.getMessage());
                }
            }
            
            updateServerStatus(serverId, OpcUaServer.ServerStatus.DISCONNECTED);
            
        } catch (Exception e) {
            log.error("Error disconnecting from OPC-UA server {}: {}", serverId, e.getMessage(), e);
        }
    }
    
    private OpcUaClient createOpcUaClient(OpcUaServer server) throws Exception {
        // 엔드포인트 검색
        List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(server.getEndpointUrl()).get();
        
        // 보안 정책에 따라 엔드포인트 선택 (일단 가장 안전한 것 또는 None 선택)
        EndpointDescription endpoint = endpoints.stream()
                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                .findFirst()
                .orElse(endpoints.get(0));
        
        log.info("Selected endpoint: {} with security policy: {}", 
                endpoint.getEndpointUrl(), endpoint.getSecurityPolicyUri());
        
        // 클라이언트 설정 구성 (기본 접근법 사용)
        // 현재 Eclipse Milo 라이브러리 로딩 문제로 인해 기본 클라이언트 생성
        // TODO: 의존성 문제 해결 후 전체 설정 적용
        
        return OpcUaClient.create(endpoint.getEndpointUrl());
    }
    
    private void setupSubscriptions(OpcUaServer server, OpcUaClient client) {
        List<OpcUaNode> subscribedNodes = nodeRepository.findActiveSubscribedNodesByServer(server);
        
        if (subscribedNodes.isEmpty()) {
            log.debug("No subscribed nodes found for server: {}", server.getServerName());
            return;
        }
        
        try {
            // 구독 생성
            UaSubscription subscription = client.getSubscriptionManager()
                    .createSubscription(server.getSubscriptionInterval().doubleValue()).get();
            
            subscriptions.put(server.getId(), subscription);
            
            // 모니터링 아이템 생성
            for (OpcUaNode node : subscribedNodes) {
                createMonitoredItem(subscription, node);
            }
            
            log.info("Created subscription with {} monitored items for server: {}", 
                    subscribedNodes.size(), server.getServerName());
            
        } catch (Exception e) {
            log.error("Error setting up subscriptions for server {}: {}", 
                     server.getServerName(), e.getMessage(), e);
        }
    }
    
    private void createMonitoredItem(UaSubscription subscription, OpcUaNode node) {
        try {
            // NodeId 생성
            NodeId nodeId = new NodeId(node.getNamespaceIndex(), node.getNodeId());
            
            // ReadValueId 생성
            ReadValueId readValueId = new ReadValueId(
                    nodeId,
                    AttributeId.Value.uid(),
                    null,
                    QualifiedName.NULL_VALUE
            );
            
            // 모니터링 파라미터 설정
            MonitoringParameters parameters = new MonitoringParameters(
                    UInteger.valueOf(node.getId()), // Client Handle
                    node.getSamplingInterval().doubleValue(), // Sampling Interval
                    null, // Filter
                    UInteger.valueOf(10), // Queue Size
                    true // Discard Oldest
            );
            
            // 모니터링 아이템 생성 요청
            MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(
                    readValueId,
                    MonitoringMode.Reporting,
                    parameters
            );
            
            // 모니터링 아이템 생성
            List<UaMonitoredItem> items = subscription.createMonitoredItems(
                    TimestampsToReturn.Both,
                    List.of(request)
            ).get();
            
            // 값 변경 콜백 설정 (Consumer<DataValue> 사용)
            if (!items.isEmpty() && items.get(0).getStatusCode().isGood()) {
                items.get(0).setValueConsumer(value -> {
                    handleDataValueChange(node, value);
                });
            }
            
            if (items.get(0).getStatusCode().isGood()) {
                log.debug("Successfully created monitored item for node: {}", node.getNodeName());
            } else {
                log.error("Failed to create monitored item for node {}: {}", 
                         node.getNodeName(), items.get(0).getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error creating monitored item for node {}: {}", 
                     node.getNodeName(), e.getMessage(), e);
        }
    }
    
    private void handleDataValueChange(OpcUaNode node, DataValue dataValue) {
        try {
            if (dataValue.getValue() != null) {
                // 데이터 품질 매핑
                OpcUaDataPoint.DataQuality quality = mapDataQuality(dataValue.getStatusCode());
                
                // 타임스탬프 변환
                LocalDateTime sourceTimestamp = dataValue.getSourceTime() != null ?
                        LocalDateTime.ofInstant(dataValue.getSourceTime().getJavaInstant(), ZoneId.systemDefault()) :
                        LocalDateTime.now();
                
                LocalDateTime serverTimestamp = dataValue.getServerTime() != null ?
                        LocalDateTime.ofInstant(dataValue.getServerTime().getJavaInstant(), ZoneId.systemDefault()) :
                        LocalDateTime.now();
                
                // 데이터 포인트 저장
                saveDataPointFromValue(node, dataValue.getValue().getValue(), quality, 
                                     sourceTimestamp, serverTimestamp);
                
                log.debug("Data change for node {}: {}", node.getNodeName(), dataValue.getValue().getValue());
            }
        } catch (Exception e) {
            log.error("Error handling data value change for node {}: {}", 
                     node.getNodeName(), e.getMessage(), e);
        }
    }
    
    private OpcUaDataPoint.DataQuality mapDataQuality(StatusCode statusCode) {
        if (statusCode.isGood()) {
            return OpcUaDataPoint.DataQuality.GOOD;
        } else if (statusCode.isUncertain()) {
            return OpcUaDataPoint.DataQuality.UNCERTAIN;
        } else if (statusCode.isBad()) {
            return OpcUaDataPoint.DataQuality.BAD;
        } else {
            return OpcUaDataPoint.DataQuality.UNKNOWN;
        }
    }
    
    @Transactional
    public void saveDataPointFromValue(OpcUaNode node, Object value, OpcUaDataPoint.DataQuality quality,
                                     LocalDateTime sourceTimestamp, LocalDateTime serverTimestamp) {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            OpcUaDataPoint dataPoint = OpcUaDataPoint.builder()
                    .node(node)
                    .value(value != null ? value.toString() : "")
                    .quality(quality)
                    .timestamp(now)
                    .sourceTimestamp(sourceTimestamp)
                    .serverTimestamp(serverTimestamp)
                    .build();
            
            // 데이터 타입에 따른 값 설정
            if (value != null) {
                setTypedValue(dataPoint, value, node.getDataType());
            }
            
            dataPointRepository.save(dataPoint);
            log.debug("Saved data point for node {}: {}", node.getNodeName(), value);
            
        } catch (Exception e) {
            log.error("Error saving data point for node {}: {}", node.getNodeName(), e.getMessage(), e);
        }
    }
    
    private void setTypedValue(OpcUaDataPoint dataPoint, Object value, OpcUaNode.NodeDataType dataType) {
        try {
            switch (dataType) {
                case BOOLEAN:
                    if (value instanceof Boolean) {
                        dataPoint.setBooleanValue((Boolean) value);
                    } else {
                        dataPoint.setBooleanValue(Boolean.parseBoolean(value.toString()));
                    }
                    break;
                case FLOAT:
                case DOUBLE:
                    if (value instanceof Number) {
                        dataPoint.setNumericValue(((Number) value).doubleValue());
                    } else {
                        dataPoint.setNumericValue(Double.parseDouble(value.toString()));
                    }
                    break;
                case INT16:
                case INT32:
                case INT64:
                case UINT16:
                case UINT32:
                case UINT64:
                    if (value instanceof Number) {
                        dataPoint.setNumericValue(((Number) value).doubleValue());
                    } else {
                        dataPoint.setNumericValue(Double.parseDouble(value.toString()));
                    }
                    break;
                case STRING:
                    // String 타입은 이미 value 필드에 저장됨
                    break;
                case BYTESTRING:
                    if (value instanceof byte[]) {
                        dataPoint.setRawData((byte[]) value);
                    }
                    break;
            }
        } catch (Exception e) {
            log.warn("Could not parse value {} for data type {}: {}", value, dataType, e.getMessage());
        }
    }
    
    // 기존 saveDataPoint 메서드는 호환성을 위해 유지
    @Transactional
    public void saveDataPoint(OpcUaNode node, Object value, OpcUaDataPoint.DataQuality quality) {
        LocalDateTime now = LocalDateTime.now();
        saveDataPointFromValue(node, value, quality, now, now);
    }
    
    @Async("opcUaTaskExecutor")
    public void startDataCollection(OpcUaNode node) {
        // 실제 OPC-UA 구독이 설정되어 있다면 추가적인 처리가 필요한 경우에만 사용
        // 일반적으로는 구독을 통해 자동으로 데이터가 수집됨
        try {
            OpcUaClient client = activeClients.get(node.getServer().getId());
            if (client != null && connectionStatus.getOrDefault(node.getServer().getId(), false)) {
                // 한번만 읽기 (구독이 설정되지 않은 경우)
                NodeId nodeId = new NodeId(node.getNamespaceIndex(), node.getNodeId());
                DataValue dataValue = client.readValue(0, TimestampsToReturn.Both, nodeId).get();
                handleDataValueChange(node, dataValue);
            }
        } catch (Exception e) {
            log.error("Error collecting data for node {}: {}", node.getNodeName(), e.getMessage(), e);
            saveDataPoint(node, null, OpcUaDataPoint.DataQuality.BAD);
        }
    }
    
    @Transactional
    public void updateServerStatus(Long serverId, OpcUaServer.ServerStatus status) {
        try {
            OpcUaServer server = serverRepository.findById(serverId).orElse(null);
            if (server != null) {
                server.setStatus(status);
                if (status == OpcUaServer.ServerStatus.CONNECTED) {
                    server.setLastConnectedAt(LocalDateTime.now());
                }
                serverRepository.save(server);
            }
        } catch (Exception e) {
            log.error("Error updating server status: {}", e.getMessage(), e);
        }
    }
    
    // 정기적으로 연결 상태 확인
    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void checkConnectionHealth() {
        log.debug("Checking OPC-UA connection health...");
        
        for (Map.Entry<Long, OpcUaClient> entry : activeClients.entrySet()) {
            Long serverId = entry.getKey();
            OpcUaClient client = entry.getValue();
            
            try {
                // 클라이언트 연결 상태 확인
                CompletableFuture<? extends UaSession> sessionFuture = client.getSession();
                if (sessionFuture == null || !sessionFuture.isDone() || sessionFuture.isCompletedExceptionally()) {
                    connectionStatus.put(serverId, false);
                    
                    // 재연결 시도
                    OpcUaServer server = serverRepository.findById(serverId).orElse(null);
                    if (server != null && server.getIsActive()) {
                        log.info("Connection lost, attempting to reconnect to server: {}", server.getServerName());
                        disconnectFromServer(serverId); // 기존 연결 정리
                        connectToServerAsync(server); // 재연결 시도
                    }
                } else {
                    connectionStatus.put(serverId, true);
                }
            } catch (Exception e) {
                log.warn("Error checking connection health for server {}: {}", serverId, e.getMessage());
                connectionStatus.put(serverId, false);
            }
        }
    }
    
    public boolean isServerConnected(Long serverId) {
        return connectionStatus.getOrDefault(serverId, false);
    }
    
    public int getActiveConnectionCount() {
        return (int) connectionStatus.values().stream().mapToLong(connected -> connected ? 1 : 0).sum();
    }
    
    // 노드 구독 추가 메서드
    @Async("opcUaTaskExecutor")
    public CompletableFuture<Boolean> addNodeSubscription(OpcUaNode node) {
        try {
            OpcUaClient client = activeClients.get(node.getServer().getId());
            UaSubscription subscription = subscriptions.get(node.getServer().getId());
            
            if (client != null && subscription != null) {
                createMonitoredItem(subscription, node);
                return CompletableFuture.completedFuture(true);
            }
        } catch (Exception e) {
            log.error("Error adding node subscription for node {}: {}", node.getNodeName(), e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(false);
    }
    
    // 노드 구독 제거 메서드
    @Async("opcUaTaskExecutor")
    public CompletableFuture<Boolean> removeNodeSubscription(OpcUaNode node) {
        try {
            UaSubscription subscription = subscriptions.get(node.getServer().getId());
            
            if (subscription != null) {
                // 특정 노드의 모니터링 아이템 찾기 및 삭제
                List<UaMonitoredItem> items = subscription.getMonitoredItems();
                for (UaMonitoredItem item : items) {
                    if (item.getClientHandle().equals(UInteger.valueOf(node.getId()))) {
                        subscription.deleteMonitoredItems(List.of(item)).get();
                        log.debug("Removed monitored item for node: {}", node.getNodeName());
                        return CompletableFuture.completedFuture(true);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error removing node subscription for node {}: {}", node.getNodeName(), e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(false);
    }
} 