package com.webdev2;

import com.webdev2.dto.CreatePetRequest;
import com.webdev2.dto.PetDTO;
import com.webdev2.dto.UpdatePetRequest;
import com.webdev2.service.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class Webdev2ApplicationTests {

	@Autowired
	private PetService petService;

	@Test
	void contextLoads() {
		PetDTO created = petService.create(new CreatePetRequest("MC-3001", "Coco", "Dog", "Poodle"));
		assertNotNull(created.getId());

		assertTrue(petService.findById(created.getId()).isPresent());

		PetDTO updated = petService.update(created.getId(),
				new UpdatePetRequest("MC-3001", "Coco", "Dog", "Toy Poodle")).orElseThrow();
		assertEquals("Toy Poodle", updated.getBreed());

		UpdatePetRequest patchReq = new UpdatePetRequest();
		patchReq.setBreed("Miniature Poodle");
		PetDTO patched = petService.patch(created.getId(), patchReq).orElseThrow();
		assertEquals("Miniature Poodle", patched.getBreed());
		assertEquals("Coco", patched.getName());

		assertTrue(petService.delete(created.getId()));
		assertTrue(petService.findById(created.getId()).isEmpty());
	}

}
