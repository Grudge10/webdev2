package com.webdev2.service;

import com.webdev2.dto.CreatePetRequest;
import com.webdev2.dto.PetDTO;
import com.webdev2.dto.UpdatePetRequest;
import com.webdev2.model.Pet;
import com.webdev2.repository.PetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
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

    public List<PetDTO> findAll() {
        List<PetDTO> dtos = new ArrayList<>();
        for (Pet pet : petRepository.findAll()) {
            dtos.add(toDTO(pet));
        }
        return dtos;
    }

    public Optional<PetDTO> findById(Long id) {
        return petRepository.findById(id).map(this::toDTO);
    }

    public boolean existsByMicrochipNumber(String microchipNumber) {
        return petRepository.existsByMicrochipNumber(microchipNumber);
    }

    public PetDTO create(CreatePetRequest request) {
        if (request.getMicrochipNumber() == null || request.getMicrochipNumber().isBlank()
                || request.getName() == null || request.getName().isBlank()
                || request.getSpecies() == null || request.getSpecies().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "microchipNumber, name, and species are required");
        }
        if (petRepository.existsByMicrochipNumber(request.getMicrochipNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Microchip number already exists");
        }
        Pet pet = new Pet(
                request.getMicrochipNumber(),
                request.getName(),
                request.getSpecies(),
                request.getBreed()
        );
        Pet saved = petRepository.save(pet);
        return toDTO(saved);
    }

    public Optional<PetDTO> update(Long id, UpdatePetRequest request) {
        Optional<Pet> optionalPet = petRepository.findById(id);
        if (optionalPet.isEmpty()) {
            return Optional.empty();
        }
        Pet existing = optionalPet.get();
        existing.setMicrochipNumber(request.getMicrochipNumber());
        existing.setName(request.getName());
        existing.setSpecies(request.getSpecies());
        existing.setBreed(request.getBreed());
        return Optional.of(toDTO(petRepository.save(existing)));
    }

    public Optional<PetDTO> patch(Long id, UpdatePetRequest request) {
        Optional<Pet> optionalPet = petRepository.findById(id);
        if (optionalPet.isEmpty()) {
            return Optional.empty();
        }
        Pet existing = optionalPet.get();
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
        return Optional.of(toDTO(petRepository.save(existing)));
    }

    public boolean delete(Long id) {
        if (!petRepository.existsById(id)) {
            return false;
        }
        petRepository.deleteById(id);
        return true;
    }
}
