package com.backend.api.repository.inventory;

import com.backend.api.model.inventory.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 제품 코드로 조회
    Optional<Product> findByProductCode(String productCode);
    
    // 제품명으로 검색 (부분 일치)
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    
    // 카테고리별 조회
    List<Product> findByCategory(Product.ProductCategory category);
    
    // 상태별 조회
    List<Product> findByStatus(Product.ProductStatus status);
    
    // 재주문이 필요한 제품 조회 (현재 재고 <= 재주문 시점)
    @Query("SELECT p FROM Product p WHERE p.currentStock <= p.reorderPoint")
    List<Product> findProductsNeedingReorder();
    
    // 재고 부족 제품 조회 (현재 재고 < 최소 재고 수준)
    @Query("SELECT p FROM Product p WHERE p.currentStock < p.minStockLevel")
    List<Product> findLowStockProducts();
    
    // 재고 과다 제품 조회 (현재 재고 > 최대 재고 수준)
    @Query("SELECT p FROM Product p WHERE p.currentStock > p.maxStockLevel")
    List<Product> findOverStockProducts();
    
    // 공급업체별 제품 조회
    List<Product> findBySupplier(String supplier);
    
    // 위치별 제품 조회
    List<Product> findByLocation(String location);
    
    // 활성 상태 제품 조회
    List<Product> findByStatusOrderByProductNameAsc(Product.ProductStatus status);
    
    // 제품 코드 존재 여부 확인
    boolean existsByProductCode(String productCode);
    
    // 재고량으로 정렬된 제품 조회
    @Query("SELECT p FROM Product p ORDER BY p.currentStock ASC")
    List<Product> findAllOrderByCurrentStockAsc();
} 