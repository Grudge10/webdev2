# WEBDEV2 Week 16 Test Report (F4)

**Tested Branch:** `week16`  
**Date Run:** 2026-11-18  
**Environment:** OpenJDK 21.0.13 (`JAVA_HOME`), Spring Boot `4.1.1`, Spring Security `7.1.1`, MySQL `8.4.13` (`vetclinic_f4_test` for `./mvnw test`, `webdev2_week8` for live UI UAT, both connected as restricted `vetclinic_app@localhost` with `ddl-auto=validate`)  

## Automated Maven Test Run

Command executed:

```bash
DB_USERNAME=vetclinic_app DB_PASSWORD='<LOCAL_APP_PASSWORD>' \
JAVA_HOME=$HOME/dev-tools/jdk-21.0.13+9/Contents/Home ./mvnw test
```

Results (recorded in [evidence/maven_test_output.txt](evidence/maven_test_output.txt)):
- `com.webdev2.Webdev2ApplicationTests`: `2` run, `0` failures, `0` errors, `0` skipped
- `com.webdev2.ClinicServiceUnitTests`: `4` run, `0` failures, `0` errors, `0` skipped
- `com.webdev2.Week15SecurityTests`: `1` run, `0` failures, `0` errors, `0` skipped
- `com.webdev2.Week16MysqlTransactionTests`: `2` run, `0` failures, `0` errors, `0` skipped
- `com.webdev2.Week16ApiSecurityTests`: `6` run, `0` failures, `0` errors, `0` skipped
- **Total:** `Tests run: 15, Failures: 0, Errors: 0, Skipped: 0` (`BUILD SUCCESS`)

## Transaction Pre/Post State Verification

In the commit case (`Week16MysqlTransactionTests.commitsAppointmentAndDecrementsVeterinarianSlotInMysql`), Dr. Reyes (`id=1`) had `3` slots and `3` appointments before the booking. Reading values independently after the DB call showed `available_slots = 2` and `appointment = 4`, and the test tore down afterward so `available_slots = 3` at the end.

In the rollback case (`Week16MysqlTransactionTests.rollsBackVeterinarianSlotWhenSecondWriteFailsAfterDecrement`), Dr. Villanueva (`id=2`) initially had `3` available slots (`3` appointments). Book appointment was called with `petId = 999999`: Write #1 (`decrementSlotIfAvailable`) executed first, then a `ResourceNotFoundException` occurred, and reading values independently showed that `available_slots = 3` and `appointment = 3`.

## Browser Acceptance Summary

All `8/8` test cases in [test_matrix.md](test_matrix.md) passed on `http://localhost:8080/` with `0` errors in the console, tested with a `375x812` viewport and keyboard tabbing. Pet Mocha (temporary pet) was deleted via UI `204`, and appointment #11 (temporary appointment) was tidied in MySQL; SQL/`EXPLAIN` verification is cross-referenced in `infoman1/week16/`.
