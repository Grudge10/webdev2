# WEBDEV2 Week 17 Release UAT Report (F5)

**Packaged Artifact Tested:** `target/webdev2-0.0.1-SNAPSHOT.jar`  
**Target Database Rehearsal:** Restored `vetclinic_f5_restore` (restored cleanly from `infoman1/week17/backup.sql` via `mysqldump`) running under restricted `'vetclinic_app'@'localhost'` with `ddl-auto=validate`  
**Execution Date:** 2026-11-18  

| # | Scenario | Action & Target | Expected | Actual Observed Result | Status |
|---|---|---|---|---|---|
| 1 | Clean packaged build & health smoke check | `./mvnw clean verify` + `GET /api/health` | `16` tests pass; JAR built; `200 UP` | `Tests run: 16, Failures: 0`; `webdev2-0.0.1-SNAPSHOT.jar` (`57M`); `{"database":"UP","status":"UP"}` | Pass |
| 2 | Anonymous guard & login | `GET /api/pets`, then `POST /api/auth/login` (`clinic_manager`) | `401` anonymous; `200` with session cookie & rotated CSRF | `401 Authentication required`, then `200 {"username":"clinic_manager","role":"MANAGER"}` | Pass |
| 3 | Read-only denial (`READER`) | `clinic_viewer` attempts `POST /api/pets` with valid CSRF | `403 Forbidden` | `403` `Forbidden: insufficient role or invalid CSRF token` | Pass |
| 4 | Authorized CRUD + filter/pagination | `MANAGER` creates `MC-F5REL` (`ReleasePup`), filters `species=Dog` | `201 Created`, `200` paged list showing 4 dogs | `id=6` created; `totalElements: 4` on `species=Dog` | Pass |
| 5 | Atomic workflow & rollback on restore target | Book Milo + Dr. Reyes `2026-12-21` (`600.00`), then book invalid `petId=9999` with Dr. Villanueva | `201` (`Dr. Reyes` `3 -> 2`); `404` rollback (`Dr. Villanueva` stays `3`) | `GET /api/veterinarians` confirmed slots `[2, 3, 3]` | Pass |
| 6 | Server restart persistence (without `APP_SEED_*`) | Stop JAR, restart `java -jar ...`, sign into UI at `http://localhost:8080/` | `clinic_manager` still logs in; `ReleasePup` & appointment #4 persist | `Page 2 of 2` showed `ReleasePup`; Dr. Reyes `2 slots left`; `Release UAT Check` visible; 0 console errors | Pass |

See [evidence/clean_verify_output.txt](evidence/clean_verify_output.txt), [evidence/release_uat_output.txt](evidence/release_uat_output.txt), and `infoman1/week17/restore_report.md`.
