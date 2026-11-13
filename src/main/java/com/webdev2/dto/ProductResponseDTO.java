package com.webdev2.dto;

import com.webdev2.model.ProductStatus;
import java.math.BigDecimal;

public class ProductResponseDTO {

    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private ProductStatus status;
    private Long categoryId;
    private String categoryName;

    public ProductResponseDTO() {
    }

    public ProductResponseDTO(Long id, String name, String sku, BigDecimal price, Integer stockQuantity, ProductStatus status, Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
