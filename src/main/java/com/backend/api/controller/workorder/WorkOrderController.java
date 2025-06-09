package com.backend.api.controller.workorder;

import com.backend.api.model.workorder.WorkOrder;
import com.backend.api.model.workorder.WorkOrderProgress;
import com.backend.api.service.workorder.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "작업 지시 관리", description = "작업 지시서 관리 API")
public class WorkOrderController {
    
    private final WorkOrderService workOrderService;
    
    @Operation(summary = "작업 지시서 생성", description = "새로운 작업 지시서를 생성합니다")
    @PostMapping
    public ResponseEntity<WorkOrder> createWorkOrder(@RequestBody WorkOrder workOrder) {
        WorkOrder createdWorkOrder = workOrderService.createWorkOrder(workOrder);
        return ResponseEntity.ok(createdWorkOrder);
    }
    
    @Operation(summary = "작업 지시서 조회", description = "작업 지시서 ID로 상세 정보를 조회합니다")
    @GetMapping("/{workOrderId}")
    public ResponseEntity<WorkOrder> getWorkOrderById(@PathVariable Long workOrderId) {
        WorkOrder workOrder = workOrderService.getWorkOrderById(workOrderId);
        return ResponseEntity.ok(workOrder);
    }
    
    @Operation(summary = "작업지시번호로 조회", description = "작업지시번호로 작업 지시서를 조회합니다")
    @GetMapping("/by-number/{workOrderNumber}")
    public ResponseEntity<WorkOrder> getWorkOrderByNumber(@PathVariable String workOrderNumber) {
        return workOrderService.getWorkOrderByNumber(workOrderNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "작업 지시서 시작", description = "작업 지시서를 시작 상태로 변경합니다")
    @PostMapping("/{workOrderId}/start")
    public ResponseEntity<WorkOrder> startWorkOrder(
            @PathVariable Long workOrderId,
            @RequestBody Map<String, String> request) {
        
        String operator = request.get("operator");
        WorkOrder workOrder = workOrderService.startWorkOrder(workOrderId, operator);
        return ResponseEntity.ok(workOrder);
    }
    
    @Operation(summary = "작업 진행 상황 보고", description = "작업 진행 상황을 보고합니다")
    @PostMapping("/{workOrderId}/progress")
    public ResponseEntity<WorkOrderProgress> reportProgress(
            @PathVariable Long workOrderId,
            @RequestBody Map<String, Object> request) {
        
        Integer quantityProduced = (Integer) request.get("quantityProduced");
        Integer quantityDefect = (Integer) request.get("quantityDefect");
        String reportedBy = (String) request.get("reportedBy");
        String comments = (String) request.get("comments");
        
        WorkOrderProgress progress = workOrderService.reportProgress(
                workOrderId, quantityProduced, quantityDefect, reportedBy, comments);
        
        return ResponseEntity.ok(progress);
    }
    
    @Operation(summary = "작업 지시서 완료", description = "작업 지시서를 완료 처리합니다")
    @PostMapping("/{workOrderId}/complete")
    public ResponseEntity<WorkOrder> completeWorkOrder(
            @PathVariable Long workOrderId,
            @RequestBody Map<String, String> request) {
        
        String completedBy = request.get("completedBy");
        WorkOrder workOrder = workOrderService.completeWorkOrder(workOrderId, completedBy);
        return ResponseEntity.ok(workOrder);
    }
    
    @Operation(summary = "작업 지시서 보류", description = "작업 지시서를 보류 처리합니다")
    @PostMapping("/{workOrderId}/hold")
    public ResponseEntity<WorkOrder> holdWorkOrder(
            @PathVariable Long workOrderId,
            @RequestBody Map<String, String> request) {
        
        String reason = request.get("reason");
        WorkOrder workOrder = workOrderService.holdWorkOrder(workOrderId, reason);
        return ResponseEntity.ok(workOrder);
    }
    
    @Operation(summary = "작업 지시서 취소", description = "작업 지시서를 취소 처리합니다")
    @PostMapping("/{workOrderId}/cancel")
    public ResponseEntity<WorkOrder> cancelWorkOrder(
            @PathVariable Long workOrderId,
            @RequestBody Map<String, String> request) {
        
        String reason = request.get("reason");
        WorkOrder workOrder = workOrderService.cancelWorkOrder(workOrderId, reason);
        return ResponseEntity.ok(workOrder);
    }
    
    @Operation(summary = "오늘 스케줄된 작업 지시서 조회", description = "오늘 스케줄된 작업 지시서 목록을 조회합니다")
    @GetMapping("/today")
    public ResponseEntity<List<WorkOrder>> getTodayScheduledWorkOrders() {
        List<WorkOrder> workOrders = workOrderService.getTodayScheduledWorkOrders();
        return ResponseEntity.ok(workOrders);
    }
    
    @Operation(summary = "진행중인 작업 지시서 조회", description = "현재 진행중인 작업 지시서 목록을 조회합니다")
    @GetMapping("/in-progress")
    public ResponseEntity<List<WorkOrder>> getInProgressWorkOrders() {
        List<WorkOrder> workOrders = workOrderService.getInProgressWorkOrders();
        return ResponseEntity.ok(workOrders);
    }
    
    @Operation(summary = "지연된 작업 지시서 조회", description = "지연된 작업 지시서 목록을 조회합니다")
    @GetMapping("/delayed")
    public ResponseEntity<List<WorkOrder>> getDelayedWorkOrders() {
        List<WorkOrder> workOrders = workOrderService.getDelayedWorkOrders();
        return ResponseEntity.ok(workOrders);
    }
    
    @Operation(summary = "작업자별 작업 지시서 조회", description = "특정 작업자에게 할당된 작업 지시서를 조회합니다")
    @GetMapping("/by-operator/{operator}")
    public ResponseEntity<List<WorkOrder>> getWorkOrdersByOperator(@PathVariable String operator) {
        List<WorkOrder> workOrders = workOrderService.getWorkOrdersByOperator(operator);
        return ResponseEntity.ok(workOrders);
    }
    
    @Operation(summary = "작업 진행 이력 조회", description = "작업 지시서의 진행 이력을 조회합니다")
    @GetMapping("/{workOrderId}/progress-history")
    public ResponseEntity<List<WorkOrderProgress>> getWorkOrderProgressHistory(@PathVariable Long workOrderId) {
        List<WorkOrderProgress> progressHistory = workOrderService.getWorkOrderProgressHistory(workOrderId);
        return ResponseEntity.ok(progressHistory);
    }
} 