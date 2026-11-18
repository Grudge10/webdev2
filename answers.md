# WEBDEV2 Week 14 Answers (F2)

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**GitHub Repository URL:** https://github.com/Grudge10/webdev2  
**Branch:** `week14`  
**Date Submitted:** 2026-11-18

## Week 14 Documents

- [Lecture Notes and Transaction Reasoning](docs/week14/lecture.md)
- [Checkpoint Report](docs/week14/checkpoint.md)
- [Updated API Documentation](API.md)
- [Updated Postman Collection](api_tests.json)
- [Vanilla Frontend](src/main/resources/static/index.html)

## Evidence

- [API & MySQL Rollback Verification](docs/week14/evidence/api_verification.txt)
- [Maven Test Output](docs/week14/evidence/maven_test_output.txt)
- [INFOMAN1 Week 14 Evidence](https://github.com/Grudge10/infoman1/tree/main/week14)

## Summary

`week14` builds on `week13` with persisted CRUD for Pets and Owners, relationship selection populated from the API, deliberate `409` delete protection, `request()` handling for `204` and structured validation errors, and an atomic `POST /api/appointments/book` workflow backed by MySQL rollback verification.
