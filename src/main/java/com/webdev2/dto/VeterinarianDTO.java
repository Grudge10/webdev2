package com.webdev2.dto;

public class VeterinarianDTO {
    private Long id;
    private String name;
    private String specialty;
    private int availableSlots;

    public VeterinarianDTO() {
    }

    public VeterinarianDTO(Long id, String name, String specialty, int availableSlots) {
        this.id = id;
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
}
