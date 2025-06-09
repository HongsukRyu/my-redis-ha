package com.backend.api.service.opcua;

import com.backend.api.model.opcua.OpcUaNode;
import com.backend.api.repository.opcua.OpcUaNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpcUaDataCollectionScheduler {
    
    private final OpcUaNodeRepository nodeRepository;
    private final OpcUaClientService clientService;
    private final OpcUaManagementService managementService;
    
    /**
     * 구독된 노드들의 데이터를 정기적으로 수집
     * 매 10초마다 실행
     */
    @Scheduled(fixedRate = 10000)
    public void collectDataFromSubscribedNodes() {
        try {
            List<OpcUaNode> subscribedNodes = nodeRepository.findByIsSubscribedTrue();
            
            if (!subscribedNodes.isEmpty()) {
                log.debug("Collecting data from {} subscribed nodes", subscribedNodes.size());
                
                for (OpcUaNode node : subscribedNodes) {
                    // 서버가 연결되어 있는지 확인
                    if (clientService.isServerConnected(node.getServer().getId())) {
                        // 비동기로 데이터 수집
                        clientService.startDataCollection(node);
                    } else {
                        log.warn("Server {} is not connected, skipping data collection for node {}", 
                                node.getServer().getServerName(), node.getNodeName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error during scheduled data collection: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 연결 상태를 정기적으로 로깅
     * 매 1분마다 실행
     */
    @Scheduled(fixedRate = 60000)
    public void logConnectionStatus() {
        try {
            int activeConnections = managementService.getActiveConnectionCount();
            long subscribedNodes = nodeRepository.findByIsSubscribedTrue().size();
            
            log.info("OPC-UA Status - Active Connections: {}, Subscribed Nodes: {}", 
                    activeConnections, subscribedNodes);
            
        } catch (Exception e) {
            log.error("Error logging connection status: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 오래된 데이터 포인트 정리
     * 매일 새벽 2시에 실행 (7일 이상 된 데이터 삭제)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldDataPoints() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
            log.info("Starting cleanup of data points older than: {}", cutoffDate);
            
            managementService.cleanupOldDataPoints(cutoffDate);
            
            log.info("Data cleanup completed successfully");
            
        } catch (Exception e) {
            log.error("Error during data cleanup: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 연결이 끊어진 서버들의 재연결 시도
     * 매 5분마다 실행
     */
    @Scheduled(fixedRate = 300000)
    public void attemptReconnection() {
        try {
            // OpcUaClientService의 checkConnectionHealth가 이미 30초마다 실행되므로
            // 여기서는 추가적인 로직이나 알림을 처리할 수 있습니다.
            log.debug("Reconnection attempt cycle completed");
            
        } catch (Exception e) {
            log.error("Error during reconnection attempt: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 시스템 헬스체크 및 알림
     * 매 10분마다 실행
     */
    @Scheduled(fixedRate = 600000)
    public void performHealthCheck() {
        try {
            // 활성 연결 수 확인
            int activeConnections = managementService.getActiveConnectionCount();
            
            // 구독된 노드 수 확인
            long subscribedNodes = nodeRepository.findByIsSubscribedTrue().size();
            
            // 최근 데이터 수집 활동 확인
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            
            // 경고 조건 확인
            if (activeConnections == 0 && subscribedNodes > 0) {
                log.warn("Health Check Alert: No active connections but {} nodes are subscribed", subscribedNodes);
            }
            
            log.info("Health Check - Connections: {}, Subscribed Nodes: {}, Timestamp: {}", 
                    activeConnections, subscribedNodes, LocalDateTime.now());
            
        } catch (Exception e) {
            log.error("Error during health check: {}", e.getMessage(), e);
        }
    }
} 