package com.backend.api.repository.workorder;

import com.backend.api.model.workorder.WorkOrderProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkOrderProgressRepository extends JpaRepository<WorkOrderProgress, Long> {
    
    // 작업 지시서별 진행 이력 조회 (최신순)
    List<WorkOrderProgress> findByWorkOrderIdOrderByReportedAtDesc(Long workOrderId);
    
    // 보고자별 조회
    List<WorkOrderProgress> findByReportedBy(String reportedBy);
    
    // 상태별 조회
    List<WorkOrderProgress> findByStatus(WorkOrderProgress.ProgressStatus status);
    
    // 기간별 진행 상황 조회
    @Query("SELECT wop FROM WorkOrderProgress wop WHERE wop.reportedAt BETWEEN :startDate AND :endDate ORDER BY wop.reportedAt DESC")
    List<WorkOrderProgress> findByReportedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
    
    // 작업 지시서별 최신 진행 상황 조회
    @Query("SELECT wop FROM WorkOrderProgress wop WHERE wop.workOrder.id = :workOrderId ORDER BY wop.reportedAt DESC LIMIT 1")
    WorkOrderProgress findLatestProgressByWorkOrderId(@Param("workOrderId") Long workOrderId);
    
    // 문제가 있는 진행 상황 조회 (품질 문제, 장비 문제 등)
    @Query("SELECT wop FROM WorkOrderProgress wop WHERE wop.status IN ('QUALITY_ISSUE', 'EQUIPMENT_ISSUE', 'MATERIAL_SHORTAGE') ORDER BY wop.reportedAt DESC")
    List<WorkOrderProgress> findProgressWithIssues();
} 