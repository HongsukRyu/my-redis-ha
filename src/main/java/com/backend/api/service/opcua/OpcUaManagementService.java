package com.backend.api.service.opcua;

import com.backend.api.model.opcua.OpcUaDataPoint;
import com.backend.api.model.opcua.OpcUaNode;
import com.backend.api.model.opcua.OpcUaServer;
import com.backend.api.repository.opcua.OpcUaDataPointRepository;
import com.backend.api.repository.opcua.OpcUaNodeRepository;
import com.backend.api.repository.opcua.OpcUaServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpcUaManagementService {
    
    private final OpcUaServerRepository serverRepository;
    private final OpcUaNodeRepository nodeRepository;
    private final OpcUaDataPointRepository dataPointRepository;
    private final OpcUaClientService clientService;
    
    // ==================== Server Management ====================
    
    @Transactional
    public OpcUaServer createServer(OpcUaServer server) {
        log.info("Creating OPC-UA server: {}", server.getServerName());
        
        if (serverRepository.existsByServerName(server.getServerName())) {
            throw new IllegalArgumentException("Server name already exists: " + server.getServerName());
        }
        
        if (serverRepository.existsByEndpointUrl(server.getEndpointUrl())) {
            throw new IllegalArgumentException("Endpoint URL already exists: " + server.getEndpointUrl());
        }
        
        OpcUaServer savedServer = serverRepository.save(server);
        
        // 서버가 활성화되어 있으면 자동으로 연결 시도
        if (savedServer.getIsActive()) {
            clientService.connectToServerAsync(savedServer);
        }
        
        return savedServer;
    }
    
    @Transactional
    public OpcUaServer updateServer(Long serverId, OpcUaServer updatedServer) {
        log.info("Updating OPC-UA server ID: {}", serverId);
        
        OpcUaServer existingServer = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("Server not found: " + serverId));
        
        // 연결 해제 (필요한 경우)
        if (clientService.isServerConnected(serverId)) {
            clientService.disconnectFromServer(serverId);
        }
        
        // 서버 정보 업데이트
        existingServer.setServerName(updatedServer.getServerName());
        existingServer.setEndpointUrl(updatedServer.getEndpointUrl());
        existingServer.setUsername(updatedServer.getUsername());
        existingServer.setPassword(updatedServer.getPassword());
        existingServer.setDescription(updatedServer.getDescription());
        existingServer.setIsActive(updatedServer.getIsActive());
        existingServer.setConnectionTimeout(updatedServer.getConnectionTimeout());
        existingServer.setRequestTimeout(updatedServer.getRequestTimeout());
        existingServer.setSubscriptionInterval(updatedServer.getSubscriptionInterval());
        
        OpcUaServer savedServer = serverRepository.save(existingServer);
        
        // 활성화된 경우 재연결
        if (savedServer.getIsActive()) {
            clientService.connectToServerAsync(savedServer);
        }
        
        return savedServer;
    }
    
    @Transactional
    public void deleteServer(Long serverId) {
        log.info("Deleting OPC-UA server ID: {}", serverId);
        
        OpcUaServer server = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("Server not found: " + serverId));
        
        // 연결 해제
        clientService.disconnectFromServer(serverId);
        
        // 관련 노드들의 구독 해제
        List<OpcUaNode> nodes = nodeRepository.findByServer(server);
        for (OpcUaNode node : nodes) {
            node.setIsSubscribed(false);
            node.setIsActive(false);
        }
        nodeRepository.saveAll(nodes);
        
        // 서버 삭제
        serverRepository.delete(server);
    }
    
    public List<OpcUaServer> getAllServers() {
        return serverRepository.findAll();
    }
    
    public Optional<OpcUaServer> getServerById(Long serverId) {
        return serverRepository.findById(serverId);
    }
    
    public List<OpcUaServer> getActiveServers() {
        return serverRepository.findByIsActiveTrue();
    }
    
    // ==================== Node Management ====================
    
    @Transactional
    public OpcUaNode createNode(OpcUaNode node) {
        log.info("Creating OPC-UA node: {} for server: {}", 
                node.getNodeName(), node.getServer().getServerName());
        
        if (nodeRepository.existsByServerAndNodeId(node.getServer(), node.getNodeId())) {
            throw new IllegalArgumentException("Node ID already exists for this server: " + node.getNodeId());
        }
        
        return nodeRepository.save(node);
    }
    
    @Transactional
    public OpcUaNode updateNode(Long nodeId, OpcUaNode updatedNode) {
        log.info("Updating OPC-UA node ID: {}", nodeId);
        
        OpcUaNode existingNode = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));
        
        existingNode.setNodeName(updatedNode.getNodeName());
        existingNode.setNodeId(updatedNode.getNodeId());
        existingNode.setNamespaceIndex(updatedNode.getNamespaceIndex());
        existingNode.setDataType(updatedNode.getDataType());
        existingNode.setDescription(updatedNode.getDescription());
        existingNode.setIsActive(updatedNode.getIsActive());
        existingNode.setSamplingInterval(updatedNode.getSamplingInterval());
        existingNode.setUnit(updatedNode.getUnit());
        existingNode.setMinValue(updatedNode.getMinValue());
        existingNode.setMaxValue(updatedNode.getMaxValue());
        
        return nodeRepository.save(existingNode);
    }
    
    @Transactional
    public void deleteNode(Long nodeId) {
        log.info("Deleting OPC-UA node ID: {}", nodeId);
        
        OpcUaNode node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));
        
        // 구독 해제
        if (node.getIsSubscribed()) {
            node.setIsSubscribed(false);
            nodeRepository.save(node);
        }
        
        nodeRepository.delete(node);
    }
    
    @Transactional
    public OpcUaNode toggleNodeSubscription(Long nodeId) {
        log.info("Toggling subscription for OPC-UA node ID: {}", nodeId);
        
        OpcUaNode node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));
        
        node.setIsSubscribed(!node.getIsSubscribed());
        OpcUaNode savedNode = nodeRepository.save(node);
        
        // 서버가 연결되어 있고 노드가 구독 상태라면 데이터 수집 시작
        if (savedNode.getIsSubscribed() && 
            clientService.isServerConnected(savedNode.getServer().getId())) {
            clientService.startDataCollection(savedNode);
        }
        
        return savedNode;
    }
    
    public List<OpcUaNode> getNodesByServer(Long serverId) {
        OpcUaServer server = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("Server not found: " + serverId));
        return nodeRepository.findByServer(server);
    }
    
    public List<OpcUaNode> getSubscribedNodes() {
        return nodeRepository.findByIsSubscribedTrue();
    }
    
    public Optional<OpcUaNode> getNodeById(Long nodeId) {
        return nodeRepository.findById(nodeId);
    }
    
    // ==================== Data Point Management ====================
    
    public Page<OpcUaDataPoint> getDataPointsByNode(Long nodeId, Pageable pageable) {
        OpcUaNode node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));
        
        return dataPointRepository.findByNodeOrderByTimestampDesc(node, pageable);
    }
    
    public List<OpcUaDataPoint> getDataPointsByNodeAndTimeRange(Long nodeId, 
                                                               LocalDateTime startTime, 
                                                               LocalDateTime endTime) {
        OpcUaNode node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));
        
        return dataPointRepository.findByNodeAndTimestampBetween(node, startTime, endTime);
    }
    
    public Optional<OpcUaDataPoint> getLatestDataPoint(Long nodeId) {
        OpcUaNode node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<OpcUaDataPoint> page = dataPointRepository.findLatestByNode(node, pageable);
        return page.getContent().stream().findFirst();
    }
    
    public List<OpcUaDataPoint> getDataPointsByServerAndTimeRange(Long serverId,
                                                                 LocalDateTime startTime,
                                                                 LocalDateTime endTime) {
        return dataPointRepository.findByServerIdAndTimestampBetween(serverId, startTime, endTime);
    }
    
    @Transactional
    public void cleanupOldDataPoints(LocalDateTime before) {
        log.info("Cleaning up data points older than: {}", before);
        dataPointRepository.deleteByTimestampBefore(before);
    }
    
    // ==================== Connection Management ====================
    
    public void connectToServer(Long serverId) {
        OpcUaServer server = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("Server not found: " + serverId));
        
        clientService.connectToServerAsync(server);
    }
    
    public void disconnectFromServer(Long serverId) {
        clientService.disconnectFromServer(serverId);
    }
    
    public boolean isServerConnected(Long serverId) {
        return clientService.isServerConnected(serverId);
    }
    
    public int getActiveConnectionCount() {
        return clientService.getActiveConnectionCount();
    }
    
    // ==================== Statistics ====================
    
    public long getDataPointCount(Long nodeId, LocalDateTime since) {
        OpcUaNode node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));
        
        return dataPointRepository.countByNodeAndTimestampAfter(node, since);
    }
    
    public long getSubscribedNodeCountByServer(Long serverId) {
        OpcUaServer server = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("Server not found: " + serverId));
        
        return nodeRepository.countSubscribedNodesByServer(server);
    }
}

