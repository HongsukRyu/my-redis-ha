package com.backend.api.repository.opcua;

import com.backend.api.model.opcua.OpcUaServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpcUaServerRepository extends JpaRepository<OpcUaServer, Long> {
    
    Optional<OpcUaServer> findByServerName(String serverName);
    
    List<OpcUaServer> findByIsActiveTrue();
    
    List<OpcUaServer> findByStatus(OpcUaServer.ServerStatus status);
    
    @Query("SELECT s FROM OpcUaServer s WHERE s.isActive = true AND s.status = :status")
    List<OpcUaServer> findActiveServersByStatus(@Param("status") OpcUaServer.ServerStatus status);
    
    boolean existsByServerName(String serverName);
    
    boolean existsByEndpointUrl(String endpointUrl);
    
    @Query("SELECT s FROM OpcUaServer s WHERE s.endpointUrl LIKE %:url%")
    List<OpcUaServer> findByEndpointUrlContaining(@Param("url") String url);
}