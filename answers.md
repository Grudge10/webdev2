# WEBDEV2 — Week 10 Lab Answers

Name: Jan Liam E. Arias
Student ID: 2510499
GitHub repo: https://github.com/Grudge10/webdev2
Date submitted: 2026-11-10

---

## Task 1 — URL Design & Controller Skeleton

**URL design table (method, path, expected status codes):**

| HTTP Method | URL Path | Expected Status Codes |
|---|---|---|
| GET | `/api/pets` | `200` |
| GET | `/api/pets/{id}` | `200`, `404` |
| POST | `/api/pets` | `201`, `400`, `409` |
| PUT | `/api/pets/{id}` | `200`, `404`, `400` |
| PATCH | `/api/pets/{id}` | `200`, `404`, `400` |
| DELETE | `/api/pets/{id}` | `204`, `404` |

**Controller skeleton code:**
```java
package com.webdev2.controller;

import com.webdev2.dto.CreatePetRequest;
import com.webdev2.dto.PetDTO;
import com.webdev2.dto.UpdatePetRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @GetMapping
    public List<PetDTO> getAllPets() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPetById(@PathVariable Long id) {
        return null;
    }

    @PostMapping
    public ResponseEntity<PetDTO> create(@RequestBody CreatePetRequest request) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> update(@PathVariable Long id, @RequestBody UpdatePetRequest request) {
        return null;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PetDTO> patch(@PathVariable Long id, @RequestBody UpdatePetRequest request) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return null;
    }
}
```

**Evidence (screenshot filename or curl output):**
```text
$ curl -i http://localhost:8080/api/pets
HTTP/1.1 200 
Content-Length: 0
Date: Tue, 10 Nov 2026 06:10:00 GMT
```

**Reflection question:**
_Why do REST URLs use plural nouns (`/api/students`) rather than verb phrases (`/api/getStudents`), and what role does the HTTP method play?_

Your answer:
RESTful URLs are written in the plural form as URLs should indicate resources (or a collection of resources). The action to be taken on the resource is indicated by the HTTP method used (GET, POST, PUT, PATCH, DELETE). Using verbs in the URL would be redundant and breaks the RESTful convention.

---

## Task 2 — GET Endpoints — List & Get by ID

**Approach / explanation:**
GET /api/pets has been implemented to get the list of all pets by calling petService.findAll(), returning a List<PetDTO> with status 200 OK. If there are no records in the database then it will return an empty JSON array [].
GET /api/pets/{id} uses petService.findById(id) and then maps it to ResponseEntity::ok or ResponseEntity.notFound().build(). This will return a 200 OK if the pet is found and 404 Not Found if not.
In my PetDTO class I only have the id, microchipNumber, name, species, and breed fields exposed from the Pet entity, not exposing the owner or appointments relationships in JPA.

**`YourResourceDTO` class code:**
```java
package com.webdev2.dto;

public class PetDTO {
    private Long id;
    private String microchipNumber;
    private String name;
    private String species;
    private String breed;

    public PetDTO() {
    }

    public PetDTO(Long id, String microchipNumber, String name, String species, String breed) {
        this.id = id;
        this.microchipNumber = microchipNumber;
        this.name = name;
        this.species = species;
        this.breed = breed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMicrochipNumber() {
        return microchipNumber;
    }

    public void setMicrochipNumber(String microchipNumber) {
        this.microchipNumber = microchipNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }
}
```

**`toDTO()` mapping method code:**
```java
public PetDTO toDTO(Pet pet) {
    return new PetDTO(
            pet.getId(),
            pet.getMicrochipNumber(),
            pet.getName(),
            pet.getSpecies(),
            pet.getBreed()
    );
}
```

**Evidence (screenshot filename or curl/Postman output):**
```text
$ curl -i http://localhost:8080/api/pets
HTTP/1.1 200 
Content-Type: application/json

[{"id":1,"microchipNumber":"MC-1001","name":"Buddy","species":"Dog","breed":"Golden Retriever"},{"id":2,"microchipNumber":"MC-1002","name":"Luna","species":"Cat","breed":"Siamese"},{"id":3,"microchipNumber":"MC-1003","name":"Max","species":"Dog","breed":"German Shepherd"},{"id":4,"microchipNumber":"MC-1004","name":"Milo","species":"Cat","breed":"Persian"}]

$ curl -i http://localhost:8080/api/pets/1
HTTP/1.1 200 
Content-Type: application/json

{"id":1,"microchipNumber":"MC-1001","name":"Buddy","species":"Dog","breed":"Golden Retriever"}

$ curl -i http://localhost:8080/api/pets/999
HTTP/1.1 404 
Content-Length: 0
```

