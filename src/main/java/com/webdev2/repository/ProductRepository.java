package com.webdev2.repository;

import com.webdev2.model.Product;
import com.webdev2.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    List<Product> findByCategoryId(Long categoryId);

    Optional<Product> findBySku(String sku);

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.status = :status")
    List<Product> findByStatus(@Param("status") ProductStatus status);
}
