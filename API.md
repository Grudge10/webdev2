# Veterinary Clinic REST API Documentation

Base URL: `http://localhost:8080`

## Endpoints

| Method | Path | Description | Status Codes |
|---|---|---|---|
| `GET` | `/api/pets` | Paginated, sorted, and optional `species` filtered list of `PetDTO` (with `ownerId` and `ownerName`) | `200`, `500` |
| `GET` | `/api/pets/{id}` | Get one pet by ID | `200`, `404` |
| `POST` | `/api/pets` | Create a pet (`CreatePetRequest`, optional `ownerId`) | `201`, `400`, `404`, `409` |
| `PUT` | `/api/pets/{id}` | Full replacement update of a pet (`UpdatePetRequest` requires `microchipNumber`, `name`, `species`) | `200`, `400`, `404`, `409` |
| `PATCH` | `/api/pets/{id}` | Partial update of non-null fields (`UpdatePetRequest`) | `200`, `400`, `404`, `409` |
| `DELETE` | `/api/pets/{id}` | Delete a pet (rejected with `409` if pet has appointments) | `204`, `404`, `409` |
| `GET` | `/api/owners` | List all pet owners (`OwnerDTO`) | `200` |
| `GET` | `/api/owners/{id}` | Get one owner by ID | `200`, `404` |
| `POST` | `/api/owners` | Create an owner (`OwnerRequest`) | `201`, `400` |
| `PUT` | `/api/owners/{id}` | Update an owner (`OwnerRequest`) | `200`, `400`, `404` |
| `DELETE` | `/api/owners/{id}` | Delete an owner (rejected with `409` if owner has pets) | `204`, `404`, `409` |
| `GET` | `/api/veterinarians` | List clinic veterinarians with current `availableSlots` | `200` |
| `GET` | `/api/appointments` | List scheduled clinic appointments with pet, owner, vet, reason, and fee | `200` |
| `POST` | `/api/appointments/book` | Atomically decrement vet `availableSlots` and create appointment (`BookAppointmentRequest`) | `201`, `400`, `404`, `409` |

## Example `POST /api/appointments/book` Request & Response (`201 Created`)

```json
{
  "petId": 4,
  "vetId": 1,
  "appointmentDate": "2026-11-28",
  "reason": "Ear Check",
  "fee": 650.00
}
```

```json
{
  "id": 5,
  "appointmentDate": "2026-11-28",
  "reason": "Ear Check",
  "fee": 650.00,
  "petId": 4,
  "petName": "Milo",
  "ownerName": "Elena Cruz",
  "vetId": 1,
  "vetName": "Dr. Reyes"
}
```

## Error Format (`ErrorResponse`)

```json
{
  "status": 409,
  "message": "Pet already has an appointment on 2026-11-18",
  "timestamp": 1794995351187,
  "errors": null
}
```
