# Veterinary Clinic REST API Documentation

Base URL: `http://localhost:8080`

## Endpoints

| Method | Path | Description | Status Codes |
|---|---|---|---|
| `GET` | `/api/pets` | Paginated, sorted, and optional `species` filtered list of `PetDTO` (with `ownerId` and `ownerName`) | `200`, `500` |
| `GET` | `/api/pets/{id}` | Get one pet by ID | `200`, `404`, `500` |
| `POST` | `/api/pets` | Create a pet (`CreatePetRequest`) | `201`, `400`, `409` |
| `PUT` | `/api/pets/{id}` | Full update of a pet (`UpdatePetRequest`) | `200`, `400`, `404` |
| `PATCH` | `/api/pets/{id}` | Partial update of a pet (`UpdatePetRequest`) | `200`, `400`, `404` |
| `DELETE` | `/api/pets/{id}` | Delete a pet by ID | `204`, `404` |
| `GET` | `/api/owners` | List all pet owners (`OwnerDTO`) | `200`, `500` |
| `GET` | `/api/owners/{id}` | Get one owner by ID | `200`, `404` |

## Example `GET /api/pets` Response (`200 OK`)

```json
{
  "content": [
    {
      "id": 1,
      "microchipNumber": "MC-1001",
      "name": "Buddy",
      "species": "Dog",
      "breed": "Golden Retriever",
      "ownerId": 1,
      "ownerName": "Maria Santos"
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "size": 5,
  "number": 0,
  "first": true,
  "last": true
}
```

## Example `GET /api/owners` Response (`200 OK`)

```json
[
  {
    "id": 1,
    "name": "Maria Santos",
    "phone": "09181112222"
  }
]
```

## Error Format (`ErrorResponse`)

```json
{
  "status": 404,
  "message": "Pet not found with id: 999",
  "timestamp": 1794825910225,
  "errors": null
}
```
