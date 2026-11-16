package com.webdev2.runner;

import com.webdev2.model.Owner;
import com.webdev2.model.Pet;
import com.webdev2.repository.OwnerRepository;
import com.webdev2.repository.PetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
public class PetDataSeeder implements CommandLineRunner {
    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;

    public PetDataSeeder(PetRepository petRepository, OwnerRepository ownerRepository) {
        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (ownerRepository.count() == 0) {
            ownerRepository.save(new Owner("Maria Santos", "09181112222"));
            ownerRepository.save(new Owner("Carlos Mendoza", "09172223333"));
        }
        if (petRepository.count() == 0) {
            petRepository.save(new Pet("MC-1001", "Buddy", "Dog", "Golden Retriever"));
            petRepository.save(new Pet("MC-1002", "Luna", "Cat", "Siamese"));
            petRepository.save(new Pet("MC-1003", "Max", "Dog", "German Shepherd"));
            petRepository.save(new Pet("MC-1004", "Milo", "Cat", "Persian"));
        }
        List<Owner> owners = ownerRepository.findAll();
        if (!owners.isEmpty()) {
            int i = 0;
            for (Pet p : petRepository.findAll()) {
                if (p.getOwner() == null) {
                    p.setOwner(owners.get(i % owners.size()));
                    petRepository.save(p);
                }
                i++;
            }
        }
    }
}
