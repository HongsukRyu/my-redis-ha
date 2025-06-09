package com.backend.api.controller.inventory;

import com.backend.api.model.inventory.Product;
import com.backend.api.model.inventory.StockTransaction;
import com.backend.api.service.inventory.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "재고 관리", description = "제품 재고 관리 API")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    @Operation(summary = "제품 등록", description = "새로운 제품을 등록합니다")
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = inventoryService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }
    
    @Operation(summary = "모든 제품 조회", description = "모든 제품 목록을 조회합니다")
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = inventoryService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "제품 상세 조회", description = "제품 ID로 상세 정보를 조회합니다")
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        Product product = inventoryService.getProductById(productId);
        return ResponseEntity.ok(product);
    }
    
    @Operation(summary = "제품 검색", description = "제품명으로 제품을 검색합니다")
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = inventoryService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "재고 입고", description = "제품 재고를 입고 처리합니다")
    @PostMapping("/products/{productId}/stock-in")
    public ResponseEntity<String> stockIn(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> request) {
        
        Integer quantity = (Integer) request.get("quantity");
        String reason = (String) request.get("reason");
        String performedBy = (String) request.get("performedBy");
        String referenceNumber = (String) request.get("referenceNumber");
        
        inventoryService.stockIn(productId, quantity, reason, performedBy, referenceNumber);
        
        return ResponseEntity.ok("재고 입고가 완료되었습니다.");
    }
    
    @Operation(summary = "재고 출고", description = "제품 재고를 출고 처리합니다")
    @PostMapping("/products/{productId}/stock-out")
    public ResponseEntity<String> stockOut(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> request) {
        
        Integer quantity = (Integer) request.get("quantity");
        String reason = (String) request.get("reason");
        String performedBy = (String) request.get("performedBy");
        String referenceNumber = (String) request.get("referenceNumber");
        
        inventoryService.stockOut(productId, quantity, reason, performedBy, referenceNumber);
        
        return ResponseEntity.ok("재고 출고가 완료되었습니다.");
    }
    
    @Operation(summary = "재고 조정", description = "제품 재고를 조정합니다")
    @PostMapping("/products/{productId}/adjust")
    public ResponseEntity<String> adjustStock(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> request) {
        
        Integer newQuantity = (Integer) request.get("newQuantity");
        String reason = (String) request.get("reason");
        String performedBy = (String) request.get("performedBy");
        
        inventoryService.adjustStock(productId, newQuantity, reason, performedBy);
        
        return ResponseEntity.ok("재고 조정이 완료되었습니다.");
    }
    
    @Operation(summary = "재주문 필요 제품 조회", description = "재주문이 필요한 제품 목록을 조회합니다")
    @GetMapping("/products/reorder-needed")
    public ResponseEntity<List<Product>> getProductsNeedingReorder() {
        List<Product> products = inventoryService.getProductsNeedingReorder();
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "재고 부족 제품 조회", description = "재고가 부족한 제품 목록을 조회합니다")
    @GetMapping("/products/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        List<Product> products = inventoryService.getLowStockProducts();
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "재고 거래 이력 조회", description = "제품의 재고 거래 이력을 조회합니다")
    @GetMapping("/products/{productId}/transactions")
    public ResponseEntity<List<StockTransaction>> getStockTransactions(@PathVariable Long productId) {
        List<StockTransaction> transactions = inventoryService.getStockTransactionsByProduct(productId);
        return ResponseEntity.ok(transactions);
    }
    
    @Operation(summary = "제품 코드로 조회", description = "제품 코드로 제품 정보를 조회합니다")
    @GetMapping("/products/by-code/{productCode}")
    public ResponseEntity<Product> getProductByCode(@PathVariable String productCode) {
        return inventoryService.getProductByCode(productCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 