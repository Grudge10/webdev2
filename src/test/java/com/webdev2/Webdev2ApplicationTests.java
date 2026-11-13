package com.webdev2;

import com.webdev2.dto.CategoryRequestDTO;
import com.webdev2.dto.CategoryResponseDTO;
import com.webdev2.dto.ProductRequestDTO;
import com.webdev2.dto.ProductResponseDTO;
import com.webdev2.exception.ResourceNotFoundException;
import com.webdev2.model.ProductStatus;
import com.webdev2.service.CategoryService;
import com.webdev2.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class Webdev2ApplicationTests {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Test
	void contextLoads() {
		CategoryResponseDTO cat = categoryService.create(new CategoryRequestDTO("TestCat", "Test description"));
		assertNotNull(cat.getId());

		ProductResponseDTO prod = productService.create(new ProductRequestDTO(
				"Test Keyboard",
				"PROD-999",
				new BigDecimal("49.99"),
				10,
				null,
				cat.getId()
		));
		assertNotNull(prod.getId());
		assertEquals(ProductStatus.ACTIVE, prod.getStatus());

		Page<ProductResponseDTO> page = productService.findAll(Optional.of(cat.getId()), 0, 5, "name", "asc");
		assertFalse(page.getContent().isEmpty());

		productService.delete(prod.getId());
		categoryService.delete(cat.getId());

		assertThrows(ResourceNotFoundException.class, () -> productService.findById(prod.getId()));
	}

}
