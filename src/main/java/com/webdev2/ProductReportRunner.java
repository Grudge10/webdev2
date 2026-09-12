package com.webdev2;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component 
public class ProductReportRunner implements CommandLineRunner {
    private ProductService productService;

    public ProductReportRunner(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
        double threshold = 5000;
        String shopName = productService.getShopProperties().getName();
        String shopCurrency = productService.getShopProperties().getCurrency();
        List<Product> products = productService.getProductsFromThreshold(threshold);

        System.out.printf("""
                ================================
                PRODUCT REPORT
                ================================
                Shop: %s
                Currency: %s

                Products above %s %s:
                """, shopName, shopCurrency, shopCurrency, threshold);

        for (Product product : products) {
            System.out.println(product.getName() + " - " + shopCurrency + " " + (int) product.getPrice());
        }

        System.out.println("================================");
    }
}
