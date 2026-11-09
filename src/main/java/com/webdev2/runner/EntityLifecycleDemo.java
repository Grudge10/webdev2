package com.webdev2.runner;

import com.webdev2.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EntityLifecycleDemo implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(EntityLifecycleDemo.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        if (count > 0) {
            return;
        }

        log.info("===== JPA Entity Lifecycle Demo =====");

        User user = new User("jdelacruz", "juan@example.com");
        log.info("[1] TRANSIENT -> {} | managed? {}", user, entityManager.contains(user));

        entityManager.persist(user);
        entityManager.flush();
        log.info("[2] MANAGED   -> {} | managed? {}", user, entityManager.contains(user));

        entityManager.detach(user);
        log.info("[3] DETACHED  -> {} | managed? {}", user, entityManager.contains(user));

        user.setEmail("changed@example.com");
        log.info("[4] Modified detached email -> {}", user.getEmail());

        User fromDb = entityManager.find(User.class, user.getId());
        log.info("[5] Java object email : {}", user.getEmail());
        log.info("[5] Database email    : {}", fromDb.getEmail());
        log.info("[5] Detached change persisted? {}", user.getEmail().equals(fromDb.getEmail()));
    }
}
