package com.webdev2;

import com.webdev2.model.Pet;
import com.webdev2.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class Webdev2ApplicationTests {

	@Autowired
	private PetRepository petRepository;

	@Test
	void contextLoads() {
		List<Pet> pets = petRepository.findAll();
		assertFalse(pets.isEmpty());
	}

}
