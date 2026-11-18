package com.webdev2;

import com.webdev2.dto.AppointmentDTO;
import com.webdev2.dto.BookAppointmentRequest;
import com.webdev2.exception.ResourceNotFoundException;
import com.webdev2.model.Veterinarian;
import com.webdev2.repository.AppointmentRepository;
import com.webdev2.repository.VeterinarianRepository;
import com.webdev2.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class Week16MysqlTransactionTests {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private VeterinarianRepository veterinarianRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Not annotated @Transactional so the test reads real committed/rolled-back MySQL rows across service boundaries.
    @Test
    void commitsAppointmentAndDecrementsVeterinarianSlotInMysql() {
        Veterinarian vet = veterinarianRepository.findById(1L).orElseThrow();
        int initialSlots = vet.getAvailableSlots();
        long initialAppts = appointmentRepository.count();

        AppointmentDTO booked = appointmentService.bookAppointment(
                new BookAppointmentRequest(4L, 1L, LocalDate.of(2026, 12, 15), "F4 Commit Test", new BigDecimal("700.00"))
        );

        assertNotNull(booked.getId());
        assertEquals(initialSlots - 1, veterinarianRepository.findById(1L).orElseThrow().getAvailableSlots());
        assertEquals(initialAppts + 1, appointmentRepository.count());

        appointmentRepository.deleteById(booked.getId());
        Veterinarian reset = veterinarianRepository.findById(1L).orElseThrow();
        reset.setAvailableSlots(initialSlots);
        veterinarianRepository.save(reset);
    }

    @Test
    void rollsBackVeterinarianSlotWhenSecondWriteFailsAfterDecrement() {
        Veterinarian vet = veterinarianRepository.findById(2L).orElseThrow();
        int initialSlots = vet.getAvailableSlots();
        long initialAppts = appointmentRepository.count();

        BookAppointmentRequest missingPetRequest = new BookAppointmentRequest(
                999999L, 2L, LocalDate.of(2026, 12, 16), "Rollback Test", new BigDecimal("500.00"));

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.bookAppointment(missingPetRequest));

        assertEquals(initialSlots, veterinarianRepository.findById(2L).orElseThrow().getAvailableSlots());
        assertEquals(initialAppts, appointmentRepository.count());
    }
}
