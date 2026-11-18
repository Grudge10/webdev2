package com.webdev2.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AppointmentDTO {
    private Long id;
    private LocalDate appointmentDate;
    private String reason;
    private BigDecimal fee;
    private Long petId;
    private String petName;
    private String ownerName;
    private Long vetId;
    private String vetName;

    public AppointmentDTO() {
    }

    public AppointmentDTO(Long id, LocalDate appointmentDate, String reason, BigDecimal fee,
                          Long petId, String petName, String ownerName, Long vetId, String vetName) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.reason = reason;
        this.fee = fee;
        this.petId = petId;
        this.petName = petName;
        this.ownerName = ownerName;
        this.vetId = vetId;
        this.vetName = vetName;
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

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getVetId() {
        return vetId;
    }

    public void setVetId(Long vetId) {
        this.vetId = vetId;
    }

    public String getVetName() {
        return vetName;
    }

    public void setVetName(String vetName) {
        this.vetName = vetName;
    }
}
