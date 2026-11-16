# WEBDEV2 Week 13 Answers (F1)

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**GitHub Repository URL:** https://github.com/Grudge10/webdev2  
**Branch:** `week13`  
**Date Submitted:** 2026-11-16

## Week 13 Documents

- [Lecture Notes and Architecture](docs/week13/lecture.md)
- [Checkpoint Report](docs/week13/checkpoint.md)
- [Updated API Documentation](API.md)
- [Example Database Config](src/main/resources/application-example.properties)
- [Vanilla Frontend](src/main/resources/static/index.html)

## Evidence

- [API Responses](docs/week13/evidence/api_output.txt)
- [CORS Headers](docs/week13/evidence/cors_output.txt)
- [MySQL Output](docs/week13/evidence/mysql_output.txt)

## Summary

`week13` follows `week11` using branch `week13` and MySQL `webdev2_week8`. I have included dummy env config, `@Transactional(readOnly = true)`, owner fields to `PetDTO`, `/api/owners` GET endpoint, and owners seeded for pets without owners. I have added origins to `WebConfig` and a basic HTML, CSS, JS GET page which uses `getJson()` and `textContent`.
