package com.webdev2.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Veterinarian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String specialty;

    @Column(nullable = false)
    private int availableSlots = 3;

    @OneToMany(mappedBy = "veterinarian", fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    public Veterinarian() {
    }

    public Veterinarian(String name, String specialty) {
        this(name, specialty, 3);
    }

    public Veterinarian(String name, String specialty, int availableSlots) {
        this.name = name;
        this.specialty = specialty;
        this.availableSlots = availableSlots;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = availableSlots;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
}
