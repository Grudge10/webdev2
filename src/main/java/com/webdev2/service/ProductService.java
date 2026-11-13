package com.webdev2.service;

import com.webdev2.dto.ProductRequestDTO;
import com.webdev2.dto.ProductResponseDTO;
import com.webdev2.exception.ResourceNotFoundException;
import com.webdev2.model.Category;
import com.webdev2.model.Product;
import com.webdev2.model.ProductStatus;
import com.webdev2.repository.CategoryRepository;
import com.webdev2.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponseDTO toDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStatus(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAll(Optional<Long> categoryId, int page, int size, String sort, String direction) {
        String sortField = sort;
        String sortDir = direction;
        if (sort != null && sort.contains(",")) {
            String[] parts = sort.split(",");
            sortField = parts[0].trim();
            if (parts.length > 1) {
                sortDir = parts[1].trim();
            }
        }
        Sort sorting = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sorting);
        if (categoryId.isPresent()) {
            return productRepository.findByCategoryId(categoryId.get(), pageable).map(this::toDTO);
        }
        return productRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findByStatus(ProductStatus status) {
        return productRepository.findByStatus(status).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO request) {
        if (productRepository.existsBySkuIgnoreCase(request.getSku())) {
            throw new DataIntegrityViolationException("SKU already exists: " + request.getSku());
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        ProductStatus status = request.getStatus() != null ? request.getStatus() : ProductStatus.ACTIVE;
        Product product = new Product(
                request.getName(),
                request.getSku(),
                request.getPrice(),
                request.getStockQuantity(),
                status,
                category
        );
        return toDTO(productRepository.save(product));
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        if (!existing.getSku().equalsIgnoreCase(request.getSku())
                && productRepository.existsBySkuIgnoreCase(request.getSku())) {
            throw new DataIntegrityViolationException("SKU already exists: " + request.getSku());
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        existing.setName(request.getName());
        existing.setSku(request.getSku());
        existing.setPrice(request.getPrice());
        existing.setStockQuantity(request.getStockQuantity());
        existing.setStatus(request.getStatus() != null ? request.getStatus() : existing.getStatus());
        existing.setCategory(category);
        return toDTO(productRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
