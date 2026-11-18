# WEBDEV2 Week 14 Lecture Notes (F2)

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**Branch:** `week14`  

## 1. Form and API Data Flow (`PUT` vs `PATCH`)

- On the frontend (`src/main/resources/static/app.js`), both the **Pet** and **Owner** forms call `event.preventDefault()`, trim user inputs, disable the submit button while awaiting `request()`, and re-enable the button in `finally`.
- Relationship dropdowns (`#pet-owner-select`, `#booking-pet-select`, `#booking-vet-select`) are populated directly from `GET /api/owners`, `GET /api/pets?page=0&size=100`, and `GET /api/veterinarians`. The selected record ID is kept in `<option value="...">` separately from the label shown to the user.
- For **Create** (`POST /api/pets`, `POST /api/owners`), no generated `id` is sent in the JSON payload.
- For **Update**, `PUT` and `PATCH` are kept distinct in `PetService`:
  - `PUT /api/pets/{id}` (`PetService.update`) requires all core fields (`microchipNumber`, `name`, `species`) and replaces the complete updatable representation, including clearing or reassigning `ownerId`. Passing a partial payload without required fields throws `400 Bad Request`.
  - `PATCH /api/pets/{id}` (`PetService.patch`) updates only non-null fields present in `UpdatePetRequest`, leaving omitted fields unchanged.
- In `app.js`, `request()` checks `response.status === 204 ? null` so `DELETE` responses do not fail trying to call `response.json()`.

## 2. Client, Server, and Database Validation Responsibilities

1. **Client (`index.html` / `app.js`):** HTML `required`/`maxlength` attributes and blank checks give fast feedback and preserve failed inputs when a request fails so the user can edit without retyping.
2. **Server (`CreatePetRequest`, `OwnerRequest`, `BookAppointmentRequest`, `GlobalExceptionHandler`):** Bean Validation (`@NotBlank`, `@Size`, `@DecimalMin("0.00")`) and service checks (`ConflictException`, `ResourceNotFoundException`) return structured `400`, `404`, and `409` JSON `ErrorResponse` payloads even if someone calls the API directly with `curl`.
3. **Database (MySQL `webdev2_week8`):** `NOT NULL`, `UNIQUE (microchip_number)`, `UNIQUE (pet_id, appointment_date)`, `FOREIGN KEY`, and `CHECK (fee >= 0.00)` / `CHECK (available_slots >= 0)` remain the final guardrail if another client writes directly to MySQL.

## 3. Delete Policy and Transaction Boundary Decisions

- **Delete Policy (`409 Conflict` instead of silent cascade):** `Owner` and `Pet` no longer cascade deletes to child history (`orphanRemoval` removed from `Owner.pets`, `CascadeType.ALL` removed from `Pet.appointments`). `OwnerService.delete` rejects deleting an owner with registered pets (`409`), and `PetService.delete` rejects deleting a pet with appointments (`409`), matching MySQL's `ON DELETE RESTRICT` foreign keys.
- **Atomic Booking Transaction (`AppointmentService.bookAppointment`):**
  - Instead of making two separate HTTP calls from the browser, `app.js` calls one endpoint: `POST /api/appointments/book`.
  - `AppointmentService.bookAppointment` is a public `@Transactional` method called through Spring's proxy so all writes run inside one MySQL transaction.
  - Step 1 checks the veterinarian exists and runs `VeterinarianRepository.decrementSlotIfAvailable(id)` (`UPDATE veterinarian SET available_slots = available_slots - 1 WHERE id = :id AND available_slots > 0`). Only `changed == 1` proceeds.
  - Step 2 looks up the pet, checks `existsByPetIdAndAppointmentDate(...)` (so one pet cannot book twice on the same day), and saves the `Appointment` via `saveAndFlush`. If step 2 throws `ResourceNotFoundException` (`404`) or `ConflictException` (`409`), Spring rolls back step 1 cleanly so `available_slots` stays unchanged.
  - Slot counts and constraints are decided on the server, never trusted from client inputs.

## 4. Test Interpretation and Diagnosed Defect

- **Positive & Negative Outcomes:** Browser and `curl` tests confirmed `201` creates, `200` `PUT`/`PATCH` updates, `204` deletes after `confirm()`, `400` field errors (`name` under 2 chars, negative booking fee `-50.00`, missing `PUT` fields), `404` missing `Pet` (`999`), `409` duplicate microchip (`MC-1001`) and in-use deletes, network failure recovery on `:5500` with backend stopped, and MySQL-verified booking commit (`Dr. Reyes` `3 -> 2` slots) vs rollback (`Dr. Villanueva` stayed at `3`).
- **Repaired Defect:** While checking Spring Boot console logs during browser testing, automatic browser requests to `/favicon.ico` threw `NoResourceFoundException`, which fell into the generic `Exception` handler and returned `500` with a stack trace. Added `NoResourceFoundException.class` to `GlobalExceptionHandler.handleNotFound` so missing static paths return clean `404` JSON without noisy `500` errors.
