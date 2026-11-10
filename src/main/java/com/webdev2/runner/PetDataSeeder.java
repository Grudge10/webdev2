package com.webdev2.runner;

import com.webdev2.model.Pet;
import com.webdev2.repository.PetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PetDataSeeder implements CommandLineRunner {
    private final PetRepository petRepository;

    public PetDataSeeder(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public void run(String... args) {
        if (petRepository.count() == 0) {
            petRepository.save(new Pet("MC-1001", "Buddy", "Dog", "Golden Retriever"));
            petRepository.save(new Pet("MC-1002", "Luna", "Cat", "Siamese"));
            petRepository.save(new Pet("MC-1003", "Max", "Dog", "German Shepherd"));
            petRepository.save(new Pet("MC-1004", "Milo", "Cat", "Persian"));
        }

        for (Pet p : petRepository.findAll()) {
            System.out.println(p.getId() + " - " + p.getName() + " (" + p.getSpecies() + ")");
        }
    }
}
