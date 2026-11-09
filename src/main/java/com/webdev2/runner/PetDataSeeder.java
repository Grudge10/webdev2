package com.webdev2.runner;

import com.webdev2.model.Pet;
import com.webdev2.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PetDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PetDataSeeder.class);

    private final PetRepository petRepository;

    public PetDataSeeder(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public void run(String... args) {
        if (petRepository.count() == 0) {
            petRepository.saveAll(List.of(
                    new Pet("MC-1001", "Buddy", "Dog", "Golden Retriever"),
                    new Pet("MC-1002", "Luna", "Cat", "Siamese"),
                    new Pet("MC-1003", "Max", "Dog", "German Shepherd"),
                    new Pet("MC-1004", "Milo", "Cat", "Persian")
            ));
        }

        List<Pet> pets = petRepository.findAll();
        log.info("Seeded pets from findAll() ({} records): {}", pets.size(), pets);
    }
}
