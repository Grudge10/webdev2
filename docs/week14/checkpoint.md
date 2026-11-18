# WEBDEV2 Week 14 Checkpoint Report (F2)

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**GitHub Repo:** https://github.com/Grudge10/webdev2  
**Branch:** `week14`  
**INFOMAN1 Folder:** https://github.com/Grudge10/infoman1 (`week14/`)  
**Date Submitted:** 2026-11-18  

## 1. Environment and Setup Commands

- **JDK:** OpenJDK 21.0.13 (compile target 17)
- **Spring Boot:** 4.1.1
- **MySQL:** 8.4.13 (`webdev2_week8`, migrated with `infoman1/week14/migration_week14.sql`)

```bash
$HOME/dev-tools/mysql/bin/mysqld --basedir=$HOME/dev-tools/mysql --datadir=$HOME/dev-tools/mysql-data --port=3307 --socket=/tmp/mysql.sock
JAVA_HOME=$HOME/dev-tools/jdk-21.0.13+9/Contents/Home ./mvnw test
JAVA_HOME=$HOME/dev-tools/jdk-21.0.13+9/Contents/Home ./mvnw spring-boot:run
```

## 2. Requirements Mapping

| Requirement | Feature / File | Evidence |
|---|---|---|
| REQ-1: Full Pet CRUD + Owner relationship selector | `PetController.java`, `PetService.java`, `CreatePetRequest.java`, `UpdatePetRequest.java`, `app.js` | `docs/week14/evidence/api_verification.txt` |
| REQ-2: Full Owner CRUD + 409 delete guard | `OwnerController.java`, `OwnerService.java`, `OwnerRequest.java`, `app.js` | `docs/week14/evidence/api_verification.txt` |
| REQ-3: Safe forms, `204` delete, field validation, and network recovery | `app.js`, `index.html`, `GlobalExceptionHandler.java` | `docs/week14/evidence/api_verification.txt` |
| REQ-4: Atomic appointment booking & rollback | `VeterinarianController.java`, `AppointmentController.java`, `AppointmentService.java` | `docs/week14/evidence/maven_test_output.txt` |
| REQ-5: Clean database migration & SQL verification | `infoman1/week14/migration_week14.sql`, `transactions.sql` | `infoman1/week14/evidence/app_transaction_mysql_check.txt` |

## 3. Executed Cases (Actual vs Expected Results)

| # | Case | Request / Action | Expected | Actual Observed Result | Status |
|---|---|---|---|---|---|
| 1 | Maven integration tests | `./mvnw test` (`Webdev2ApplicationTests`) | 2 tests pass on MySQL | `Tests run: 2, Failures: 0, Errors: 0, Skipped: 0` | Pass |
| 2 | Owner create & update | UI & `POST /api/owners`, `PUT /api/owners/{id}` | `201`, then `200`, dropdown updated | Created `"Ana Navarro"` (`201`), edited phone (`200`), persisted | Pass |
| 3 | Pet validation `400` | Submit Pet `"MC-2001"`, name `"A"` | `400` field error; form keeps input | `400` `"name: Name must be between 2 and 100 characters"`, inputs preserved | Pass |
| 4 | Pet create, reload & `PUT`/`PATCH` | Create `"Bantay Jr"`, reload page, edit breed | `201`, persists on reload, `200` `PUT`/`PATCH` | Persisted ID 14 across page reload; `PUT` & `PATCH` updated breed | Pass |
| 5 | Duplicate microchip `409` | Submit Pet `"MC-1001"` | Rejected with `409 Conflict` | `409` `"Microchip number already exists: MC-1001"` | Pass |
| 6 | Pet missing `404` & `PUT` vs `PATCH` | `GET /api/pets/999`; partial `PUT` without microchip | `404` not found; `400` on incomplete `PUT` | `404` `"Pet not found with id: 999"`; `400` on incomplete `PUT` | Pass |
| 7 | Delete policies (`409` vs `204`) | Delete Maria (`id=1`), Buddy (`id=1`), then free Pet & Owner | `409` on referenced parents; `204` after `confirm()` | `409` messages shown; `204` removed rows cleanly with no JSON error | Pass |
| 8 | Network failure recovery | Stop `:8080`, open `:5500`, submit Pet `"MC-2099"` `"Sky"` | Clean error, button re-enabled, inputs kept | Showed `"Failed to fetch"`, `Save Pet` enabled, inputs remained filled | Pass |
| 9 | Atomic booking commit | UI book Milo (`id=4`) + Dr. Reyes (`id=1`) `2026-11-28` | `201`, Dr. Reyes `3 -> 2` slots, row in MySQL | `201`, appointment #5 inserted, MySQL `available_slots = 2` | Pass |
| 10 | Atomic booking post-write rollback | Book Buddy duplicate date `2026-11-18`; `curl` `petId=999` with vet `id=2` | `409`/`404`, Dr. Villanueva stays `3` slots | `409` and `404` returned; MySQL `available_slots` stayed `3` (`appt_count` unchanged) | Pass |

## 4. Known Defects and Scope Notes

- Fixed `NoResourceFoundException` in `GlobalExceptionHandler.java` so requests to missing paths like `/favicon.ico` return `404` instead of `500`.
- Login, password hashing, CSRF tokens, and role permissions belong to `week15` (`F3`).
- Earlier `Task`, `User`, `Department`, and `Student` classes from `prelim` and `week9` remain in the repo without affecting Vet Clinic CRUD or booking workflows.

## 5. Change Log and Evidence

1. Sync backend models, delete guards, Owner CRUD, Veterinarian read, and atomic Appointment booking on `week14`
2. Add full vanilla JS CRUD forms, relationship selectors, `request()` `204`/error handling, and booking UI
3. Update `API.md`, `api_tests.json`, `Webdev2ApplicationTests`, and `docs/week14/`

- [docs/week14/evidence/api_verification.txt](evidence/api_verification.txt)
- [docs/week14/evidence/maven_test_output.txt](evidence/maven_test_output.txt)
