package com.backend.api.service.workorder;

import com.backend.api.model.workorder.WorkOrder;
import com.backend.api.model.workorder.WorkOrderProgress;
import com.backend.api.repository.workorder.WorkOrderRepository;
import com.backend.api.repository.workorder.WorkOrderProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WorkOrderService {
    
    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderProgressRepository workOrderProgressRepository;
    
    /**
     * 작업 지시서 생성
     */
    public WorkOrder createWorkOrder(WorkOrder workOrder) {
        // 작업지시번호 중복 확인
        if (workOrderRepository.findByWorkOrderNumber(workOrder.getWorkOrderNumber()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 작업지시번호입니다: " + workOrder.getWorkOrderNumber());
        }
        
        // 초기값 설정
        workOrder.setCompletedQuantity(0);
        workOrder.setDefectQuantity(0);
        workOrder.setStatus(WorkOrder.WorkOrderStatus.PENDING);
        
        log.info("작업 지시서 생성: {}", workOrder.getWorkOrderNumber());
        return workOrderRepository.save(workOrder);
    }
    
    /**
     * 작업 지시서 시작
     */
    public WorkOrder startWorkOrder(Long workOrderId, String operator) {
        WorkOrder workOrder = getWorkOrderById(workOrderId);
        
        if (workOrder.getStatus() != WorkOrder.WorkOrderStatus.PENDING && 
            workOrder.getStatus() != WorkOrder.WorkOrderStatus.RELEASED) {
            throw new IllegalStateException("시작할 수 없는 작업 지시서 상태입니다: " + workOrder.getStatus());
        }
        
        workOrder.setStatus(WorkOrder.WorkOrderStatus.IN_PROGRESS);
        workOrder.setActualStartTime(LocalDateTime.now());
        workOrder.setAssignedOperator(operator);
        
        log.info("작업 지시서 시작: {} - 작업자: {}", workOrder.getWorkOrderNumber(), operator);
        return workOrderRepository.save(workOrder);
    }
    
    /**
     * 작업 진행 상황 보고
     */
    public WorkOrderProgress reportProgress(Long workOrderId, Integer quantityProduced, 
                                          Integer quantityDefect, String reportedBy, String comments) {
        WorkOrder workOrder = getWorkOrderById(workOrderId);
        
        if (workOrder.getStatus() != WorkOrder.WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행중인 작업이 아닙니다: " + workOrder.getStatus());
        }
        
        // 진행률 계산
        Integer totalProduced = (workOrder.getCompletedQuantity() != null ? workOrder.getCompletedQuantity() : 0) + quantityProduced;
        Double progressPercentage = (double) totalProduced / workOrder.getOrderQuantity() * 100;
        
        // 작업 지시서 업데이트
        workOrder.setCompletedQuantity(totalProduced);
        workOrder.setDefectQuantity((workOrder.getDefectQuantity() != null ? workOrder.getDefectQuantity() : 0) + 
                                   (quantityDefect != null ? quantityDefect : 0));
        
        // 진행 상황 기록 생성
        WorkOrderProgress progress = new WorkOrderProgress();
        progress.setWorkOrder(workOrder);
        progress.setReportedAt(LocalDateTime.now());
        progress.setQuantityProduced(quantityProduced);
        progress.setQuantityDefect(quantityDefect);
        progress.setProgressPercentage(progressPercentage);
        progress.setReportedBy(reportedBy);
        progress.setComments(comments);
        progress.setStatus(WorkOrderProgress.ProgressStatus.ON_TRACK);
        
        workOrderRepository.save(workOrder);
        workOrderProgressRepository.save(progress);
        
        log.info("작업 진행 상황 보고: {} - 생산량: {}, 진행률: {}%", 
                workOrder.getWorkOrderNumber(), quantityProduced, String.format("%.1f", progressPercentage));
        
        return progress;
    }
    
    /**
     * 작업 지시서 완료
     */
    public WorkOrder completeWorkOrder(Long workOrderId, String completedBy) {
        WorkOrder workOrder = getWorkOrderById(workOrderId);
        
        if (workOrder.getStatus() != WorkOrder.WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행중인 작업이 아닙니다: " + workOrder.getStatus());
        }
        
        workOrder.setStatus(WorkOrder.WorkOrderStatus.COMPLETED);
        workOrder.setActualEndTime(LocalDateTime.now());
        
        log.info("작업 지시서 완료: {} - 완료자: {}", workOrder.getWorkOrderNumber(), completedBy);
        return workOrderRepository.save(workOrder);
    }
    
    /**
     * 작업 지시서 보류
     */
    public WorkOrder holdWorkOrder(Long workOrderId, String reason) {
        WorkOrder workOrder = getWorkOrderById(workOrderId);
        
        workOrder.setStatus(WorkOrder.WorkOrderStatus.ON_HOLD);
        workOrder.setRemarks(workOrder.getRemarks() + " [보류사유: " + reason + "]");
        
        log.info("작업 지시서 보류: {} - 사유: {}", workOrder.getWorkOrderNumber(), reason);
        return workOrderRepository.save(workOrder);
    }
    
    /**
     * 작업 지시서 취소
     */
    public WorkOrder cancelWorkOrder(Long workOrderId, String reason) {
        WorkOrder workOrder = getWorkOrderById(workOrderId);
        
        workOrder.setStatus(WorkOrder.WorkOrderStatus.CANCELLED);
        workOrder.setRemarks(workOrder.getRemarks() + " [취소사유: " + reason + "]");
        
        log.info("작업 지시서 취소: {} - 사유: {}", workOrder.getWorkOrderNumber(), reason);
        return workOrderRepository.save(workOrder);
    }
    
    /**
     * 오늘 스케줄된 작업 지시서 조회
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getTodayScheduledWorkOrders() {
        return workOrderRepository.findTodayScheduledWorkOrders();
    }
    
    /**
     * 진행중인 작업 지시서 조회
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getInProgressWorkOrders() {
        return workOrderRepository.findInProgressWorkOrders();
    }
    
    /**
     * 지연된 작업 지시서 조회
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getDelayedWorkOrders() {
        return workOrderRepository.findDelayedWorkOrders(LocalDateTime.now());
    }
    
    /**
     * 작업자별 할당된 작업 지시서 조회
     */
    @Transactional(readOnly = true)
    public List<WorkOrder> getWorkOrdersByOperator(String operator) {
        return workOrderRepository.findByAssignedOperator(operator);
    }
    
    /**
     * 작업 지시서 조회 (ID)
     */
    @Transactional(readOnly = true)
    public WorkOrder getWorkOrderById(Long workOrderId) {
        return workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("작업 지시서를 찾을 수 없습니다: " + workOrderId));
    }
    
    /**
     * 작업 지시서 조회 (작업지시번호)
     */
    @Transactional(readOnly = true)
    public Optional<WorkOrder> getWorkOrderByNumber(String workOrderNumber) {
        return workOrderRepository.findByWorkOrderNumber(workOrderNumber);
    }
    
    /**
     * 작업 진행 이력 조회
     */
    @Transactional(readOnly = true)
    public List<WorkOrderProgress> getWorkOrderProgressHistory(Long workOrderId) {
        return workOrderProgressRepository.findByWorkOrderIdOrderByReportedAtDesc(workOrderId);
    }
} 