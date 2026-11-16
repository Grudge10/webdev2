package com.webdev2.service;

import com.webdev2.dto.OwnerDTO;
import com.webdev2.exception.ResourceNotFoundException;
import com.webdev2.model.Owner;
import com.webdev2.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OwnerService {
    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
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
}
