# WEBDEV2 Week 16 Defects and Fixes Log (F4)

## Fixed Defects

### Defect 1: Clean DB Failed with `ddl-auto=validate`

When running `webdev2` with `spring.jpa.hibernate.ddl-auto=validate` against a clean database containing only `infoman1` SQL (`vetclinic_f4_test`), an error was raised due to schema validation failure. This was because `prelim`/`week9` entities (`Task`, `Department`, `Employee`, `Student`, `Course`, `Enrollment`, `User`, and `UserProfile`) and startup runners did not exist in the Vet Clinic schema. Obsolete `week7`/`week9` lab code (controllers, models, repositories, services, runners, and Thymeleaf templates) was removed in `week16`, and running `./mvnw test` passed `15/15` tests on `vetclinic_f4_test`.

### Defect 2: Invalid `sort` Returns `500`

A request to `GET /api/pets?sort=dropTable` returned a `500` error of `"An unexpected error occurred"`. This is because in `PetService.findAll()`, arbitrary `sort` strings were passed directly to the `Sort.by()` method, resulting in `PropertyReferenceException` being thrown and caught by `GlobalExceptionHandler.handleGeneric()`. `ALLOWED_SORTS` (`id`, `microchipNumber`, `name`, `species`, `breed`) was implemented, in addition to validation that `page >= 0` and `size >= 1`, to throw an `IllegalArgumentException` (`400 Bad Request`). This was verified in `ClinicServiceUnitTests.rejectsUnsupportedSortFieldOnPetList()` and `Week16ApiSecurityTests.rejectsInvalidSortParameterWithBadRequest()`.

### Defect 3: Outdated Detail Panel and Overflow on Small Screen Sizes

If we edited or deleted the currently viewed pet, the details did not update in `#pet-detail`, and large tables caused the screen width to extend beyond `375px` screen sizes. To resolve this, a `currentDetailPetId` variable was added to `app.js` to trigger a refresh on `PUT` requests and clear the detail on `DELETE`, tables were wrapped in a `div` with class `.table-wrap` using `overflow-x: auto`, and focus rings were added with `:focus-visible` in `styles.css`. Verified in Week 16 Browser UAT Cases 3, 5, and 7.
