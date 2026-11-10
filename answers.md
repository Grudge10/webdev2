# Lab 8: Database-Backed CRUD Application

## Task 1: Create the Repository Interface

### PetRepository Interface Code

```java
package com.webdev2.repository;

import com.webdev2.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
```

### Smoke-Test Output

Console output from `PetDataSeeder` calling `petRepository.findAll()`:

```text
Hibernate: select p1_0.id,p1_0.breed,p1_0.microchip_number,p1_0.name,p1_0.species from pet p1_0
1 - Buddy (Dog)
2 - Luna (Cat)
3 - Max (Dog)
4 - Milo (Cat)
```

MySQL CLI output (`SELECT * FROM pet;`):

```text
+----+------------------+------------------+-------+---------+
| id | breed            | microchip_number | name  | species |
+----+------------------+------------------+-------+---------+
|  1 | Golden Retriever | MC-1001          | Buddy | Dog     |
|  2 | Siamese          | MC-1002          | Luna  | Cat     |
|  3 | German Shepherd  | MC-1003          | Max   | Dog     |
|  4 | Persian          | MC-1004          | Milo  | Cat     |
+----+------------------+------------------+-------+---------+
```

## Task 2: Implement Full CRUD via a Service Layer

### PetService Code

```java
@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet registerPet(Pet p) {
        if (p.getMicrochipNumber() == null || p.getMicrochipNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Microchip number is required");
        }
        return petRepository.save(p);
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id: " + id));
    }

    public Pet updatePet(Long id, Pet updated) {
        Pet existing = getPetById(id);
        existing.setMicrochipNumber(updated.getMicrochipNumber());
        existing.setName(updated.getName());
        existing.setSpecies(updated.getSpecies());
        existing.setBreed(updated.getBreed());
        return petRepository.save(existing);
    }

    public void removePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id: " + id);
        }
        petRepository.deleteById(id);
    }
}
```

### PetController Code

```java
@RestController
@RequestMapping("/api/pets")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<Pet> registerPet(@RequestBody Pet pet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.registerPet(pet));
    }

    @GetMapping
    public List<Pet> getAllPets() {
        return petService.getAllPets();
    }

    @GetMapping("/{id}")
    public Pet getPetById(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    @PutMapping("/{id}")
    public Pet updatePet(@PathVariable Long id, @RequestBody Pet updated) {
        return petService.updatePet(id, updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removePet(@PathVariable Long id) {
        petService.removePet(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Request and Response Samples

1. POST `/api/pets` (201 Created)

Request:
```bash
curl -i -X POST http://localhost:8080/api/pets -H "Content-Type: application/json" -d '{"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Holland Lop"}'
```

Response:
```http
HTTP/1.1 201 
Content-Type: application/json

{"id":5,"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Holland Lop"}
```

2. GET `/api/pets` (200 OK)

Request:
```bash
curl -i http://localhost:8080/api/pets
```

Response:
```http
HTTP/1.1 200 
Content-Type: application/json

[{"id":1,"microchipNumber":"MC-1001","name":"Buddy","species":"Dog","breed":"Golden Retriever"},{"id":2,"microchipNumber":"MC-1002","name":"Luna","species":"Cat","breed":"Siamese"},{"id":3,"microchipNumber":"MC-1003","name":"Max","species":"Dog","breed":"German Shepherd"},{"id":4,"microchipNumber":"MC-1004","name":"Milo","species":"Cat","breed":"Persian"},{"id":5,"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Holland Lop"}]
```

3. GET `/api/pets/5` (200 OK) and GET `/api/pets/999` (404 Not Found)

Request:
```bash
curl -i http://localhost:8080/api/pets/5
```

Response:
```http
HTTP/1.1 200 
Content-Type: application/json

{"id":5,"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Holland Lop"}
```

Request for missing ID:
```bash
curl -i http://localhost:8080/api/pets/999
```

Response:
```http
HTTP/1.1 404 
Content-Type: application/json

{"timestamp":"2026-11-09T15:12:37.572Z","status":404,"error":"Not Found","path":"/api/pets/999"}
```

4. PUT `/api/pets/5` (200 OK)

Request:
```bash
curl -i -X PUT http://localhost:8080/api/pets/5 -H "Content-Type: application/json" -d '{"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Mini Lop"}'
```

Response:
```http
HTTP/1.1 200 
Content-Type: application/json

