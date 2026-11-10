package com.webdev2.repository;

import com.webdev2.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findBySpeciesIgnoreCase(String species);

    List<Pet> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT DISTINCT p.species FROM Pet p")
    List<String> findAllSpecies();
}
