package com.webdev2.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "appointment",
        uniqueConstraints = @UniqueConstraint(name = "uq_appointment_pet_date", columnNames = {"pet_id", "appointment_date"})
)
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal fee = new BigDecimal("500.00");

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vet_id", nullable = false)
    private Veterinarian veterinarian;

    public Appointment() {
    }

    public Appointment(Pet pet, Veterinarian veterinarian, LocalDate appointmentDate, String reason) {
        this(pet, veterinarian, appointmentDate, reason, new BigDecimal("500.00"));
    }

    public Appointment(Pet pet, Veterinarian veterinarian, LocalDate appointmentDate, String reason, BigDecimal fee) {
        this.pet = pet;
        this.veterinarian = veterinarian;
        this.appointmentDate = appointmentDate;
        this.reason = reason;
        this.fee = fee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Veterinarian getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(Veterinarian veterinarian) {
        this.veterinarian = veterinarian;
    }
}
