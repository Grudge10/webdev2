package com.webdev2.service;

import com.webdev2.dto.CategoryRequestDTO;
import com.webdev2.dto.CategoryResponseDTO;
import com.webdev2.exception.ResourceNotFoundException;
import com.webdev2.model.Category;
import com.webdev2.repository.CategoryRepository;
import com.webdev2.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public CategoryResponseDTO toDTO(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return toDTO(category);
    }

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DataIntegrityViolationException("Category name already exists: " + request.getName());
        }
        Category category = new Category(request.getName(), request.getDescription());
        Category saved = categoryRepository.save(category);
        return toDTO(saved);
    }

    @Transactional
    public CategoryResponseDTO update(Long id, CategoryRequestDTO request) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        if (!existing.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DataIntegrityViolationException("Category name already exists: " + request.getName());
        }
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        return toDTO(categoryRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        if (productRepository.existsByCategoryId(id)) {
            throw new DataIntegrityViolationException("Cannot delete category with existing products");
        }
        categoryRepository.deleteById(id);
    }
}
