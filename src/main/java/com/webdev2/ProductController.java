package com.webdev2;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final List<Product> products = new ArrayList<>();

    public ProductController() {
        products.add(new Product(1L, "Laptop", 999.99, new Category("Electronics")));
        products.add(new Product(2L, "Coffee Mug", 12.50, new Category("Kitchen")));
    }

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping("/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        Product product = products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (product == null)
            return "redirect:/products";

        model.addAttribute("product", product);
        return "product-detail";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }

    @PostMapping
    public String createProduct(@ModelAttribute Product product, BindingResult result) {
        if (result.hasErrors()) {
            return "product-form";
        }

        product.setId((long) products.size() + 1);
        if (product.getCategory() == null) {
            product.setCategory(new Category("General"));
        }
        products.add(product);

        return "redirect:/products";
    }
}