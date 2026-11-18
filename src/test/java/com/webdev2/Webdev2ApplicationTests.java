package com.webdev2;

import com.webdev2.dto.AppointmentDTO;
import com.webdev2.dto.BookAppointmentRequest;
import com.webdev2.dto.CreatePetRequest;
import com.webdev2.dto.OwnerDTO;
import com.webdev2.dto.OwnerRequest;
import com.webdev2.dto.PetDTO;
import com.webdev2.dto.UpdatePetRequest;
import com.webdev2.exception.ConflictException;
import com.webdev2.exception.ResourceNotFoundException;
import com.webdev2.model.Veterinarian;
import com.webdev2.repository.AppointmentRepository;
import com.webdev2.repository.VeterinarianRepository;
import com.webdev2.service.AppointmentService;
import com.webdev2.service.OwnerService;
import com.webdev2.service.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class Webdev2ApplicationTests {

	@Autowired
	private PetService petService;

	@Autowired
	private OwnerService ownerService;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private VeterinarianRepository veterinarianRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Test
	void petAndOwnerCrudWorksWithDeletePolicy() {
		OwnerDTO owner = ownerService.create(new OwnerRequest("Test Owner", "09170001111"));
		assertNotNull(owner.getId());

		PetDTO created = petService.create(new CreatePetRequest("MC-3001", "Coco", "Dog", "Poodle", owner.getId()));
		assertNotNull(created.getId());
		assertEquals(owner.getId(), created.getOwnerId());

		assertThrows(ConflictException.class, () -> ownerService.delete(owner.getId()));

		PetDTO found = petService.findById(created.getId());
		assertEquals("Coco", found.getName());

		Page<PetDTO> page = petService.findAll(Optional.of("Dog"), 0, 5, "name", "asc");
		assertFalse(page.getContent().isEmpty());

		PetDTO updated = petService.update(created.getId(),
				new UpdatePetRequest("MC-3001", "Coco", "Dog", "Toy Poodle", owner.getId()));
		assertEquals("Toy Poodle", updated.getBreed());

		UpdatePetRequest patchReq = new UpdatePetRequest();
		patchReq.setBreed("Miniature Poodle");
		PetDTO patched = petService.patch(created.getId(), patchReq);
		assertEquals("Miniature Poodle", patched.getBreed());
		assertEquals("Coco", patched.getName());

		petService.delete(created.getId());
		assertThrows(ResourceNotFoundException.class, () -> petService.findById(created.getId()));

		ownerService.delete(owner.getId());
		assertThrows(ResourceNotFoundException.class, () -> ownerService.findById(owner.getId()));
	}

	@Test
	void appointmentBookingCommitsAndRollsBackAfterFirstWrite() {
		Veterinarian vet = veterinarianRepository.findAll().get(0);
		int initialSlots = vet.getAvailableSlots();

		OwnerDTO owner = ownerService.create(new OwnerRequest("Booking Owner", "09180002222"));
		PetDTO pet = petService.create(new CreatePetRequest("MC-3002", "Koda", "Dog", "Shiba", owner.getId()));

		LocalDate bookingDate = LocalDate.of(2026, 12, 10);
		AppointmentDTO booked = appointmentService.bookAppointment(
				new BookAppointmentRequest(pet.getId(), vet.getId(), bookingDate, "Checkup", new BigDecimal("600.00"))
		);
		assertNotNull(booked.getId());
		assertEquals(initialSlots - 1, veterinarianRepository.findById(vet.getId()).orElseThrow().getAvailableSlots());

		assertThrows(ConflictException.class, () -> appointmentService.bookAppointment(
				new BookAppointmentRequest(pet.getId(), vet.getId(), bookingDate, "Duplicate Date", new BigDecimal("600.00"))
		));
		assertEquals(initialSlots - 1, veterinarianRepository.findById(vet.getId()).orElseThrow().getAvailableSlots());

		assertThrows(ConflictException.class, () -> petService.delete(pet.getId()));

		appointmentRepository.deleteById(booked.getId());
		Veterinarian resetVet = veterinarianRepository.findById(vet.getId()).orElseThrow();
		resetVet.setAvailableSlots(initialSlots);
		veterinarianRepository.save(resetVet);
		petService.delete(pet.getId());
		ownerService.delete(owner.getId());
	}
}
