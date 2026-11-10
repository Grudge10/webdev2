# WEBDEV2 — Lab 8: Database-Backed CRUD Application (`answers.md`)

---

## Task 1: Create the Repository Interface

### `PetRepository` Interface Code

```java
package com.webdev2.repository;

import com.webdev2.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
```

### Smoke-Test Output (`findAll()` Returning Seeded Records)

**Application Console Log (`PetDataSeeder`):**

```text
Hibernate: 
    select
        p1_0.id,
        p1_0.breed,
        p1_0.microchip_number,
        p1_0.name,
        p1_0.species 
    from
        pet p1_0
INFO 13171 --- [webdev2] [           main] com.webdev2.runner.PetDataSeeder : Seeded pets from findAll() (4 records): [Pet{id=1, microchipNumber='MC-1001', name='Buddy', species='Dog', breed='Golden Retriever'}, Pet{id=2, microchipNumber='MC-1002', name='Luna', species='Cat', breed='Siamese'}, Pet{id=3, microchipNumber='MC-1003', name='Max', species='Dog', breed='German Shepherd'}, Pet{id=4, microchipNumber='MC-1004', name='Milo', species='Cat', breed='Persian'}]
```

**MySQL CLI Verification (`USE webdev2_week8; DESCRIBE pet; SELECT * FROM pet;`):**

```text
+------------------+--------------+------+-----+---------+----------------+
| Field            | Type         | Null | Key | Default | Extra          |
+------------------+--------------+------+-----+---------+----------------+
| id               | bigint       | NO   | PRI | NULL    | auto_increment |
| breed            | varchar(255) | YES  |     | NULL    |                |
| microchip_number | varchar(255) | NO   | UNI | NULL    |                |
| name             | varchar(255) | NO   |     | NULL    |                |
| species          | varchar(255) | NO   |     | NULL    |                |
+------------------+--------------+------+-----+---------+----------------+

+----+------------------+------------------+-------+---------+
| id | breed            | microchip_number | name  | species |
+----+------------------+------------------+-------+---------+
|  1 | Golden Retriever | MC-1001          | Buddy | Dog     |
|  2 | Siamese          | MC-1002          | Luna  | Cat     |
|  3 | German Shepherd  | MC-1003          | Max   | Dog     |
|  4 | Persian          | MC-1004          | Milo  | Cat     |
+----+------------------+------------------+-------+---------+
```

---

## Task 2: Implement Full CRUD via a Service Layer

### `PetService` Code

```java
@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Transactional
    public Pet registerPet(Pet p) {
        if (p.getMicrochipNumber() == null || p.getMicrochipNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Microchip number is required");
        }
        if (p.getName() == null || p.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pet name is required");
        }
        if (p.getSpecies() == null || p.getSpecies().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Species is required");
        }
        p.setId(null);
        return petRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pet not found with id: " + id));
    }

    @Transactional
    public Pet updatePet(Long id, Pet updated) {
        Pet existing = getPetById(id);
        existing.setMicrochipNumber(updated.getMicrochipNumber());
        existing.setName(updated.getName());
        existing.setSpecies(updated.getSpecies());
        existing.setBreed(updated.getBreed());
        return petRepository.save(existing);
    }

    @Transactional
    public void removePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Pet not found with id: " + id);
        }
        petRepository.deleteById(id);
    }
}
```

