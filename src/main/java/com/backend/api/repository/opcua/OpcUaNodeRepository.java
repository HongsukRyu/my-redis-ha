package com.backend.api.repository.opcua;

import com.backend.api.model.opcua.OpcUaNode;
import com.backend.api.model.opcua.OpcUaServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpcUaNodeRepository extends JpaRepository<OpcUaNode, Long> {
    
    List<OpcUaNode> findByServer(OpcUaServer server);
    
    List<OpcUaNode> findByServerAndIsActiveTrue(OpcUaServer server);
    
    List<OpcUaNode> findByIsSubscribedTrue();
    
    @Query("SELECT n FROM OpcUaNode n WHERE n.server = :server AND n.isSubscribed = true AND n.isActive = true")
    List<OpcUaNode> findActiveSubscribedNodesByServer(@Param("server") OpcUaServer server);
    
    Optional<OpcUaNode> findByServerAndNodeNameAndNodeId(OpcUaServer server, String nodeName, String nodeId);
    
    List<OpcUaNode> findByNodeNameContainingIgnoreCase(String nodeName);
    
    @Query("SELECT n FROM OpcUaNode n WHERE n.server.id = :serverId AND n.dataType = :dataType")
    List<OpcUaNode> findByServerIdAndDataType(@Param("serverId") Long serverId, 
                                              @Param("dataType") OpcUaNode.NodeDataType dataType);
    
    boolean existsByServerAndNodeId(OpcUaServer server, String nodeId);
    
    @Query("SELECT COUNT(n) FROM OpcUaNode n WHERE n.server = :server AND n.isSubscribed = true")
    long countSubscribedNodesByServer(@Param("server") OpcUaServer server);
} 