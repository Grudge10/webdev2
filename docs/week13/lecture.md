# WEBDEV2 Week 13 Lecture Notes

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**GitHub Repo:** https://github.com/Grudge10/webdev2  
**Branch:** `week13`  
**Date Submitted:** 2026-11-16

## 1. Baseline Audit and Scope

The `week13` branch was branched off from `week11` rather than `midterm`, as `week11` contains our Vet Clinic capstone project. The `Pet`, `Owner`, `Veterinarian` and `Appointment` entities were working with the `/api/pets` endpoints including validation and pagination.

For `week13`, I made some small fixes to prepare for frontend integration. The database username and password were moved from being hardcoded in `application.properties` to `${DB_USERNAME}` and `${DB_PASSWORD}`. This was also added to `application-example.properties`. The `PetService` read methods had `@Transactional(readOnly = true)` added. `ownerId` and `ownerName` were added to `PetDTO`. The `PetDataSeeder` was changed so that any pets without an owner would be given one when starting up. `OwnerController` and `OwnerService` were created to handle the `/api/owners` endpoint. `WebConfig` was updated to allow access from `http://localhost:5500` and `http://localhost:8080`. A basic HTML/CSS/JS page was created inside `src/main/resources/static/`.

## 2. Architecture and Data Flow

```text
Browser (index.html / app.js) -> Spring Boot REST (Controller / DTO -> Service -> Repository) -> MySQL (webdev2_week8)
```

The browser only communicates with Spring Boot via HTTP JSON through `getJson()` calls. No connection is made to MySQL and no database passwords are stored. The controllers only receive and send back DTO objects (`PetDTO` and `OwnerDTO`), not JPA entities. `PetService` and `OwnerService` contain the mapping between entities and ensure that any lazy field calls such as `pet.getOwner().getName()` will be safe in a transaction with `@Transactional(readOnly = true)`. All tables have been stored in MySQL with primary keys, unique microchip constraints and foreign key constraints.

## 3. API Endpoint Contract

| Method & Path | Params | Success Response | Error Response |
|---|---|---|---|
| `GET /api/pets` | `page`, `size` (max 100), `sort`, `direction`, `species` | `200 OK` `Page<PetDTO>` (rows inside `content`, empty filter returns `content: []`) | `500` `ErrorResponse` |
| `GET /api/pets/{id}` | Path `id` | `200 OK` `PetDTO` with `ownerId` and `ownerName` | `404` `Pet not found with id: {id}` |
| `GET /api/owners` | None | `200 OK` array of `OwnerDTO` (`id`, `name`, `phone`) | `500` `ErrorResponse` |
| `GET /api/owners/{id}` | Path `id` | `200 OK` `OwnerDTO` | `404` `Owner not found with id: {id}` |

The `loadPets()` function in `app.js` now takes pets from `page.content` rather than expecting a raw array from `/api/pets`, and renders text with `textContent` instead of `innerHTML`.

## 4. Origins, CORS, and Config

The origin includes scheme, host, and port. This means `http://localhost:5500` and `http://localhost:8080` would be different origins. The origins `http://localhost:3000`, `http://localhost:5500`, `http://localhost:8080` were put into `WebConfig`. Any origins that aren't trusted will be blocked with a `403 Invalid CORS request`.

The `no-cors` option in `fetch()` isn't used since it provides an opaque response meaning there's no way for JavaScript to see the HTTP status or JSON body. CORS only deals with browser security between origins, meaning there's no issues with role checking on the backend (Week 15) and MySQL user permissions. There are no database passwords in the browser files.

## 5. Diagnosis and SQL Comparison

`Buddy`, `Luna`, `Max` and `Milo` all had `owner_id = NULL` within the `webdev2_week8` database. The `OPTIONS` preflight request (from `http://localhost:5500`) had a 403 error because `WebConfig` only allowed `http://localhost:3000`. By amending the `PetDataSeeder` file so that the pets have owners and amending `WebConfig` to include `http://localhost:5500` and `http://localhost:8080`, these issues were solved.

| Check | Record for Pet ID 1 |
|---|---|
| MySQL `SELECT` | `id=1`, `microchip_number='MC-1001'`, `name='Buddy'`, `species='Dog'`, `breed='Golden Retriever'`, `owner_id=1`, `owner_name='Maria Santos'` |
| `GET /api/pets/1` | `id: 1`, `microchipNumber: "MC-1001"`, `name: "Buddy"`, `species: "Dog"`, `breed: "Golden Retriever"`, `ownerId: 1`, `ownerName: "Maria Santos"` |
| Browser Table & Detail | Row displays `1 | Buddy | Dog | Maria Santos` and View loads `ID: 1`, `Name: Buddy`, `Species: Dog`, `Breed: Golden Retriever`, `Microchip: MC-1001`, `Owner: Maria Santos` |
