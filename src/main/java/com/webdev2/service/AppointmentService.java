package com.webdev2.service;

import com.webdev2.dto.AppointmentDTO;
import com.webdev2.dto.BookAppointmentRequest;
import com.webdev2.exception.ConflictException;
import com.webdev2.exception.ResourceNotFoundException;
import com.webdev2.model.Appointment;
import com.webdev2.model.Pet;
import com.webdev2.model.Veterinarian;
import com.webdev2.repository.AppointmentRepository;
import com.webdev2.repository.PetRepository;
import com.webdev2.repository.VeterinarianRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final VeterinarianRepository veterinarianRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PetRepository petRepository,
            VeterinarianRepository veterinarianRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.veterinarianRepository = veterinarianRepository;
    }

    public AppointmentDTO toDTO(Appointment appointment) {
        Pet pet = appointment.getPet();
        String ownerName = pet.getOwner() != null ? pet.getOwner().getName() : null;
        Veterinarian vet = appointment.getVeterinarian();
        return new AppointmentDTO(
                appointment.getId(),
                appointment.getAppointmentDate(),
                appointment.getReason(),
                appointment.getFee(),
                pet.getId(),
                pet.getName(),
                ownerName,
                vet.getId(),
                vet.getName()
        );
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> findAll() {
        return appointmentRepository.findAllWithDetails().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public AppointmentDTO bookAppointment(BookAppointmentRequest request) {
        Veterinarian vet = veterinarianRepository.findById(request.getVetId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinarian not found with id: " + request.getVetId()));

        int changed = veterinarianRepository.decrementSlotIfAvailable(vet.getId());
        if (changed == 0) {
            throw new ConflictException("No available slots left for " + vet.getName());
        }

        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + request.getPetId()));

        if (appointmentRepository.existsByPetIdAndAppointmentDate(pet.getId(), request.getAppointmentDate())) {
            throw new ConflictException("Pet already has an appointment on " + request.getAppointmentDate());
        }

        Appointment appointment = new Appointment(
                pet,
                vet,
                request.getAppointmentDate(),
                request.getReason().trim(),
                request.getFee()
        );
        Appointment saved = appointmentRepository.saveAndFlush(appointment);
        return toDTO(saved);
    }
}
