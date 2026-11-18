package com.webdev2.controller;

import com.webdev2.dto.AppointmentDTO;
import com.webdev2.dto.BookAppointmentRequest;
import com.webdev2.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentService.findAll();
    }

    @PostMapping("/book")
    public ResponseEntity<AppointmentDTO> bookAppointment(@Valid @RequestBody BookAppointmentRequest request) {
        AppointmentDTO created = appointmentService.bookAppointment(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/appointments")
                .build()
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
