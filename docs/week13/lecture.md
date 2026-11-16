# WEBDEV2 Week 13 Lecture Notes

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**GitHub Repo:** https://github.com/Grudge10/webdev2  
**Branch:** `week13`  
**Date Submitted:** 2026-11-16

## 1. Baseline Audit and Scope

I branched `week13` from `week11` instead of `midterm`, since `week11` holds our Vet Clinic capstone project. On `week11`, the `Pet`, `Owner`, `Veterinarian`, and `Appointment` entities and the `/api/pets` endpoints already worked with validation and pagination.

For Week 13, I fixed a few gaps before connecting the frontend. I replaced the hardcoded database login in `application.properties` with `${DB_USERNAME}` and `${DB_PASSWORD}` placeholders and added `application-example.properties`. I added `@Transactional(readOnly = true)` to `PetService` reads and added `ownerId` and `ownerName` to `PetDTO`. I updated `PetDataSeeder` so pets without owners get assigned an owner on startup, added `OwnerController` and `OwnerService` for `/api/owners`, updated `WebConfig` to allow `http://localhost:5500` and `http://localhost:8080`, and built the vanilla HTML, CSS, and JS page in `src/main/resources/static/`.

## 2. Architecture and Data Flow

```text
Browser (index.html / app.js) -> Spring Boot REST (Controller / DTO -> Service -> Repository) -> MySQL (webdev2_week8)
```

The browser handles rendering and user clicks, and only talks to Spring Boot over HTTP JSON using `getJson()`. It never connects to MySQL or stores database passwords. Controllers accept and return DTOs (`PetDTO`, `OwnerDTO`) instead of JPA entities. `PetService` and `OwnerService` map entities inside `@Transactional(readOnly = true)` so lazy fields like `pet.getOwner().getName()` load safely without `LazyInitializationException`. MySQL stores all tables and enforces primary keys, unique microchips, and foreign keys.

## 3. API Endpoint Contract

| Method & Path | Params | Success Response | Error Response |
|---|---|---|---|
| `GET /api/pets` | `page`, `size` (max 100), `sort`, `direction`, `species` | `200 OK` `Page<PetDTO>` (rows inside `content`, empty filter returns `content: []`) | `500` `ErrorResponse` |
| `GET /api/pets/{id}` | Path `id` | `200 OK` `PetDTO` with `ownerId` and `ownerName` | `404` `Pet not found with id: {id}` |
| `GET /api/owners` | None | `200 OK` array of `OwnerDTO` (`id`, `name`, `phone`) | `500` `ErrorResponse` |
| `GET /api/owners/{id}` | Path `id` | `200 OK` `OwnerDTO` | `404` `Owner not found with id: {id}` |

In `app.js`, `loadPets()` reads `page.content` instead of assuming `/api/pets` returns a raw array, and renders text with `textContent` instead of `innerHTML`.

## 4. Origins, CORS, and Config

An origin is scheme + host + port, so `http://localhost:5500` and `http://localhost:8080` are different origins. In `WebConfig`, I listed `http://localhost:3000`, `http://localhost:5500`, and `http://localhost:8080` instead of opening CORS to `*`. Untrusted origins like `http://untrusted.example` get rejected with `403 Invalid CORS request`.

I do not use `mode: "no-cors"` in `fetch()` because it gives an opaque response where JavaScript cannot read the HTTP status or JSON body. CORS only controls browser access across origins, so backend role checks (coming in Week 15) and MySQL user permissions remain separate. Browser files contain no database passwords.

## 5. Diagnosis and SQL Comparison

When checking the `webdev2_week8` database, `Buddy`, `Luna`, `Max`, and `Milo` had `owner_id = NULL`, and `OPTIONS` preflight from `http://localhost:5500` returned `403` because `WebConfig` only allowed `http://localhost:3000`. Fixing `PetDataSeeder` to assign owners and adding `http://localhost:5500` and `http://localhost:8080` in `WebConfig` solved both problems.

| Check | Record for Pet ID 1 |
|---|---|
| MySQL `SELECT` | `id=1`, `microchip_number='MC-1001'`, `name='Buddy'`, `species='Dog'`, `breed='Golden Retriever'`, `owner_id=1`, `owner_name='Maria Santos'` |
| `GET /api/pets/1` | `id: 1`, `microchipNumber: "MC-1001"`, `name: "Buddy"`, `species: "Dog"`, `breed: "Golden Retriever"`, `ownerId: 1`, `ownerName: "Maria Santos"` |
| Browser Table & Detail | Row displays `1 | Buddy | Dog | Maria Santos` and View loads `ID: 1`, `Name: Buddy`, `Species: Dog`, `Breed: Golden Retriever`, `Microchip: MC-1001`, `Owner: Maria Santos` |
