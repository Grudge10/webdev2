# Veterinary Clinic REST API Documentation

Base URL: `http://localhost:8080`

---

## 1. `GET /api/pets`

Returns a paginated and optionally filtered list of pets.

### Query Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `page` | `integer` | `0` | Page number (0-based) |
| `size` | `integer` | `20` | Items per page (capped at 100) |
| `sort` | `string` | `id` | Field name to sort by (for example `id`, `name`, `species`) |
| `direction` | `string` | `asc` | Sort direction (`asc` or `desc`) |
| `species` | `string` | optional | Case-insensitive species filter (for example `Dog`, `Cat`) |

### Response Codes

- `200 OK`: Returns a Spring `Page<PetDTO>` object containing `content`, `totalElements`, `totalPages`, `size`, `number`, `first`, and `last`.
- `500 Internal Server Error`: Returned when an unexpected server error occurs.

### Example Request

```bash
curl "http://localhost:8080/api/pets?page=0&size=5&sort=name&direction=asc"
```

### Example Response (`200 OK`)

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
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "size": 5,
  "number": 0,
  "numberOfElements": 2,
  "empty": false
}
```

---

## 2. `GET /api/pets/{id}`

Returns a single pet matching the given ID.

### Path Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| `id` | `Long` | Yes | Unique database ID of the pet |

### Response Codes

- `200 OK`: Returns the matching `PetDTO` JSON object.
- `404 Not Found`: Returned when no pet exists with the specified `id`.
- `500 Internal Server Error`: Returned when an unexpected server error occurs.

### Example Request

```bash
curl "http://localhost:8080/api/pets/1"
```

### Example Success Response (`200 OK`)

```json
{
  "id": 1,
  "microchipNumber": "MC-1001",
  "name": "Buddy",
  "species": "Dog",
  "breed": "Golden Retriever"
}
```

### Example Error Response (`404 Not Found`)

```json
{
  "status": 404,
  "message": "Pet not found with id: 999",
  "timestamp": 1731234567890,
  "errors": null
}
```

---

## 3. `POST /api/pets`

Creates a new pet record after validating the request body.

### Request Body (`CreatePetRequest`)

| Field | Type | Required | Validation Constraints |
|---|---|---|---|
| `microchipNumber` | `String` | Yes | `@NotBlank`, `@Size(max = 50)` |
| `name` | `String` | Yes | `@NotBlank`, `@Size(min = 2, max = 100)` |
| `species` | `String` | Yes | `@NotBlank`, `@Size(max = 50)` |
| `breed` | `String` | No | `@Size(max = 100)` |
| `age` | `Integer` | No | `@Min(0)` |
| `ownerEmail` | `String` | No | `@Email` |

### Response Codes

- `201 Created`: Pet created and returned as a `PetDTO` with the `Location` header pointing to `/api/pets/{id}`.
- `400 Bad Request`: Returned when Bean Validation fails on one or more fields, with field-level messages in the `errors` array.
- `409 Conflict`: Returned when a pet with the same `microchipNumber` already exists.
- `500 Internal Server Error`: Returned when an unexpected server error occurs.

### Example Request

```json
{
  "microchipNumber": "MC-1005",
  "name": "Bella",
  "species": "Dog",
  "breed": "Beagle",
  "age": 3,
  "ownerEmail": "owner@example.com"
}
```

### Example Success Response (`201 Created`)

```json
{
  "id": 5,
  "microchipNumber": "MC-1005",
  "name": "Bella",
  "species": "Dog",
  "breed": "Beagle"
}
```

### Example Validation Error Response (`400 Bad Request`)

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

## 4. `PUT /api/pets/{id}`

Replaces all fields of an existing pet with the given ID.

### Request Body (`UpdatePetRequest`)

| Field | Type | Required | Validation Constraints |
|---|---|---|---|
| `microchipNumber` | `String` | Yes | `@Size(min = 1, max = 50)` |
| `name` | `String` | Yes | `@Size(min = 2, max = 100)` |
| `species` | `String` | Yes | `@Size(min = 1, max = 50)` |
| `breed` | `String` | No | `@Size(max = 100)` |
| `age` | `Integer` | No | `@Min(0)` |
| `ownerEmail` | `String` | No | `@Email` |

### Response Codes

- `200 OK`: Returns the updated `PetDTO`.
- `400 Bad Request`: Returned when validation fails on the request body.
- `404 Not Found`: Returned when no pet exists with the specified `id`.
- `500 Internal Server Error`: Returned when an unexpected server error occurs.

### Example Request

```json
{
  "microchipNumber": "MC-1005",
  "name": "Bella",
  "species": "Dog",
  "breed": "Pocket Beagle"
}
```

### Example Response (`200 OK`)

```json
{
  "id": 5,
  "microchipNumber": "MC-1005",
  "name": "Bella",
  "species": "Dog",
  "breed": "Pocket Beagle"
}
```

---

## 5. `PATCH /api/pets/{id}`

Partially updates only the non-null fields provided for an existing pet.

### Request Body (`UpdatePetRequest`)

| Field | Type | Required | Validation Constraints |
|---|---|---|---|
| `microchipNumber` | `String` | No | `@Size(min = 1, max = 50)` |
| `name` | `String` | No | `@Size(min = 2, max = 100)` |
| `species` | `String` | No | `@Size(min = 1, max = 50)` |
| `breed` | `String` | No | `@Size(max = 100)` |
| `age` | `Integer` | No | `@Min(0)` |
| `ownerEmail` | `String` | No | `@Email` |

### Response Codes

- `200 OK`: Returns the patched `PetDTO`.
- `400 Bad Request`: Returned when a provided field fails validation.
- `404 Not Found`: Returned when no pet exists with the specified `id`.
- `500 Internal Server Error`: Returned when an unexpected server error occurs.

### Example Request

```json
{
  "breed": "Lemon Beagle"
}
```

### Example Response (`200 OK`)

```json
{
  "id": 5,
  "microchipNumber": "MC-1005",
  "name": "Bella",
  "species": "Dog",
  "breed": "Lemon Beagle"
}
```

---

## 6. `DELETE /api/pets/{id}`

Deletes an existing pet with the given ID.

### Path Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| `id` | `Long` | Yes | Unique database ID of the pet |

### Response Codes

- `204 No Content`: Pet was deleted and no response body is returned.
- `404 Not Found`: Returned when no pet exists with the specified `id`.
- `500 Internal Server Error`: Returned when an unexpected server error occurs.

### Example Request

```bash
curl -i -X DELETE "http://localhost:8080/api/pets/5"
```

### Example Response (`204 No Content`)

Empty response body with status `HTTP/1.1 204`.
