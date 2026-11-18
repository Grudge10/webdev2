package com.webdev2.service;

import com.webdev2.dto.CreatePetRequest;
import com.webdev2.dto.PetDTO;
import com.webdev2.dto.UpdatePetRequest;
import com.webdev2.exception.ConflictException;
import com.webdev2.exception.ResourceNotFoundException;
import com.webdev2.model.Owner;
import com.webdev2.model.Pet;
import com.webdev2.repository.AppointmentRepository;
import com.webdev2.repository.OwnerRepository;
import com.webdev2.repository.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class PetService {
    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;
    private final AppointmentRepository appointmentRepository;

    public PetService(PetRepository petRepository, OwnerRepository ownerRepository, AppointmentRepository appointmentRepository) {
        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public PetDTO toDTO(Pet pet) {
        Long ownerId = pet.getOwner() != null ? pet.getOwner().getId() : null;
        String ownerName = pet.getOwner() != null ? pet.getOwner().getName() : null;
        return new PetDTO(
                pet.getId(),
                pet.getMicrochipNumber(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                ownerId,
                ownerName
        );
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public PetDTO findById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
        return toDTO(pet);
    }

    public boolean existsByMicrochipNumber(String microchipNumber) {
        return petRepository.existsByMicrochipNumber(microchipNumber);
    }

    @Transactional
    public PetDTO create(CreatePetRequest request) {
        String microchip = request.getMicrochipNumber().trim();
        if (petRepository.existsByMicrochipNumber(microchip)) {
            throw new ConflictException("Microchip number already exists: " + microchip);
        }
        Pet pet = new Pet(
                microchip,
                request.getName().trim(),
                request.getSpecies().trim(),
                cleanOptional(request.getBreed())
        );
        pet.setOwner(resolveOwner(request.getOwnerId()));
        Pet saved = petRepository.save(pet);
        return toDTO(saved);
    }

    @Transactional
    public PetDTO update(Long id, UpdatePetRequest request) {
        Pet existing = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
        if (isBlank(request.getMicrochipNumber()) || isBlank(request.getName()) || isBlank(request.getSpecies())) {
            throw new IllegalArgumentException("Microchip number, name, and species are required for PUT");
        }
        String microchip = request.getMicrochipNumber().trim();
        if (petRepository.existsByMicrochipNumberAndIdNot(microchip, id)) {
            throw new ConflictException("Microchip number already exists: " + microchip);
        }
        existing.setMicrochipNumber(microchip);
        existing.setName(request.getName().trim());
        existing.setSpecies(request.getSpecies().trim());
        existing.setBreed(cleanOptional(request.getBreed()));
        existing.setOwner(resolveOwner(request.getOwnerId()));
        return toDTO(petRepository.save(existing));
    }

    @Transactional
    public PetDTO patch(Long id, UpdatePetRequest request) {
        Pet existing = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
        if (request.getMicrochipNumber() != null) {
            if (request.getMicrochipNumber().isBlank()) {
                throw new IllegalArgumentException("Microchip number cannot be blank");
            }
            String microchip = request.getMicrochipNumber().trim();
            if (petRepository.existsByMicrochipNumberAndIdNot(microchip, id)) {
                throw new ConflictException("Microchip number already exists: " + microchip);
            }
            existing.setMicrochipNumber(microchip);
        }
        if (request.getName() != null) {
            if (request.getName().isBlank()) {
                throw new IllegalArgumentException("Name cannot be blank");
            }
            existing.setName(request.getName().trim());
        }
        if (request.getSpecies() != null) {
            if (request.getSpecies().isBlank()) {
                throw new IllegalArgumentException("Species cannot be blank");
            }
            existing.setSpecies(request.getSpecies().trim());
        }
        if (request.getBreed() != null) {
            existing.setBreed(cleanOptional(request.getBreed()));
        }
        if (request.getOwnerId() != null) {
            existing.setOwner(resolveOwner(request.getOwnerId()));
        }
        return toDTO(petRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pet not found with id: " + id);
        }
        if (appointmentRepository.existsByPetId(id)) {
            throw new ConflictException("Cannot delete pet because it has existing appointments");
        }
        petRepository.deleteById(id);
    }

    private Owner resolveOwner(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String cleanOptional(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
