# Veterinary Clinic REST API Documentation

Base URL: `http://localhost:8080`

## Authentication & CSRF Endpoints (Session-Based)

| Method | Path | Access | Description | Status Codes |
|---|---|---|---|---|
| `GET` | `/api/auth/csrf` | Public | Returns `{ headerName, parameterName, token }` for current session | `200` |
| `POST` | `/api/auth/login` | Public (+ CSRF) | Authenticates credentials (`LoginRequest`), sets `HttpOnly` session cookie, rotates CSRF token | `200`, `400`, `401`, `403` |
| `GET` | `/api/auth/me` | Authenticated | Returns current `{ username, role }` without password hash | `200`, `401` |
| `POST` | `/api/auth/logout` | Authenticated (+ CSRF) | Invalidates HTTP session and clears security context | `204`, `401`, `403` |

## Domain Endpoints (All Protected)

State-changing requests (`POST`, `PUT`, `PATCH`, `DELETE`) require an authenticated `MANAGER` session cookie and valid `X-CSRF-TOKEN` header.

| Method | Path | Required Role | Description | Status Codes |
|---|---|---|---|---|
| `GET` | `/api/pets` | `READER`, `MANAGER` | Paginated, sorted, and optional `species` filtered list of `PetDTO` | `200`, `401` |
| `GET` | `/api/pets/{id}` | `READER`, `MANAGER` | Get one pet by ID | `200`, `401`, `404` |
| `POST` | `/api/pets` | `MANAGER` | Create a pet (`CreatePetRequest`, optional `ownerId`) | `201`, `400`, `401`, `403`, `404`, `409` |
| `PUT` | `/api/pets/{id}` | `MANAGER` | Full replacement update of a pet | `200`, `400`, `401`, `403`, `404`, `409` |
| `PATCH` | `/api/pets/{id}` | `MANAGER` | Partial update of non-null fields | `200`, `400`, `401`, `403`, `404`, `409` |
| `DELETE` | `/api/pets/{id}` | `MANAGER` | Delete a pet (rejected with `409` if pet has appointments) | `204`, `401`, `403`, `404`, `409` |
| `GET` | `/api/owners` | `READER`, `MANAGER` | List all pet owners (`OwnerDTO`) | `200`, `401` |
| `GET` | `/api/owners/{id}` | `READER`, `MANAGER` | Get one owner by ID | `200`, `401`, `404` |
| `POST` | `/api/owners` | `MANAGER` | Create an owner (`OwnerRequest`) | `201`, `400`, `401`, `403` |
| `PUT` | `/api/owners/{id}` | `MANAGER` | Update an owner (`OwnerRequest`) | `200`, `400`, `401`, `403`, `404` |
| `DELETE` | `/api/owners/{id}` | `MANAGER` | Delete an owner (rejected with `409` if owner has pets) | `204`, `401`, `403`, `404`, `409` |
| `GET` | `/api/veterinarians` | `READER`, `MANAGER` | List clinic veterinarians with current `availableSlots` | `200`, `401` |
| `GET` | `/api/appointments` | `READER`, `MANAGER` | List scheduled appointments | `200`, `401` |
| `POST` | `/api/appointments/book` | `MANAGER` | Atomically decrement vet `availableSlots` and create appointment | `201`, `400`, `401`, `403`, `404`, `409` |
