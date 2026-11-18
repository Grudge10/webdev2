package com.webdev2.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BookAppointmentRequest {

    @NotNull(message = "Pet is required")
    private Long petId;

    @NotNull(message = "Veterinarian is required")
    private Long vetId;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotBlank(message = "Reason is required")
    @Size(max = 255, message = "Reason must not exceed 255 characters")
    private String reason;

    @NotNull(message = "Fee is required")
    @DecimalMin(value = "0.00", message = "Fee cannot be negative")
    @Digits(integer = 6, fraction = 2, message = "Fee must have at most 2 decimal places")
    private BigDecimal fee;

    public BookAppointmentRequest() {
    }

    public BookAppointmentRequest(Long petId, Long vetId, LocalDate appointmentDate, String reason, BigDecimal fee) {
        this.petId = petId;
        this.vetId = vetId;
        this.appointmentDate = appointmentDate;
        this.reason = reason;
        this.fee = fee;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getVetId() {
        return vetId;
    }

    public void setVetId(Long vetId) {
        this.vetId = vetId;
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
}
