package com.webdev2.controller;

import com.webdev2.model.Pet;
import com.webdev2.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<Pet> registerPet(@RequestBody Pet pet) {
        Pet saved = petService.registerPet(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Pet> getAllPets() {
        return petService.getAllPets();
    }

    @GetMapping("/{id}")
    public Pet getPetById(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    @PutMapping("/{id}")
    public Pet updatePet(@PathVariable Long id, @RequestBody Pet updated) {
        return petService.updatePet(id, updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removePet(@PathVariable Long id) {
        petService.removePet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/species")
    public List<Pet> getPetsBySpecies(@RequestParam("name") String name) {
        return petService.getPetsBySpecies(name);
    }

    @GetMapping("/search")
    public List<Pet> searchPetsByName(@RequestParam("keyword") String keyword) {
        return petService.searchPetsByName(keyword);
    }

    @GetMapping("/species-list")
    public List<String> getAllSpecies() {
        return petService.getAllSpecies();
    }
}