**Reflection question:**
_What would go wrong if you returned the JPA entity directly from `@GetMapping("/{id}")` instead of a DTO?_

Your answer:
Exposing your JPA entity directly may expose internal DB fields and associations. It can cause a LazyInitializationException or infinite JSON recursion as Jackson will attempt to serialize any lazy associations (such as owner and appointments) after the transaction has already completed. It also creates a tight coupling between your public JSON API and the DB table structure. If you change the name of a field in your entity then API clients will break.

---

## Task 3 — POST Endpoint — Create with 201 + Location

**Approach / explanation:**
I made a CreatePetRequest object which only had the fields microchipNumber, name, species, and breed. On the create method in PetController I used ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri() to make the Location URI and then I returned ResponseEntity.created(location).body(created) to get a 201 Created response. The create method inside PetService converts the CreatePetRequest object into a new Pet entity and saves it using the petRepository.save(pet) method. Then it converts the saved Pet entity into a PetDTO using toDTO(saved).

**`CreateYourResourceRequest` class code:**
```java
package com.webdev2.dto;

public class CreatePetRequest {
    private String microchipNumber;
    private String name;
    private String species;
    private String breed;

    public CreatePetRequest() {
    }

    public CreatePetRequest(String microchipNumber, String name, String species, String breed) {
        this.microchipNumber = microchipNumber;
        this.name = name;
        this.species = species;
        this.breed = breed;
    }

    public String getMicrochipNumber() {
        return microchipNumber;
    }

    public void setMicrochipNumber(String microchipNumber) {
        this.microchipNumber = microchipNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }
}
```

**Controller `create()` method code:**
```java
@PostMapping
public ResponseEntity<?> create(@RequestBody CreatePetRequest request) {
    if (request.getMicrochipNumber() != null
            && petService.existsByMicrochipNumber(request.getMicrochipNumber())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Microchip number already exists: " + request.getMicrochipNumber()));
    }
    PetDTO created = petService.create(request);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getId())
            .toUri();
    return ResponseEntity.created(location).body(created);
}
```

**Evidence (screenshot filename or curl/Postman output):**
```text
$ curl -i -X POST http://localhost:8080/api/pets \
  -H "Content-Type: application/json" \
  -d '{"microchipNumber":"MC-2010","name":"Coco","species":"Dog","breed":"Poodle"}'

HTTP/1.1 201 
Location: http://localhost:8080/api/pets/6
Content-Type: application/json

{"id":6,"microchipNumber":"MC-2010","name":"Coco","species":"Dog","breed":"Poodle"}
```

**Reflection question:**
_Why does the REST contract specify `201 Created` for a successful POST rather than `200 OK`, and why is the `Location` header useful to include?_

Your answer:
201 Created indicates that a new resource has been created on the server. This is different from responses such as 200 OK which only indicate that existing data has been returned. The Location header helps by providing the client with the exact URL of the new resource, for example /api/pets/6. This allows the client to go directly to the new resource.

---

## Task 4 — PUT, PATCH & DELETE

**Approach / explanation:**
The update() method handles the PUT request and replaces all four updatable fields (microchipNumber, name, species, breed) of the loaded Pet entity with their corresponding values from the UpdatePetRequest. The patch() method handles the PATCH request and only updates fields that are not null from the UpdatePetRequest by checking if each field is != null. The delete() method first checks if the pet exists in the database using petRepository.existsById(id). This means the first time a DELETE is called it will return a 204 No Content, and if DELETE is called again with the same ID it will return a 404 Not Found.

**`UpdateYourResourceRequest` class code:**
```java
package com.webdev2.dto;

public class UpdatePetRequest {
    private String microchipNumber;
    private String name;
    private String species;
    private String breed;

    public UpdatePetRequest() {
    }

    public UpdatePetRequest(String microchipNumber, String name, String species, String breed) {
        this.microchipNumber = microchipNumber;
        this.name = name;
        this.species = species;
        this.breed = breed;
    }

    public String getMicrochipNumber() {
        return microchipNumber;
    }

    public void setMicrochipNumber(String microchipNumber) {
        this.microchipNumber = microchipNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }
}
```

**Controller code for PUT, PATCH, DELETE:**
```java
@PutMapping("/{id}")
public ResponseEntity<PetDTO> update(@PathVariable Long id, @RequestBody UpdatePetRequest request) {
    return petService.update(id, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@PatchMapping("/{id}")
public ResponseEntity<PetDTO> patch(@PathVariable Long id, @RequestBody UpdatePetRequest request) {
    return petService.patch(id, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (!petService.delete(id)) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
}
```

