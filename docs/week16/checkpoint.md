# WEBDEV2 Week 16 Checkpoint Report (F4)

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**GitHub Repo:** https://github.com/Grudge10/webdev2  
**Branch:** `week16`  
**INFOMAN1 Folder:** https://github.com/Grudge10/infoman1 (`week16/`)  
**Date Submitted:** 2026-11-20  

## Feature-Complete Checklist

- [x] Persisted CRUD for Pets and Owners with relationship selection and `409` delete protection
- [x] Atomic appointment booking (`veterinarian.available_slots` + `appointment`) with MySQL rollback proof
- [x] Session authentication (`BCrypt`), `READER`/`MANAGER` role guards, CSRF rotation, and `vetclinic_app` DB runtime (`ddl-auto=validate`)
- [x] `4` Mockito service tests + `6` MockMvc API/security tests + `2` MySQL non-`@Transactional` integration tests + existing CRUD & security tests (`15/15` passing)
- [x] `8/8` browser UAT cases verified (`0` console errors, `375x812` responsive check, keyboard navigation)
- [x] Clean isolated test config (`src/test/resources/application-test.properties` -> `vetclinic_f4_test`)

## Remaining Limitations

Only manual/session-based roles (`READER`/`MANAGER`) are implemented, with no public self-registration and no email/SMS reminders. Packaging for deployment (using `./mvnw clean verify` to generate an executable JAR and performing backup/restore simulations) is part of `week17` (`F5`).

## Change Log

1. Removed obsolete Week 7–9 lab files, validated `sort`/`page`/`size` in `PetService.findAll`, and updated `app.js`/`index.html`/`styles.css` for live `#pet-detail` sync and `.table-wrap` overflow handling.
2. Added `application-test.properties` (`vetclinic_f4_test`), `ClinicServiceUnitTests`, `Week16ApiSecurityTests`, and non-`@Transactional` `Week16MysqlTransactionTests` (`15/15` passing).
3. Recorded `docs/week16/` test matrix, report, defects log, Maven test evidence, and `8/8` browser UAT results.

## Links & Evidence

- [docs/week16/lecture.md](lecture.md)
- [docs/week16/test_matrix.md](test_matrix.md)
- [docs/week16/test_report.md](test_report.md)
- [docs/week16/defects.md](defects.md)
- [docs/week16/evidence/maven_test_output.txt](evidence/maven_test_output.txt)
- [src/test/resources/application-test.properties](../../src/test/resources/application-test.properties)
- Cross-referenced INFOMAN1 database integration & `EXPLAIN` evidence: https://github.com/Grudge10/infoman1/tree/main/week16
