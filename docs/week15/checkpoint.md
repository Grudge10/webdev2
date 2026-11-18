# WEBDEV2 Week 15 Checkpoint Report (F3)

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**GitHub Repo:** https://github.com/Grudge10/webdev2  
**Branch:** `week15`  
**INFOMAN1 Folder:** https://github.com/Grudge10/infoman1 (`week15/`)  
**Date Submitted:** 2026-11-19  

## 1. Local Provisioning & Startup (No Committed Secrets)

1. Maintainer runs `infoman1/week15/migration_week15.sql` and provisions restricted MySQL account `'vetclinic_app'@'localhost'` from `infoman1/week15/access_control.sql` using local generated passwords.
2. Start backend using environment variables matching [application-example.properties](../../src/main/resources/application-example.properties) (set `APP_SEED_*` on first run to insert `BCrypt`-hashed `clinic_account` test users, then omit `APP_SEED_*` on later startups):

```bash
DB_USERNAME=vetclinic_app DB_PASSWORD='<LOCAL_APP_PASSWORD>' \
APP_SEED_READER_USERNAME=clinic_viewer APP_SEED_READER_PASSWORD='<LOCAL_READER_PASSWORD>' \
APP_SEED_MANAGER_USERNAME=clinic_manager APP_SEED_MANAGER_PASSWORD='<LOCAL_MANAGER_PASSWORD>' \
JAVA_HOME=$HOME/dev-tools/jdk-21.0.13+9/Contents/Home ./mvnw spring-boot:run
```

3. Run automated suite (`Webdev2ApplicationTests` + `Week15SecurityTests`):

```bash
DB_USERNAME=vetclinic_app DB_PASSWORD='<LOCAL_APP_PASSWORD>' \
JAVA_HOME=$HOME/dev-tools/jdk-21.0.13+9/Contents/Home ./mvnw test
```

## 2. Requirements & Evidence Map

| Requirement | Feature / File | Evidence |
|---|---|---|
| REQ-1: `BCrypt` login, logout & safe user endpoint | `ClinicAccount.java`, `ClinicUserDetailsService.java`, `AuthController.java`, `AccountInitializer.java` | [security_tests.md](security_tests.md), [evidence/security_curl_output.txt](evidence/security_curl_output.txt) |
| REQ-2: Backend role enforcement (`READER` vs `MANAGER`) | `SecurityConfig.java`, [application-example.properties](../../src/main/resources/application-example.properties) | [evidence/security_curl_output.txt](evidence/security_curl_output.txt), [evidence/maven_test_output.txt](evidence/maven_test_output.txt) |
| REQ-3: Session CSRF rotation & role-aware UI | `index.html`, `styles.css`, `app.js` | [security_tests.md](security_tests.md), [API.md](../../API.md) |
| REQ-4: Least-privilege MySQL runtime identity | `application.properties` (`vetclinic_app`, `ddl-auto=validate`) | [infoman1/week15/evidence/mysql_privileges_output.txt](https://github.com/Grudge10/infoman1/blob/main/week15/evidence/mysql_privileges_output.txt) |

## 3. Defects Fixed and Known Notes

When logging out of the application, `#user-panel` (`class="row pager"`) was not being hidden as intended despite `userPanel.hidden` being set to `true` because `.row` uses `display: flex`. By adding `[hidden] { display: none !important; }` to `styles.css`, the panel is correctly hidden and `isVisible()` returns `false`.

Maven tests (`Week15SecurityTests` / `Webdev2ApplicationTests`) passed (`Failures: 0, Errors: 0, Skipped: 0`; `3/3` tests passed). Related MySQL access-control evidence is stored in `access_control.sql` and `evidence/mysql_privileges_output.txt` under `infoman1/week15/`.

## 4. Change Log

1. Added Spring Security session auth, `BCrypt` user store, `AccountInitializer`, rotated CSRF tokens, `READER`/`MANAGER` matchers, and `vetclinic_app` `ddl-auto=validate` defaults.
2. Updated `index.html`, `styles.css`, and `app.js` with login/logout panels, in-memory `X-CSRF-TOKEN` handling, `401` handling, and role-aware controls.
3. Added `Week15SecurityTests`, updated `API.md`, redacted security matrix/curl outputs, and `docs/week15/` documentation.
