package com.webdev2.repository;

import com.webdev2.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {
    @Modifying
    @Query("UPDATE Veterinarian v SET v.availableSlots = v.availableSlots - 1 WHERE v.id = :id AND v.availableSlots > 0")
    int decrementSlotIfAvailable(@Param("id") Long id);
}
