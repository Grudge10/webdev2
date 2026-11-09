package com.webdev2.service;

import com.webdev2.model.Pet;
import com.webdev2.repository.PetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Transactional
    public Pet registerPet(Pet p) {
        if (p.getMicrochipNumber() == null || p.getMicrochipNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Microchip number is required");
        }
        if (p.getName() == null || p.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pet name is required");
        }
        if (p.getSpecies() == null || p.getSpecies().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Species is required");
        }
        p.setId(null);
        return petRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pet not found with id: " + id));
    }

    @Transactional
    public Pet updatePet(Long id, Pet updated) {
        Pet existing = getPetById(id);
        existing.setMicrochipNumber(updated.getMicrochipNumber());
        existing.setName(updated.getName());
        existing.setSpecies(updated.getSpecies());
        existing.setBreed(updated.getBreed());
        return petRepository.save(existing);
    }

    @Transactional
    public void removePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Pet not found with id: " + id);
        }
        petRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Pet> getPetsBySpecies(String species) {
        return petRepository.findBySpeciesIgnoreCase(species);
    }

    @Transactional(readOnly = true)
    public List<Pet> searchPetsByName(String keyword) {
        return petRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional(readOnly = true)
    public List<String> getAllSpecies() {
        return petRepository.findAllSpecies();
    }

    @Transactional
    public void batchRegister(List<Pet> pets) {
        for (Pet p : pets) {
            petRepository.save(p);
        }
    }
}
