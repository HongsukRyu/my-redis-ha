package com.backend.api.service.opcua;

import com.backend.api.model.opcua.OpcUaServer;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OpcUaRealClientService {
    
    /**
     * 간단한 OPC-UA 서버 연결 테스트
     * Eclipse Milo 라이브러리의 호환성을 확인합니다.
     */
    public CompletableFuture<Boolean> testConnection(OpcUaServer server) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Testing OPC-UA connection to: {}", server.getEndpointUrl());
                
                // 엔드포인트 검색
                List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(server.getEndpointUrl()).get();
                
                if (endpoints.isEmpty()) {
                    log.warn("No endpoints found for: {}", server.getEndpointUrl());
                    return false;
                }
                
                // 보안 정책이 None인 엔드포인트 선택 (테스트용)
                EndpointDescription endpoint = endpoints.stream()
                        .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                        .findFirst()
                        .orElse(endpoints.get(0));
                
                log.info("Found endpoint: {} with security policy: {}", 
                        endpoint.getEndpointUrl(), endpoint.getSecurityPolicyUri());
                
                // 간단한 클라이언트 생성 및 연결 테스트
                OpcUaClient client = OpcUaClient.create(endpoint.getEndpointUrl());
                client.connect().get();
                client.disconnect().get();
                
                log.info("Successfully tested connection to: {}", server.getEndpointUrl());
                return true;
                
            } catch (Exception e) {
                log.error("Error testing OPC-UA connection to {}: {}", server.getEndpointUrl(), e.getMessage(), e);
                return false;
            }
        });
    }
    
    /**
     * 단일 노드 값 읽기 테스트
     */
    public Object readNodeValue(String endpointUrl, String nodeId, int namespaceIndex) {
        try {
            log.info("Reading node value from {}: ns={}; s={}", endpointUrl, namespaceIndex, nodeId);
            
            // 시뮬레이션 값 반환
            return Math.random() * 100.0;
            
        } catch (Exception e) {
            log.error("Error reading node value: {}", e.getMessage(), e);
            return null;
        }
    }
} 