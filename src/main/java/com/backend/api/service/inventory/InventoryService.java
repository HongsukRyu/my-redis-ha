package com.backend.api.service.inventory;

import com.backend.api.model.inventory.Product;
import com.backend.api.model.inventory.StockTransaction;
import com.backend.api.repository.inventory.ProductRepository;
import com.backend.api.repository.inventory.StockTransactionRepository;
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
public class InventoryService {
    
    private final ProductRepository productRepository;
    private final StockTransactionRepository stockTransactionRepository;
    
    /**
     * 제품 등록
     */
    public Product createProduct(Product product) {
        // 제품 코드 중복 확인
        if (productRepository.existsByProductCode(product.getProductCode())) {
            throw new IllegalArgumentException("이미 존재하는 제품 코드입니다: " + product.getProductCode());
        }
        
        // 초기 재고는 0으로 설정
        product.setCurrentStock(0);
        
        log.info("새 제품 등록: {}", product.getProductCode());
        return productRepository.save(product);
    }
    
    /**
     * 재고 입고 처리
     */
    public void stockIn(Long productId, Integer quantity, String reason, String performedBy, String referenceNumber) {
        Product product = getProductById(productId);
        Integer stockBefore = product.getCurrentStock();
        Integer stockAfter = stockBefore + quantity;
        
        // 재고 업데이트
        product.setCurrentStock(stockAfter);
        productRepository.save(product);
        
        // 거래 이력 생성
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setTransactionType(StockTransaction.TransactionType.STOCK_IN);
        transaction.setQuantity(quantity);
        transaction.setStockBefore(stockBefore);
        transaction.setStockAfter(stockAfter);
        transaction.setReason(reason);
        transaction.setPerformedBy(performedBy);
        transaction.setReferenceNumber(referenceNumber);
        
        stockTransactionRepository.save(transaction);
        
        log.info("재고 입고 완료 - 제품: {}, 수량: {}, 입고 후 재고: {}", 
                product.getProductCode(), quantity, stockAfter);
    }
    
    /**
     * 재고 출고 처리
     */
    public void stockOut(Long productId, Integer quantity, String reason, String performedBy, String referenceNumber) {
        Product product = getProductById(productId);
        Integer stockBefore = product.getCurrentStock();
        
        // 재고 부족 확인
        if (stockBefore < quantity) {
            throw new IllegalArgumentException(
                    String.format("재고가 부족합니다. 현재 재고: %d, 요청 수량: %d", stockBefore, quantity));
        }
        
        Integer stockAfter = stockBefore - quantity;
        
        // 재고 업데이트
        product.setCurrentStock(stockAfter);
        productRepository.save(product);
        
        // 거래 이력 생성
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setTransactionType(StockTransaction.TransactionType.STOCK_OUT);
        transaction.setQuantity(quantity);
        transaction.setStockBefore(stockBefore);
        transaction.setStockAfter(stockAfter);
        transaction.setReason(reason);
        transaction.setPerformedBy(performedBy);
        transaction.setReferenceNumber(referenceNumber);
        
        stockTransactionRepository.save(transaction);
        
        log.info("재고 출고 완료 - 제품: {}, 수량: {}, 출고 후 재고: {}", 
                product.getProductCode(), quantity, stockAfter);
    }
    
    /**
     * 재고 조정
     */
    public void adjustStock(Long productId, Integer newQuantity, String reason, String performedBy) {
        Product product = getProductById(productId);
        Integer stockBefore = product.getCurrentStock();
        Integer adjustmentQuantity = newQuantity - stockBefore;
        
        // 재고 업데이트
        product.setCurrentStock(newQuantity);
        productRepository.save(product);
        
        // 거래 이력 생성
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setTransactionType(StockTransaction.TransactionType.ADJUSTMENT);
        transaction.setQuantity(Math.abs(adjustmentQuantity));
        transaction.setStockBefore(stockBefore);
        transaction.setStockAfter(newQuantity);
        transaction.setReason(reason);
        transaction.setPerformedBy(performedBy);
        
        stockTransactionRepository.save(transaction);
        
        log.info("재고 조정 완료 - 제품: {}, 조정 전: {}, 조정 후: {}", 
                product.getProductCode(), stockBefore, newQuantity);
    }
    
    /**
     * 재주문이 필요한 제품 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsNeedingReorder() {
        return productRepository.findProductsNeedingReorder();
    }
    
    /**
     * 재고 부족 제품 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }
    
    /**
     * 제품 검색
     */
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByProductNameContainingIgnoreCase(keyword);
    }
    
    /**
     * 제품 조회 (ID)
     */
    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다: " + productId));
    }
    
    /**
     * 제품 조회 (제품 코드)
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode);
    }
    
    /**
     * 모든 제품 조회
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    /**
     * 재고 거래 이력 조회
     */
    @Transactional(readOnly = true)
    public List<StockTransaction> getStockTransactionsByProduct(Long productId) {
        return stockTransactionRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }
} 