### `PetController` Code

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
        Pet saved = petService.registerPet(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
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

### Request / Response Samples for All Five CRUD Operations

#### 1. `POST /api/pets` (Create — `201 Created`)

Request:
```bash
curl -i -X POST http://localhost:8080/api/pets \
  -H "Content-Type: application/json" \
  -d '{"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Holland Lop"}'
```

Response:
```http
HTTP/1.1 201 
Content-Type: application/json

{"id":5,"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Holland Lop"}
```

#### 2. `GET /api/pets` (Read All — `200 OK`)

Request:
```bash
curl -i http://localhost:8080/api/pets
```

Response:
```http
HTTP/1.1 200 
Content-Type: application/json

[
  {"id":1,"microchipNumber":"MC-1001","name":"Buddy","species":"Dog","breed":"Golden Retriever"},
  {"id":2,"microchipNumber":"MC-1002","name":"Luna","species":"Cat","breed":"Siamese"},
  {"id":3,"microchipNumber":"MC-1003","name":"Max","species":"Dog","breed":"German Shepherd"},
  {"id":4,"microchipNumber":"MC-1004","name":"Milo","species":"Cat","breed":"Persian"},
  {"id":5,"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Holland Lop"}
]
```

#### 3. `GET /api/pets/{id}` (Read One — `200 OK` and `404 Not Found`)

Request (existing ID):
```bash
curl -i http://localhost:8080/api/pets/5
```

Response:
```http
HTTP/1.1 200 
Content-Type: application/json

{"id":5,"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Holland Lop"}
```

Request (missing ID):
```bash
curl -i http://localhost:8080/api/pets/999
```

Response:
```http
HTTP/1.1 404 
Content-Type: application/json

{"timestamp":"2026-11-09T15:12:37.572Z","status":404,"error":"Not Found","path":"/api/pets/999"}
```

#### 4. `PUT /api/pets/{id}` (Update — `200 OK`)

Request:
```bash
curl -i -X PUT http://localhost:8080/api/pets/5 \
  -H "Content-Type: application/json" \
  -d '{"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Mini Lop"}'
```

Response:
```http
HTTP/1.1 200 
Content-Type: application/json

{"id":5,"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Mini Lop"}
```

#### 5. `DELETE /api/pets/{id}` (Delete — `204 No Content`)

Request:
```bash
curl -i -X DELETE http://localhost:8080/api/pets/5
```

Response:
```http
HTTP/1.1 204 
```

---

## Task 3: Write Derived & Custom Query Methods

### Updated `PetRepository` Interface

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

### Sample JSON Responses for the Three Query Endpoints

#### 1. `GET /api/pets/species?name=dog`

```json
[
  {"id":1,"microchipNumber":"MC-1001","name":"Buddy","species":"Dog","breed":"Golden Retriever"},
  {"id":3,"microchipNumber":"MC-1003","name":"Max","species":"Dog","breed":"German Shepherd"}
]
```

#### 2. `GET /api/pets/search?keyword=lu`

```json
[
  {"id":2,"microchipNumber":"MC-1002","name":"Luna","species":"Cat","breed":"Siamese"}
]
```

#### 3. `GET /api/pets/species-list`

```json
["Dog","Cat"]
```

---

## Task 4: Apply `@Transactional` and Observe Rollback

### `batchRegister` Method Code

```java
@Transactional
public void batchRegister(List<Pet> pets) {
    for (Pet p : pets) {
        petRepository.save(p);
    }
}
```

### Failed Batch Request

```bash
curl -i -X POST http://localhost:8080/api/pets/batch \
  -H "Content-Type: application/json" \
  -d '[
    {"microchipNumber":"MC-2001","name":"Rocky","species":"Dog","breed":"Beagle"},
    {"microchipNumber":"MC-2001","name":"Shadow","species":"Cat","breed":"Maine Coon"}
  ]'
```

### SQL Log Output and Exception Trace

```text
Hibernate: 
    insert 
    into
        pet
        (breed, microchip_number, name, species) 
    values
        (?, ?, ?, ?)
Hibernate: 
    insert 
    into
        pet
        (breed, microchip_number, name, species) 
    values
        (?, ?, ?, ?)
WARN  org.hibernate.orm.jdbc.error : HHH000247: ErrorCode: 1062, SQLState: 23000
WARN  org.hibernate.orm.jdbc.error : Duplicate entry 'MC-2001' for key 'pet.UKk92of6wx70ej50626uc0vvdm7'
ERROR o.a.c.c.C.[.[.[/].[dispatcherServlet] : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed: org.springframework.dao.DataIntegrityViolationException: could not execute statement [Duplicate entry 'MC-2001' for key 'pet.UKk92of6wx70ej50626uc0vvdm7'] [insert into pet (breed,microchip_number,name,species) values (?,?,?,?)]; SQL [insert into pet (breed,microchip_number,name,species) values (?,?,?,?)]; constraint [pet.UKk92of6wx70ej50626uc0vvdm7]] with root cause
java.sql.SQLIntegrityConstraintViolationException: Duplicate entry 'MC-2001' for key 'pet.UKk92of6wx70ej50626uc0vvdm7'
```

### MySQL CLI Output After the Failed Batch (`SELECT * FROM pet;`)

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

### Explanation of What Was Observed

When `batchRegister` ran, Hibernate executed the first `INSERT INTO pet` for `"Rocky"` (`MC-2001`) inside the active transaction opened by `@Transactional`. When the loop attempted the second `INSERT INTO pet` for `"Shadow"` with the duplicate `microchip_number` `"MC-2001"`, MySQL rejected the statement with error `1062 (23000): Duplicate entry 'MC-2001' for key 'pet.UKk92of6wx70ej50626uc0vvdm7'`, which Spring translated into an unchecked `DataIntegrityViolationException`. Because `batchRegister` is annotated with `@Transactional`, the unchecked exception escaping the method caused Spring's transaction interceptor to roll back the entire transaction, undoing the first `INSERT` for `"Rocky"` so zero rows from the failed batch were committed to the `pet` table.

---

## Task 5: Debug a Persistence Error

### Defects Identified in the Broken Snippet

1. **Missing `@GeneratedValue` on `@Id private Long id`:** Without `@GeneratedValue(strategy = GenerationType.IDENTITY)`, Hibernate expects the primary key to be assigned manually before `persist()` is called. Saving a new `Pet` with `id == null` fails with `org.springframework.orm.jpa.JpaSystemException: Identifier of entity 'com.webdev2.model.Pet' must be manually assigned before calling 'persist()'`.
2. **Omitted `species` (and `breed`) field and accessors on `Pet`:** Removing `species` breaks the domain mapping for the `pet` table, causes compile errors where `getSpecies()`/`setSpecies()` are used, and causes Spring Data JPA repository validation to fail at startup (`QueryCreationException` / `UnknownPathException: Could not resolve attribute 'species' of 'com.webdev2.model.Pet'`) when validating `findBySpeciesIgnoreCase` and `SELECT DISTINCT p.species FROM Pet p`.
3. **Null `microchipNumber` in the service call (`p.setMicrochipNumber(null)`):** The `microchipNumber` column is declared with `@Column(unique = true, nullable = false)`. Passing `null` (and leaving `name` and `species` unset) violates the `NOT NULL` column constraint in MySQL, throwing `org.springframework.dao.DataIntegrityViolationException` with root cause `java.sql.SQLIntegrityConstraintViolationException: Column 'microchip_number' cannot be null`.

### Three-Step Debugging Process

1. **Stack Trace Analysis:**
   - **Defect 1 (`species` omitted):** On startup, Spring Data JPA failed to create `petRepository`:
     ```text
     org.springframework.data.repository.query.QueryCreationException: Cannot create query for method [PetRepository.findAllSpecies()]; Query validation failed for 'SELECT DISTINCT p.species FROM Pet p'
     Caused by: org.hibernate.query.sqm.UnknownPathException: Could not resolve attribute 'species' of 'com.webdev2.model.Pet' [SELECT DISTINCT p.species FROM Pet p]
     ```
   - **Defect 2 (`@GeneratedValue` missing on `@Id`):** Calling `petRepository.save(p)` with `id == null` threw:
     ```text
     org.springframework.orm.jpa.JpaSystemException: Identifier of entity 'com.webdev2.model.Pet' must be manually assigned before calling 'persist()'
     Caused by: org.hibernate.id.IdentifierGenerationException: Identifier of entity 'com.webdev2.model.Pet' must be manually assigned before calling 'persist()'
     ```
   - **Defect 3 (`p.setMicrochipNumber(null)`):** After adding `@GeneratedValue(strategy = GenerationType.IDENTITY)`, calling `petRepository.save(p)` with `microchipNumber = null` threw:
     ```text
     WARN  org.hibernate.orm.jdbc.error : HHH000247: ErrorCode: 1048, SQLState: 23000
     WARN  org.hibernate.orm.jdbc.error : Column 'microchip_number' cannot be null
     org.springframework.dao.DataIntegrityViolationException: could not execute statement [Column 'microchip_number' cannot be null] [insert into pet (breed,microchip_number,name,species) values (?,?,?,?)]; SQL [insert into pet (breed,microchip_number,name,species) values (?,?,?,?)]; constraint [microchip_number]
     Caused by: org.hibernate.exception.ConstraintViolationException: could not execute statement [Column 'microchip_number' cannot be null] [insert into pet (breed,microchip_number,name,species) values (?,?,?,?)]
     Caused by: java.sql.SQLIntegrityConstraintViolationException: Column 'microchip_number' cannot be null
     ```
2. **SQL Log Correlation:**
   - With `spring.jpa.show-sql=true` and `logging.level.org.hibernate.SQL=DEBUG` enabled:
     - During the missing `@GeneratedValue` failure, **zero `insert into pet` statements appeared in the SQL log** because Hibernate aborted `persist()` before generating SQL when it saw `id == null` without an identifier generator.
     - Once `@GeneratedValue(strategy = GenerationType.IDENTITY)` was added, the SQL log printed `insert into pet (breed, microchip_number, name, species) values (?, ?, ?, ?)` immediately followed by `HHH000247: ErrorCode: 1048, SQLState: 23000` (`Column 'microchip_number' cannot be null`), confirming the `INSERT` reached MySQL and was rejected by the `NOT NULL` constraint on `microchip_number`.
3. **Targeted Fix:**
   - Added `@GeneratedValue(strategy = GenerationType.IDENTITY)` on `@Id private Long id` so MySQL `AUTO_INCREMENT` assigns the primary key.
   - Restored the `species` (`@Column(nullable = false)`) and `breed` fields, constructors, and getters/setters on `Pet`.
   - Updated the service call to supply non-null `microchipNumber`, `name`, `species`, and `breed` before calling `petRepository.save(p)`. After applying these fixes, Hibernate executed the `INSERT` statement and committed the row without errors.

### Fixed `Pet` Entity and Service Snippet

```java
@Entity
@Table(name = "pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String microchipNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
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
