package com.webdev2.service;

import com.webdev2.dto.CreatePetRequest;
import com.webdev2.dto.PetDTO;
import com.webdev2.dto.UpdatePetRequest;
import com.webdev2.exception.ResourceNotFoundException;
import com.webdev2.model.Pet;
import com.webdev2.repository.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public PetDTO toDTO(Pet pet) {
        return new PetDTO(
                pet.getId(),
                pet.getMicrochipNumber(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed()
        );
    }

    public Page<PetDTO> findAll(Optional<String> species, int page, int size, String sort, String direction) {
        String sortField = sort;
        String sortDir = direction;
        if (sort != null && sort.contains(",")) {
            String[] parts = sort.split(",");
            sortField = parts[0].trim();
            if (parts.length > 1) {
                sortDir = parts[1].trim();
            }
        }
        Sort sorting = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sorting);
        if (species.isPresent() && !species.get().isBlank()) {
            return petRepository.findBySpeciesIgnoreCase(species.get(), pageable).map(this::toDTO);
        }
        return petRepository.findAll(pageable).map(this::toDTO);
    }

    public PetDTO findById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
        return toDTO(pet);
    }

    public boolean existsByMicrochipNumber(String microchipNumber) {
        return petRepository.existsByMicrochipNumber(microchipNumber);
    }

    public PetDTO create(CreatePetRequest request) {
        Pet pet = new Pet(
                request.getMicrochipNumber(),
                request.getName(),
                request.getSpecies(),
                request.getBreed()
        );
        Pet saved = petRepository.save(pet);
        return toDTO(saved);
    }

    public PetDTO update(Long id, UpdatePetRequest request) {
        Pet existing = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
        existing.setMicrochipNumber(request.getMicrochipNumber());
        existing.setName(request.getName());
        existing.setSpecies(request.getSpecies());
        existing.setBreed(request.getBreed());
        return toDTO(petRepository.save(existing));
    }

    public PetDTO patch(Long id, UpdatePetRequest request) {
        Pet existing = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
        if (request.getMicrochipNumber() != null) {
            existing.setMicrochipNumber(request.getMicrochipNumber());
        }
        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getSpecies() != null) {
            existing.setSpecies(request.getSpecies());
        }
        if (request.getBreed() != null) {
            existing.setBreed(request.getBreed());
        }
        return toDTO(petRepository.save(existing));
    }

    public void delete(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pet not found with id: " + id);
        }
        petRepository.deleteById(id);
    }
}
