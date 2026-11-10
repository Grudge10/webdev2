package com.webdev2;

import com.webdev2.dto.CreatePetRequest;
import com.webdev2.dto.PetDTO;
import com.webdev2.dto.UpdatePetRequest;
import com.webdev2.exception.ResourceNotFoundException;
import com.webdev2.service.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class Webdev2ApplicationTests {

	@Autowired
	private PetService petService;

	@Test
	void contextLoads() {
		PetDTO created = petService.create(new CreatePetRequest("MC-3001", "Coco", "Dog", "Poodle"));
		assertNotNull(created.getId());

		PetDTO found = petService.findById(created.getId());
		assertEquals("Coco", found.getName());

		Page<PetDTO> page = petService.findAll(Optional.of("Dog"), 0, 5, "name", "asc");
		assertFalse(page.getContent().isEmpty());

		PetDTO updated = petService.update(created.getId(),
				new UpdatePetRequest("MC-3001", "Coco", "Dog", "Toy Poodle"));
		assertEquals("Toy Poodle", updated.getBreed());

		UpdatePetRequest patchReq = new UpdatePetRequest();
		patchReq.setBreed("Miniature Poodle");
		PetDTO patched = petService.patch(created.getId(), patchReq);
		assertEquals("Miniature Poodle", patched.getBreed());
		assertEquals("Coco", patched.getName());

		petService.delete(created.getId());
		assertThrows(ResourceNotFoundException.class, () -> petService.findById(created.getId()));
	}

}
