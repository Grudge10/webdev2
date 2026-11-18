package com.webdev2.service;

import com.webdev2.dto.OwnerDTO;
import com.webdev2.dto.OwnerRequest;
import com.webdev2.exception.ConflictException;
import com.webdev2.exception.ResourceNotFoundException;
import com.webdev2.model.Owner;
import com.webdev2.repository.OwnerRepository;
import com.webdev2.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;

    public OwnerService(OwnerRepository ownerRepository, PetRepository petRepository) {
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
    }

    public OwnerDTO toDTO(Owner owner) {
        return new OwnerDTO(owner.getId(), owner.getName(), owner.getPhone());
    }

    @Transactional(readOnly = true)
    public List<OwnerDTO> findAll() {
        return ownerRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public OwnerDTO findById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
        return toDTO(owner);
    }

    @Transactional
    public OwnerDTO create(OwnerRequest request) {
        Owner owner = new Owner(request.getName().trim(), cleanPhone(request.getPhone()));
        return toDTO(ownerRepository.save(owner));
    }

    @Transactional
    public OwnerDTO update(Long id, OwnerRequest request) {
        Owner existing = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
        existing.setName(request.getName().trim());
        existing.setPhone(cleanPhone(request.getPhone()));
        return toDTO(ownerRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!ownerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Owner not found with id: " + id);
        }
        if (petRepository.existsByOwnerId(id)) {
            throw new ConflictException("Cannot delete owner because they still have registered pets");
        }
        ownerRepository.deleteById(id);
    }

    private String cleanPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return phone.trim();
    }
}
