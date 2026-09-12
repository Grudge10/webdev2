package com.webdev2;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ShopProperties shopProperties;

    public ProductService(ProductRepository productRepository, ShopProperties shopProperties) {
        this.productRepository = productRepository;
        this.shopProperties = shopProperties;
    }

    public ShopProperties getShopProperties() {
        return shopProperties;
    }

    public List<Product> getProductsFromThreshold(double threshold) {
        return productRepository.findAll().stream()
                .filter(product -> product.getPrice() > threshold)
                .toList();
    }
}
