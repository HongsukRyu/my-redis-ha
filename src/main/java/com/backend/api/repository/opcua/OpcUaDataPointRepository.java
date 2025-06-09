package com.backend.api.repository.opcua;

import com.backend.api.model.opcua.OpcUaDataPoint;
import com.backend.api.model.opcua.OpcUaNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OpcUaDataPointRepository extends JpaRepository<OpcUaDataPoint, Long> {
    
    List<OpcUaDataPoint> findByNode(OpcUaNode node);
    
    Page<OpcUaDataPoint> findByNode(OpcUaNode node, Pageable pageable);
    
    @Query("SELECT d FROM OpcUaDataPoint d WHERE d.node = :node AND d.timestamp BETWEEN :startTime AND :endTime ORDER BY d.timestamp DESC")
    List<OpcUaDataPoint> findByNodeAndTimestampBetween(@Param("node") OpcUaNode node, 
                                                       @Param("startTime") LocalDateTime startTime, 
                                                       @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT d FROM OpcUaDataPoint d WHERE d.node = :node ORDER BY d.timestamp DESC")
    Page<OpcUaDataPoint> findByNodeOrderByTimestampDesc(@Param("node") OpcUaNode node, Pageable pageable);
    
    @Query("SELECT d FROM OpcUaDataPoint d WHERE d.node = :node ORDER BY d.timestamp DESC")
    Page<OpcUaDataPoint> findLatestByNode(@Param("node") OpcUaNode node, Pageable pageable);

    @Query("SELECT d FROM OpcUaDataPoint d WHERE d.node.server.id = :serverId AND d.timestamp BETWEEN :startTime AND :endTime")
    List<OpcUaDataPoint> findByServerIdAndTimestampBetween(@Param("serverId") Long serverId, 
                                                           @Param("startTime") LocalDateTime startTime, 
                                                           @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT d FROM OpcUaDataPoint d WHERE d.quality = :quality AND d.timestamp >= :since")
    List<OpcUaDataPoint> findByQualityAndTimestampAfter(@Param("quality") OpcUaDataPoint.DataQuality quality, 
                                                        @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(d) FROM OpcUaDataPoint d WHERE d.node = :node AND d.timestamp >= :since")
    long countByNodeAndTimestampAfter(@Param("node") OpcUaNode node, @Param("since") LocalDateTime since);
    
    @Query("SELECT d FROM OpcUaDataPoint d WHERE d.timestamp < :before")
    List<OpcUaDataPoint> findOldDataPoints(@Param("before") LocalDateTime before);
    
    void deleteByTimestampBefore(LocalDateTime before);
}
