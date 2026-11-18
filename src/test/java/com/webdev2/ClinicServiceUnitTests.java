package com.webdev2;

import com.webdev2.dto.BookAppointmentRequest;
import com.webdev2.exception.ConflictException;
import com.webdev2.model.Pet;
import com.webdev2.model.Veterinarian;
import com.webdev2.repository.AppointmentRepository;
import com.webdev2.repository.OwnerRepository;
import com.webdev2.repository.PetRepository;
import com.webdev2.repository.VeterinarianRepository;
import com.webdev2.service.AppointmentService;
import com.webdev2.service.OwnerService;
import com.webdev2.service.PetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicServiceUnitTests {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @InjectMocks
    private OwnerService ownerService;

    @InjectMocks
    private PetService petService;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void rejectsOwnerDeleteWhenOwnerHasRegisteredPets() {
        when(ownerRepository.existsById(1L)).thenReturn(true);
        when(petRepository.existsByOwnerId(1L)).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> ownerService.delete(1L));
        assertTrue(ex.getMessage().contains("registered pets"));
        verify(ownerRepository, never()).deleteById(any());
    }

    @Test
    void rejectsAppointmentWhenVeterinarianHasNoAvailableSlots() {
        Veterinarian vet = new Veterinarian("Dr. Reyes", "Surgery", 0);
        vet.setId(1L);
        when(veterinarianRepository.findById(1L)).thenReturn(Optional.of(vet));
        when(veterinarianRepository.decrementSlotIfAvailable(1L)).thenReturn(0);

        BookAppointmentRequest req = new BookAppointmentRequest(
                1L, 1L, LocalDate.of(2026, 12, 1), "Checkup", new BigDecimal("500.00"));

        ConflictException ex = assertThrows(ConflictException.class, () -> appointmentService.bookAppointment(req));
        assertTrue(ex.getMessage().contains("No available slots"));
        verify(appointmentRepository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsAppointmentWhenPetAlreadyBookedOnSameDate() {
        Veterinarian vet = new Veterinarian("Dr. Reyes", "Surgery", 3);
        vet.setId(1L);
        Pet pet = new Pet("MC-1001", "Buddy", "Dog", "Retriever");
        pet.setId(1L);
        LocalDate date = LocalDate.of(2026, 11, 18);

        when(veterinarianRepository.findById(1L)).thenReturn(Optional.of(vet));
        when(veterinarianRepository.decrementSlotIfAvailable(1L)).thenReturn(1);
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(appointmentRepository.existsByPetIdAndAppointmentDate(1L, date)).thenReturn(true);

        BookAppointmentRequest req = new BookAppointmentRequest(1L, 1L, date, "Repeat", new BigDecimal("500.00"));
        assertThrows(ConflictException.class, () -> appointmentService.bookAppointment(req));
        verify(appointmentRepository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsUnsupportedSortFieldOnPetList() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> petService.findAll(Optional.empty(), 0, 5, "unknownField", "asc")
        );
        assertTrue(ex.getMessage().contains("Unsupported sort field"));
    }
}
