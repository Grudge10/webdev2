package com.webdev2.service;

import com.webdev2.dto.VeterinarianDTO;
import com.webdev2.model.Veterinarian;
import com.webdev2.repository.VeterinarianRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VeterinarianService {
    private final VeterinarianRepository veterinarianRepository;

    public VeterinarianService(VeterinarianRepository veterinarianRepository) {
        this.veterinarianRepository = veterinarianRepository;
    }

    public VeterinarianDTO toDTO(Veterinarian vet) {
        return new VeterinarianDTO(vet.getId(), vet.getName(), vet.getSpecialty(), vet.getAvailableSlots());
    }

    @Transactional(readOnly = true)
    public List<VeterinarianDTO> findAll() {
        return veterinarianRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }
}
