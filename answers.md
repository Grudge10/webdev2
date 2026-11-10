# Lab 11 — Answer Template

**Course:** Web Development 2 &nbsp;|&nbsp; **Week:** 11 &nbsp;|&nbsp; **Lab:** Capstone Milestone 3

> **Instructions:** Fill in each section with links to your code and screenshots/recordings. Do not paste full file contents here — link to the file in your repository instead. Submit your GitHub repository URL to Moodle.

---

## Student Information

| Field | Value |
|---|---|
| Name | Jan Liam E. Arias |
| Student ID | 2510499 |
| GitHub Repository URL | https://github.com/Grudge10/webdev2 |
| Branch (if not `main`) | week11 |

---

## Task 1 — Input Validation on DTOs

**DTO files with annotations** *(link to files in your repo)*:

- `CreatePetRequest.java`: [src/main/java/com/webdev2/dto/CreatePetRequest.java](src/main/java/com/webdev2/dto/CreatePetRequest.java)
- `UpdatePetRequest.java`: [src/main/java/com/webdev2/dto/UpdatePetRequest.java](src/main/java/com/webdev2/dto/UpdatePetRequest.java)

**Evidence of 400 response for missing field:**

> Paste the JSON response body you received when you sent a POST with a missing required field:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": 1731234567890,
  "errors": [
    {
      "field": "name",
      "message": "Name is required"
    }
  ]
}
```

---

## Task 2 — Global Exception Handler

**Files created:**

- `GlobalExceptionHandler.java`: [src/main/java/com/webdev2/exception/GlobalExceptionHandler.java](src/main/java/com/webdev2/exception/GlobalExceptionHandler.java)
- `ErrorResponse.java`: [src/main/java/com/webdev2/dto/ErrorResponse.java](src/main/java/com/webdev2/dto/ErrorResponse.java)
- `ResourceNotFoundException.java`: [src/main/java/com/webdev2/exception/ResourceNotFoundException.java](src/main/java/com/webdev2/exception/ResourceNotFoundException.java)

**Evidence — 404 response:**

> Paste the response body for a GET request to a non-existent ID:

```json
{
  "status": 404,
  "message": "Pet not found with id: 999",
  "timestamp": 1731234569102,
  "errors": null
}
```

**Evidence — 400 response with field errors:**

> Paste the response body for a POST with an invalid body, showing the `errors` array:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": 1731234571450,
  "errors": [
    {
      "field": "microchipNumber",
      "message": "Microchip number is required"
    },
    {
      "field": "name",
      "message": "Name is required"
    },
    {
      "field": "name",
      "message": "Name must be between 2 and 100 characters"
    },
    {
      "field": "age",
      "message": "Age must be at least 0"
    },
    {
      "field": "ownerEmail",
      "message": "Invalid email format"
    }
  ]
}
```

---

## Task 3 — CORS Configuration

**File created:**

- `WebConfig.java`: [src/main/java/com/webdev2/config/WebConfig.java](src/main/java/com/webdev2/config/WebConfig.java)

**Evidence — CORS headers present:**

> Paste the curl output or describe the browser Network tab result showing `Access-Control-Allow-Origin`:

```
< HTTP/1.1 200
< Vary: Origin
< Vary: Access-Control-Request-Method
< Vary: Access-Control-Request-Headers
< Access-Control-Allow-Origin: http://localhost:3000
< Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE,OPTIONS
< Access-Control-Allow-Credentials: true
< Allow: GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH
< Content-Length: 0
```

---

## Task 4 — Pagination and Filtering

**Files updated:**

- Repository method added: [src/main/java/com/webdev2/repository/PetRepository.java](src/main/java/com/webdev2/repository/PetRepository.java) (`Page<Pet> findBySpeciesIgnoreCase(String species, Pageable pageable)`)
- Service method updated: [src/main/java/com/webdev2/service/PetService.java](src/main/java/com/webdev2/service/PetService.java) (`Page<PetDTO> findAll(Optional<String> species, int page, int size, String sort, String direction)`)
- Controller method updated: [src/main/java/com/webdev2/controller/PetController.java](src/main/java/com/webdev2/controller/PetController.java) (`GET /api/pets`)

**Evidence — paginated response:**

