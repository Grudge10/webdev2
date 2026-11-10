package com.webdev2.controller;

import com.webdev2.dto.CreatePetRequest;
import com.webdev2.dto.ErrorResponse;
import com.webdev2.dto.PetDTO;
import com.webdev2.dto.UpdatePetRequest;
import com.webdev2.service.PetService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/pets")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public Page<PetDTO> getAllPets(
            @RequestParam Optional<String> species,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        int safeSize = Math.min(size, 100);
        return petService.findAll(species, page, safeSize, sort, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPetById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreatePetRequest request) {
        if (petService.existsByMicrochipNumber(request.getMicrochipNumber())) {
            ErrorResponse conflict = new ErrorResponse(
                    409,
                    "Microchip number already exists: " + request.getMicrochipNumber(),
                    System.currentTimeMillis(),
                    null
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflict);
        }
        PetDTO created = petService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> update(@PathVariable Long id, @Valid @RequestBody UpdatePetRequest request) {
        return ResponseEntity.ok(petService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PetDTO> patch(@PathVariable Long id, @Valid @RequestBody UpdatePetRequest request) {
        return ResponseEntity.ok(petService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
