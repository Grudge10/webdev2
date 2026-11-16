# WEBDEV2 Week 13 Checkpoint Report (F1)

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**GitHub Repo:** https://github.com/Grudge10/webdev2  
**Branch:** `week13`  
**INFOMAN1 Folder:** https://github.com/Grudge10/infoman1 (week13/)  
**Date Submitted:** 2026-11-16

## 1. Environment and Setup Commands

- **JDK:** OpenJDK 21.0.13 (compile target 17)
- **Spring Boot:** 4.1.1
- **MySQL:** 8.4.13

```bash
$HOME/dev-tools/mysql/bin/mysqld --basedir=$HOME/dev-tools/mysql --datadir=$HOME/dev-tools/mysql-data --port=3307 --socket=/tmp/mysql.sock --mysqlx=OFF
JAVA_HOME=$HOME/dev-tools/jdk-21.0.13+9/Contents/Home ./mvnw test
JAVA_HOME=$HOME/dev-tools/jdk-21.0.13+9/Contents/Home ./mvnw spring-boot:run
```

Open `http://localhost:8080/` in a browser after startup (set `DB_USERNAME` and `DB_PASSWORD` env vars if your MySQL login differs from `application-example.properties`).

## 2. Requirements Mapping

| Requirement | Feature / File | Evidence |
|---|---|---|
| REQ-1: App runs with MySQL and safe config | `application.properties`, `application-example.properties`, `.gitignore` | `docs/week13/evidence/mysql_output.txt` |
| REQ-2: Paged pet list reads `page.content` with owner | `PetDTO.java`, `PetService.java`, `app.js` | `docs/week13/evidence/api_output.txt` |
| REQ-3: Detail lookup by ID works for click and input | `PetController.java`, `index.html`, `app.js` | `docs/week13/evidence/api_output.txt` |
| REQ-4: Loading, empty, error, and 404 UI states via `textContent` | `index.html`, `styles.css`, `app.js` | `docs/week13/evidence/api_output.txt` |
| REQ-5: Related Owner resource readable via DTO endpoint | `OwnerController.java`, `OwnerService.java`, `OwnerDTO.java` | `docs/week13/evidence/api_output.txt` |
| REQ-6: HTTP static serving and scoped CORS work | `WebConfig.java`, `src/main/resources/static/` | `docs/week13/evidence/cors_output.txt` |

## 3. Actual vs Expected Results

| Test Case | Expected | Actual | Result |
|---|---|---|---|
| `./mvnw test` | Context loads on MySQL and assertions pass | `Tests run: 1, Failures: 0, Errors: 0` | Pass |
| `GET /` and `/app.js` | Static frontend files served with 200 | `index:200`, `app.js:200`, `styles.css:200` | Pass |
| `GET /api/pets?page=0&size=5` | Returns 5 pets in `content` with `ownerName` | Returned 5 pets (`Buddy` to `Bantay`) with owners | Pass |
| `GET /api/pets/1` | Returns single `Buddy` `PetDTO` JSON | Returned `200 OK` matching MySQL row `id=1` | Pass |
| `GET /api/pets?species=Bird` | Returns `200 OK` with empty `content: []` | Returned `totalElements: 0`, `empty: true` | Pass |
| `GET /api/pets/999` | Returns `404` `ErrorResponse` JSON | Returned `404` `Pet not found with id: 999` | Pass |
| `GET /api/owners` | Returns 3 owners as JSON array | Returned `Maria Santos`, `Carlos Mendoza`, `Elena Cruz` | Pass |
| CORS `http://localhost:5500` vs `http://untrusted.example` | `5500` allowed, untrusted blocked | `5500` returned `200`, `untrusted.example` returned `403` | Pass |

## 4. Known Defects and Scope Notes

- The create, edit and delete forms are part of week14 (F2). Login and roles are part of week15 (F3).
- The `Task`, `User`, `Department` and `Student` classes from the previous tasks (`prelim` and `week9`) have been carried over into the repo. This does not affect the Vet Clinic endpoints.
- We have confirmed that the frontend is serving HTTP requests and the API is returning JSON using `curl` and `./mvnw test`.

## 5. Change Log and Evidence

1. `add owner endpoint and update pet dto`
2. `add vanilla js frontend`
3. `add week 13 docs and evidence`

- [evidence/api_output.txt](evidence/api_output.txt)
- [evidence/cors_output.txt](evidence/cors_output.txt)
- [evidence/mysql_output.txt](evidence/mysql_output.txt)
