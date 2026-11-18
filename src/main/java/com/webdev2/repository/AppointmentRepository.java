package com.webdev2.repository;

import com.webdev2.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByPetId(Long petId);

    boolean existsByPetIdAndAppointmentDate(Long petId, LocalDate appointmentDate);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.pet p LEFT JOIN FETCH p.owner JOIN FETCH a.veterinarian ORDER BY a.appointmentDate ASC, a.id ASC")
    List<Appointment> findAllWithDetails();
}
