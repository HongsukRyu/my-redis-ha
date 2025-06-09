package com.backend.api.repository.workorder;

import com.backend.api.model.workorder.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    
    // 작업지시번호로 조회
    Optional<WorkOrder> findByWorkOrderNumber(String workOrderNumber);
    
    // 상태별 조회
    List<WorkOrder> findByStatus(WorkOrder.WorkOrderStatus status);
    
    // 우선순위별 조회
    List<WorkOrder> findByPriority(WorkOrder.WorkOrderPriority priority);
    
    // 담당 작업자별 조회
    List<WorkOrder> findByAssignedOperator(String assignedOperator);
    
    // 생산라인별 조회
    List<WorkOrder> findByProductionLineId(Long productionLineId);
    
    // 제품별 조회
    List<WorkOrder> findByProductId(Long productId);
    
    // 예정 시작 시간 범위로 조회
    @Query("SELECT w FROM WorkOrder w WHERE w.scheduledStartTime BETWEEN :startTime AND :endTime")
    List<WorkOrder> findByScheduledStartTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);
    
    // 진행중인 작업지시 조회
    @Query("SELECT w FROM WorkOrder w WHERE w.status = 'IN_PROGRESS'")
    List<WorkOrder> findInProgressWorkOrders();
    
    // 지연된 작업지시 조회 (예정 완료시간이 지났지만 완료되지 않은 것)
    @Query("SELECT w FROM WorkOrder w WHERE w.scheduledEndTime < :currentTime AND w.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<WorkOrder> findDelayedWorkOrders(@Param("currentTime") LocalDateTime currentTime);
    
    // 오늘 스케줄된 작업지시 조회
    @Query("SELECT w FROM WorkOrder w WHERE DATE(w.scheduledStartTime) = CURRENT_DATE")
    List<WorkOrder> findTodayScheduledWorkOrders();
    
    // 우선순위별 정렬 및 상태별 조회
    List<WorkOrder> findByStatusOrderByPriorityAscScheduledStartTimeAsc(WorkOrder.WorkOrderStatus status);
    
    // 생산계획별 작업지시 조회
    List<WorkOrder> findByProductionPlanId(Long productionPlanId);
    
    // 완료율 계산이 가능한 작업지시 조회
    @Query("SELECT w FROM WorkOrder w WHERE w.orderQuantity > 0 AND w.completedQuantity IS NOT NULL")
    List<WorkOrder> findWorkOrdersWithCompletionData();
} 