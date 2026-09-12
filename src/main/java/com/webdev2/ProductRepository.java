package com.webdev2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {
    private List<Product> products = new ArrayList<>();

    public ProductRepository() {
        products.add(new Product("Laptop", 45000));
        products.add(new Product("Mouse", 800));
        products.add(new Product("Keyboard", 1500));
        products.add(new Product("Monitor", 12000));
        products.add(new Product("Headset", 2500));
        products.add(new Product("Printer", 8500));
    }

    public List<Product> findAll() {
        return products;
    }
}