{"id":5,"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Mini Lop"}
```

5. DELETE `/api/pets/5` (204 No Content)

Request:
```bash
curl -i -X DELETE http://localhost:8080/api/pets/5
```

Response:
```http
HTTP/1.1 204 
```

## Task 3: Write Derived and Custom Query Methods

### Updated PetRepository Interface

```java
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
```

### Sample JSON Responses

1. `GET /api/pets/species?name=dog`

```json
[
  {"id":1,"microchipNumber":"MC-1001","name":"Buddy","species":"Dog","breed":"Golden Retriever"},
  {"id":3,"microchipNumber":"MC-1003","name":"Max","species":"Dog","breed":"German Shepherd"}
]
```

2. `GET /api/pets/search?keyword=lu`

```json
[
  {"id":2,"microchipNumber":"MC-1002","name":"Luna","species":"Cat","breed":"Siamese"}
]
```

3. `GET /api/pets/species-list`

```json
["Dog","Cat"]
```

## Task 4: Apply @Transactional and Observe Rollback

### batchRegister Method Code

```java
@Transactional
public void batchRegister(List<Pet> pets) {
    for (Pet p : pets) {
        petRepository.save(p);
    }
}
```

### SQL Log and MySQL Output

SQL log when sending two pets with the same microchip number (`MC-2001`):

```text
Hibernate: insert into pet (breed,microchip_number,name,species) values (?,?,?,?)
Hibernate: insert into pet (breed,microchip_number,name,species) values (?,?,?,?)
WARN  org.hibernate.orm.jdbc.error : HHH000247: ErrorCode: 1062, SQLState: 23000
WARN  org.hibernate.orm.jdbc.error : Duplicate entry 'MC-2001' for key 'pet.UKk92of6wx70ej50626uc0vvdm7'
ERROR o.a.c.c.C.[.[.[/].[dispatcherServlet] : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed: org.springframework.dao.DataIntegrityViolationException: could not execute statement [Duplicate entry 'MC-2001' for key 'pet.UKk92of6wx70ej50626uc0vvdm7']]
```

MySQL CLI output after the failed batch (`SELECT * FROM pet;`):

```text
+----+------------------+------------------+-------+---------+
| id | breed            | microchip_number | name  | species |
+----+------------------+------------------+-------+---------+
|  1 | Golden Retriever | MC-1001          | Buddy | Dog     |
|  2 | Siamese          | MC-1002          | Luna  | Cat     |
|  3 | German Shepherd  | MC-1003          | Max   | Dog     |
|  4 | Persian          | MC-1004          | Milo  | Cat     |
+----+------------------+------------------+-------+---------+
```

### Explanation

When running batchRegister, Hibernate logged an insert statement for inserting the first pet in the list (Rocky with MC-2001). When trying to insert the second pet (Shadow) with the same microchip number MC-2001, a duplicate entry error was thrown due to the unique constraint in MySQL. This caused a DataIntegrityViolationException to be thrown in Spring. As batchRegister is annotated with @Transactional, Spring rolled back the entire transaction due to the runtime exception being thrown. Therefore, the first pet Rocky was rolled back and not inserted into the database table.

## Task 5: Debug a Persistence Error

### Defects Found

1. The @GeneratedValue annotation is missing on the id field in Pet, so JPA will not auto-generate a new primary key.
2. The species (and breed) fields along with getters and setters are missing from Pet, causing errors in entity mapping. This causes any query methods in the repository that use species to fail on startup.
3. In the service call, p.setMicrochipNumber(null) is setting the microchipNumber to null when it has a @Column annotation with unique = true and nullable = false.

### Three-Step Debugging Process

The stack trace said: When species was removed from Pet, the application would crash on launch with QueryCreationException: Could not resolve attribute 'species' of 'com.webdev2.model.Pet' due to UnknownPathException. When species was added back in and a new Pet was saved without @GeneratedValue being included on the entity, I received a JpaSystemException: IdentifierGenerationException: The identifier of an instance of entity 'com.webdev2.model.Pet' is required to be assigned manually. When @GeneratedValue was added onto the entity and I tried to save(p) with p.setMicrochipNumber(null), I received a DataIntegrityViolationException: SQLIntegrityConstraintViolationException: Column 'microchip_number' cannot be null.

Looking at the SQL logs showed that when @GeneratedValue wasn't included, no insert into pet statement was shown as Hibernate didn't get to that point. When @GeneratedValue was added, the SQL logs showed that Hibernate: insert into pet (breed,microchip_number,name,species) values (?,?,?,?) was about to be run but then MySQL returned code 1048 (Column 'microchip_number' cannot be null).

I fixed this by adding @GeneratedValue(strategy = GenerationType.IDENTITY) to the id field under @Id. I also added the missing species and breed fields to Pet, along with the constructors, getters, and setters. Then I changed the service call to include a valid non-null microchipNumber, name, species, and breed.

### Fixed Pet Entity and Service Snippet

```java
@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String microchipNumber;

    private String name;

    private String species;

    private String breed;

    public Pet() {
    }

    public Pet(String microchipNumber, String name, String species, String breed) {
        this.microchipNumber = microchipNumber;
        this.name = name;
        this.species = species;
        this.breed = breed;
    }

    // getters and setters
}
```

```java
Pet p = new Pet("MC-1006", "Coco", "Dog", "Poodle");
petRepository.save(p);
```
