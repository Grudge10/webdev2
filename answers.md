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

Week 13 builds on `week11` on branch `week13` with MySQL `webdev2_week8`. I added placeholder env config, `@Transactional(readOnly = true)` and owner fields on `PetDTO`, the `/api/owners` read endpoint, owner seeding for pets without owners, updated `WebConfig` origins, and a vanilla HTML, CSS, and JS read page using `getJson()` and `textContent`.