> Paste the JSON response for `GET /api/students?page=0&size=5&sort=name,asc`:

```json
{
  "content": [
    {
      "id": 1,
      "microchipNumber": "MC-1001",
      "name": "Buddy",
      "species": "Dog",
      "breed": "Golden Retriever"
    },
    {
      "id": 2,
      "microchipNumber": "MC-1002",
      "name": "Luna",
      "species": "Cat",
      "breed": "Siamese"
    },
    {
      "id": 3,
      "microchipNumber": "MC-1003",
      "name": "Max",
      "species": "Dog",
      "breed": "German Shepherd"
    },
    {
      "id": 4,
      "microchipNumber": "MC-1004",
      "name": "Milo",
      "species": "Cat",
      "breed": "Persian"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 4,
  "totalPages": 1,
  "first": true,
  "size": 5,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 4,
  "empty": false
}
```

**Evidence — filtered response:**

> Paste the JSON response for `GET /api/{resource}?{filter}=...`:

```json
{
  "content": [
    {
      "id": 1,
      "microchipNumber": "MC-1001",
      "name": "Buddy",
      "species": "Dog",
      "breed": "Golden Retriever"
    },
    {
      "id": 3,
      "microchipNumber": "MC-1003",
      "name": "Max",
      "species": "Dog",
      "breed": "German Shepherd"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "size": 20,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 2,
  "empty": false
}
```

**Evidence — max size cap:**

> What response did you get when you requested `?size=999`? What was the actual page size in the metadata?

```
When making a GET to /api/pets?size=999 we got a 200 OK. The page size was limited by Math.min(size, 100) in the PetController. In the metadata we saw that pageSize: 100 was set in the pageable object and size: 100 at the top level.
```

---

## Task 5 — Documentation and Test Collection

**Files committed:**

- `API.md` location in repo: [API.md](API.md)
- `api_tests.json` location in repo: [api_tests.json](api_tests.json)

**Test collection run evidence:**

> Paste a screenshot link or describe the test run result (e.g., "5/5 requests passed"):

```
11/11 requests completed in the Week11 - Jan Liam E. Arias API collection. The following were implemented: GET list, GET by ID, POST create, PUT update, PATCH partial update, and DELETE across all 6 CRUD endpoints, along with 400 validation error with errors array, 404 not found error, pagination with sorting, filtering by species, and max page size of 999 limited to 100.
```

**List the requests in your collection:**

| Request name | Method | Path | Expected status |
|---|---|---|---|
| 1. GET All Pets (Happy Path) | GET | `/api/pets` | 200 OK |
| 2. GET Pet By ID (Happy Path) | GET | `/api/pets/1` | 200 OK |
| 3. POST Create Pet (Happy Path) | POST | `/api/pets` | 201 Created |
| 4. PUT Update Pet (Happy Path) | PUT | `/api/pets/5` | 200 OK |
| 5. PATCH Partial Update Pet (Happy Path) | PATCH | `/api/pets/5` | 200 OK |
| 6. DELETE Pet (Happy Path) | DELETE | `/api/pets/5` | 204 No Content |
| 7. POST Invalid Pet (400 Validation Error) | POST | `/api/pets` | 400 Bad Request |
| 8. GET Non-Existent Pet (404 Not Found) | GET | `/api/pets/999` | 404 Not Found |
| 9. GET Pets Paginated and Sorted | GET | `/api/pets?page=0&size=5&sort=name&direction=asc` | 200 OK |
| 10. GET Pets Filtered by Species | GET | `/api/pets?species=Dog` | 200 OK |
| 11. GET Pets Max Size Cap (size=999 capped to 100) | GET | `/api/pets?size=999` | 200 OK |

---

## Reflection

> In 2–3 sentences: which of the five topics from Week 11 had the biggest impact on your capstone API? What surprised you most when implementing it?

The biggest change I saw on my capstone API was Global Exception Handling using `@RestControllerAdvice` and the `ErrorResponse` class. This meant that there was one standard error response in JSON format throughout the application rather than multiple different ones in each Controller. The `@Valid` annotation and `MethodArgumentNotValidException` automatically gathered all the failed constraints on the fields rather than just stopping at the first invalid field. Using `Page` and `Pageable` in the `PetRepository` made the API much safer and cleaner with minimal overhead.