**Evidence (screenshot filename or curl/Postman output):**
```text
1. PUT /api/pets/6 (200 OK full replacement):
$ curl -i -X PUT http://localhost:8080/api/pets/6 \
  -H "Content-Type: application/json" \
  -d '{"microchipNumber":"MC-2010","name":"Coco","species":"Dog","breed":"Toy Poodle"}'
HTTP/1.1 200 
Content-Type: application/json

{"id":6,"microchipNumber":"MC-2010","name":"Coco","species":"Dog","breed":"Toy Poodle"}

2. PATCH /api/pets/6 (200 OK partial update of breed only):
$ curl -i -X PATCH http://localhost:8080/api/pets/6 \
  -H "Content-Type: application/json" \
  -d '{"breed":"Miniature Poodle"}'
HTTP/1.1 200 
Content-Type: application/json

{"id":6,"microchipNumber":"MC-2010","name":"Coco","species":"Dog","breed":"Miniature Poodle"}

3. First DELETE /api/pets/6 (204 No Content):
$ curl -i -X DELETE http://localhost:8080/api/pets/6
HTTP/1.1 204 

4. Second DELETE /api/pets/6 (404 Not Found):
$ curl -i -X DELETE http://localhost:8080/api/pets/6
HTTP/1.1 404 
Content-Length: 0
```

**Reflection question:**
_What is the difference between `PUT` and `PATCH` in terms of what the client sends, and what must the service-layer `patch()` method do differently from `update()`?_

Your answer:
When performing a PUT operation the client sends an object containing all the fields that can be updated, whereas when using a PATCH operation the client only sends the fields that need updating. The update() method overwrites all fields on the entity, whereas the patch() method checks for nulls on each field contained within the request so that it only overwrites the fields which aren't null.

---

## Task 5 — Full DTO Layer Audit + Capstone Milestone 3

**DTO class list (names only — confirm no entity class names appear):**
- `CreatePetRequest`
- `UpdatePetRequest`
- `PetDTO`

**409 Conflict scenario:**
Attempting to create a pet with a `microchipNumber` (`MC-1001`) that already exists in the database triggers a `409 Conflict` response.

Request:
```http
POST /api/pets
Content-Type: application/json

{"microchipNumber":"MC-1001","name":"DuplicatePet","species":"Dog","breed":"Beagle"}
```

Response:
```http
HTTP/1.1 409 Conflict
Content-Type: application/json

{"message":"Microchip number already exists: MC-1001"}
```

**Capstone Milestone 3 smoke-check evidence:**
```text
1. POST /api/pets -> 201 Created
HTTP/1.1 201 
Location: http://localhost:8080/api/pets/6
{"id":6,"microchipNumber":"MC-2010","name":"Coco","species":"Dog","breed":"Poodle"}

2. GET /api/pets/6 -> 200 OK
HTTP/1.1 200 
{"id":6,"microchipNumber":"MC-2010","name":"Coco","species":"Dog","breed":"Poodle"}

3. PUT /api/pets/6 -> 200 OK
HTTP/1.1 200 
{"id":6,"microchipNumber":"MC-2010","name":"Coco","species":"Dog","breed":"Toy Poodle"}

4. PATCH /api/pets/6 -> 200 OK (only breed changed, other fields unchanged)
HTTP/1.1 200 
{"id":6,"microchipNumber":"MC-2010","name":"Coco","species":"Dog","breed":"Miniature Poodle"}

5. DELETE /api/pets/6 -> 204 No Content
HTTP/1.1 204 

6. GET /api/pets/6 -> 404 Not Found
HTTP/1.1 404 
```

- [x] `POST` → `201`
- [x] `GET /{new-id}` → `200`
- [x] `PUT /{id}` → `200`
- [x] `PATCH /{id}` → `200` (other fields unchanged)
- [x] `DELETE /{id}` → `204`
- [x] `GET /{deleted-id}` → `404`

**Reflection question:**
_For one endpoint in your capstone API, state: the method, the URL, what status code it returns on success, what status code it returns when the resource is not found, and why there is no JPA entity in the controller method signature._

Your answer:
The partial update endpoint uses HTTP PATCH and the path /api/pets/{id}. The endpoint returns a 200 OK with the updated PetDTO, or 404 Not Found if the pet ID does not exist. There is no Pet JPA entity in the controller method signature as we accept an UpdatePetRequest object and return a ResponseEntity<PetDTO>. This means that our database entities remain encapsulated in our service layer, we avoid serialization errors caused by lazy loading, and our public JSON contract is not tied to our database schema.

---

## Self-Check

- [x] All Week 10 work on the `week10` branch (project at branch root, no `week10/` folder)
- [x] This file completed and renamed `answers.md`
- [x] No JPA entity class appears in any controller method signature
- [x] Repository link pasted into Moodle (no files uploaded)
