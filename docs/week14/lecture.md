# WEBDEV2 Week 14 Lecture Notes (F2)

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**Branch:** `week14`  

## 1. Form and API Data Flow (`PUT` vs `PATCH`)

In `app.js`, the Pet and Owner forms prevent default form submission, clean user input, and disable the submit button until the request finishes. The submit button is enabled in the `finally` block. The dropdowns for relationships (`#pet-owner-select`, `#booking-pet-select`, `#booking-vet-select`) have their options filled from `GET /api/owners`, `GET /api/pets?page=0&size=100`, and `GET /api/veterinarians`. Each option stores the ID of the entity in `value`, while the visible text displayed to the user is different from the ID. In create requests (`POST /api/pets`, `POST /api/owners`), no ID is included in the request body.

**Update:**
In `PetService`, `PUT` and `PATCH` are separated. `PUT /api/pets/{id}` (`PetService.update`) requires all required fields (`microchipNumber`, `name`, `species`) and replaces the full updatable representation, including resetting or setting `ownerId`. If required fields are missing, a `400 Bad Request` error is returned. `PATCH /api/pets/{id}` (`PetService.patch`) only updates the non-null fields in `UpdatePetRequest`, leaving other fields unchanged. In `app.js`, if the status is `204`, the response body is set to `null`. This prevents errors when calling `.json()` on `DELETE` responses.

## 2. Client, Server, and Database Validation Responsibilities

- **Client (`index.html`/`app.js`):** Constraints (`required` and `maxlength`) allow quick validation. If an error occurs during requests, erroneous data is preserved in the form to allow changes in the UI without re-typing.
- **Server (`CreatePetRequest`, `OwnerRequest`, `BookAppointmentRequest`, `GlobalExceptionHandler`):** `jakarta.validation` annotations (such as `@NotBlank`, `@Size`, `@DecimalMin("0.00")`) within the DTOs and exceptions (`ConflictException`, `ResourceNotFoundException`) within services produce standardised `400`, `404`, and `409` responses with `ErrorResponse` JSON objects. These still apply if the API is called directly with `curl`.
- **Database (MySQL `webdev2_week8`):** Constraints (`NOT NULL`, `UNIQUE (microchip_number)`, `UNIQUE (pet_id, appointment_date)`, `FOREIGN KEY`, `fee >= 0.00`, and `available_slots >= 0`) within the database are the last line of defence, in case data is sent directly to MySQL from another client application.

## 3. Delete Policy and Transaction Boundary Decisions

- **Deletion Policy (No Silent Cascading Deletion):** Owner and Pet do not delete child histories anymore. `orphanRemoval` has been removed from `Owner.pets` and `CascadeType.ALL` from `Pet.appointments`. Deleting any Owner with registered Pets returns a `409` status code, and deleting any Pet with appointments also returns a `409` response. This matches the behaviour of MySQL foreign keys with `ON DELETE RESTRICT`.
- **Atomic Booking (`AppointmentService.bookAppointment`):**
  A single request is made from `app.js` to `POST /api/appointments/book`. The public method `AppointmentService.bookAppointment` is called through the Spring proxy, meaning all database changes occur as a single MySQL transaction:
  - **Step 1:** Check if the requested Veterinarian exists. Call `VeterinarianRepository.decrementSlotIfAvailable(id)`. This SQL statement decrements `available_slots` by `1` if `available_slots > 0`, and proceeds only if `changed == 1`.
  - **Step 2:** Find the Pet and check it does not have another booking on the same date (`existsByPetIdAndAppointmentDate(...)`). Create the `Appointment` and call `saveAndFlush()`. If anything during Step 2 fails (`ResourceNotFoundException` `404` or `ConflictException` `409`), Spring rolls back the changes made in Step 1 so the veterinarian's available slots remain the same.
  The number of available slots and constraints will always be determined server-side, as client data should never be trusted.

## 4. Test Interpretation and Diagnosed Defect

- **Positive/Negative Behaviours Confirmed:**
  Confirmed `201 Created`, `200` updated (`PUT`/`PATCH`), `204` deleted with confirmation, `400` invalid inputs (name too short `< 2`, negative booking fee `-50.00`, missing fields on `PUT`), `404` (Pet `999` does not exist), and `409` (duplicate microchip `MC-1001`, delete while in use). Tested recovery from network communication failures on port `5500` when the backend was down. Bookings were committed (`Dr. Reyes` reduced to `2` slots) and rolled back (`Dr. Villanueva` remained at `3` slots) in MySQL.
- **Defect Fixed:**
  Testing via the browser caused automatic requests to `/favicon.ico`, which triggered `NoResourceFoundException` errors. Because this exception was not handled specifically, it fell through to the general `Exception` handler, returning status `500` with a stack trace logged on the server. Adding `NoResourceFoundException.class` to `handleNotFound` in `GlobalExceptionHandler` ensures missing static resource paths return a clean `404` JSON response instead of `500`.
