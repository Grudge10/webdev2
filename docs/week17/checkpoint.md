# WEBDEV2 Week 17 Checkpoint & Release Handoff (F5)

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**GitHub Repo:** https://github.com/Grudge10/webdev2  
**Pinned Release Branch:** `week17`  
**Packaged Artifact:** `target/webdev2-0.0.1-SNAPSHOT.jar` (`Spring Boot 4.1.1`, `Java 21`, `MySQL 8.4.13`)  
**Matching INFOMAN1 Release Bundle:** https://github.com/Grudge10/infoman1 (`week17/release/`)  
**Date Submitted:** 2026-11-21  

## 1. Release Summary & Local Rehearsal

The application was not deployed in a paid hosted environment; instead, a complete local deployment of the packaged artifact (`java -jar target/webdev2-0.0.1-SNAPSHOT.jar`) ran against a locally restored fresh MySQL database (`vetclinic_f5_restore`), accessed via `vetclinic_app@localhost` with `ddl-auto=validate`.

Since `week16`, `HealthController` was added at `/api/health`, `CORS_ALLOWED_ORIGINS` (default `http://localhost:8080`) and `FORWARD_HEADERS_STRATEGY` (default `none`) were made configurable, `application-example.properties` was updated, and `./mvnw clean verify` ran cleanly with all `16` tests passing.

`spring-boot-starter-thymeleaf` is still included in `pom.xml`, which generates a harmless startup warning about missing templates, and initial user creation requires setting `APP_SEED_*` environment variables on first boot.

## 2. Requirement-to-Artifact Matrix

| Requirement | Implementation / Docs | Evidence |
|---|---|---|
| Packaged clean build & `/api/health` smoke check | `pom.xml`, `HealthController.java`, `docs/week17/setup.md` | [evidence/clean_verify_output.txt](evidence/clean_verify_output.txt) (`16/16` pass) |
| Secure config (`CORS`, cookies, proxy headers, redacted example) | `WebConfig.java`, `SecurityConfig.java`, `application.properties`, `application-example.properties` | [docs/week17/lecture.md](lecture.md) |
| Reproducible setup guide, API docs & user guide | `docs/week17/setup.md`, `docs/week17/user_guide.md`, `API.md` | [docs/week17/uat.md](uat.md) |
| Restart-persistent UAT on separate restore target | `target/webdev2-0.0.1-SNAPSHOT.jar` -> `vetclinic_f5_restore` | [evidence/release_uat_output.txt](evidence/release_uat_output.txt) |

## 3. Draft Week 18 Walkthrough Outline

1. Identity, repo branches (`finals` / `infoman1/week18`), and deployment topology diagram (`0:00–1:30`)
2. Schema, normalization (`owner`, `veterinarian`, `pet`, `appointment`), and `pet_appointment_report` live view (`1:30–3:30`)
3. Request architecture trace (`app.js` -> `Controller` -> `Service` -> `Repository` -> MySQL) (`3:30–5:00`)
4. Auth (`READER` vs `MANAGER`), `BCrypt`, CSRF rotation, `401`/`403` direct API checks, and MySQL `GRANT`/`REVOKE` (`5:00–7:30`)
5. Browser CRUD (`Owner` + `Pet`), delete `409` guard, atomic booking commit + post-write rollback (`7:30–9:30`)
6. `SAVEPOINT`, `EXPLAIN`, `./mvnw test` (`16/16`), backup/restore verification, and defect diagnosis (`9:30–11:30`)

## 4. Change Log

1. Added `HealthController` (`/api/health`), parameterized `CORS_ALLOWED_ORIGINS` and `FORWARD_HEADERS_STRATEGY`, and updated `application.properties` and `application-example.properties`.
2. Added `docs/week17/` setup guide, user guide, UAT matrix, lecture notes, release checkpoint, `API.md` `/api/health` entry, and captured `clean_verify_output.txt` + `release_uat_output.txt`.

## 5. Deliverables & Evidence

- [docs/week17/setup.md](setup.md)
- [docs/week17/user_guide.md](user_guide.md)
- [docs/week17/uat.md](uat.md)
- [docs/week17/lecture.md](lecture.md)
- [API Documentation](../../API.md)
- [evidence/clean_verify_output.txt](evidence/clean_verify_output.txt)
- [evidence/release_uat_output.txt](evidence/release_uat_output.txt)
