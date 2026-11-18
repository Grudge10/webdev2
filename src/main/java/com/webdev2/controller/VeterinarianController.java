package com.webdev2.controller;

import com.webdev2.dto.VeterinarianDTO;
import com.webdev2.service.VeterinarianService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/veterinarians")
public class VeterinarianController {
    private final VeterinarianService veterinarianService;

    public VeterinarianController(VeterinarianService veterinarianService) {
        this.veterinarianService = veterinarianService;
    }

    @GetMapping
    public List<VeterinarianDTO> getAllVeterinarians() {
        return veterinarianService.findAll();
    }
}
