# WEBDEV2 Week 16 Lecture Notes (F4)

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**Branch:** `week16`  

## 1. Test-Layer Choices and Limitations

In the service layer (`ClinicServiceUnitTests`), business logic is tested (`OwnerService.delete`, `PetService.findAll`, `AppointmentService.bookAppointment`). These tests are fast with no Spring or MySQL dependency, though Foreign Key, `UNIQUE` constraints, or InnoDB rollbacks in MySQL cannot be validated using mock repositories.

In the API and security tests (`Week16ApiSecurityTests`, `Week15SecurityTests`), request mapping, Bean Validation (`400`), Global Exception Handling (`404`/`409`), role checking, and CSRF filters are tested together. Using `MockMvcBuilders.webAppContextSetup(context).apply(springSecurity())` means that the full suite of Spring Security filters are enabled, and we do not skip CSRF or authentication.

In the MySQL transactional tests (`Week16MysqlTransactionTests`), testing runs directly against a separate, temporary MySQL database (`vetclinic_f4_test`) to match the behaviour of `CHECK`, `UNIQUE`, `FOREIGN KEY`, and InnoDB rollbacks in production.

Acceptance testing in the browser performs complete end-to-end testing of the UI, covering login, role-based functionality, forms, small screen size (`375x812`), keyboard navigation (`Tab`), and persistence after refresh.

## 2. Avoiding the Rollback Test False-Positive Risk

By adding `@Transactional` to a test method in Spring, a single outer transaction will be created, which will automatically be rolled back by Spring Test. This means we cannot determine if `AppointmentService.bookAppointment` would have committed or rolled back in isolation. In `Week16MysqlTransactionTests`, no test methods have `@Transactional`. Each test method calls a proxy of `appointmentService` (managed by Spring) so that its `@Transactional` transaction either commits or throws `ResourceNotFoundException` and is rolled back. The test then gets an updated version of the veterinarian and appointment data using repository methods.

## 3. Diagnosed Cross-Layer Defects Fixed in Week 16

An incorrect request parameter (`sort`/`page`/`size`) caused an error of status `500` (`PetService.java`). Requesting `?sort=dropTable` caused a `PropertyReferenceException`, which was caught by `handleGeneric()` in `GlobalExceptionHandler` and returned error `500`. To fix this, validation was added so that `sort` must be one of `ALLOWED_SORTS`, and `page >= 0` and `size >= 1` are checked in the service layer to throw an `IllegalArgumentException` (error `400`).

An out-of-date `#pet-detail` panel and scrolling issues in mobile views occurred in `app.js`, `index.html`, and `styles.css`. When editing or deleting a pet, `#pet-detail` did not update and became out of date, and tables also caused horizontal page scrolling on small screens (`375px`). To fix this, `currentDetailPetId` is now tracked so that when a `PUT` is performed the detail updates, or if a `DELETE` is performed it is cleared, while tables are wrapped with `.table-wrap` to apply `overflow-x: auto` and `:focus-visible` outlines were added.

Schema clean-up was needed for new MySQL databases because Week 7–9 lab entities (`Task`, `Department`, `Employee`, `Student`, `Course`, `Enrollment`, `User`) were not included in `schema.sql` in `infoman1/week13`, so using `ddl-auto=validate` resulted in an error as there were no matching tables in `vetclinic_f4_test`. Outdated lab entities were removed in `week16` so JPA entities correlate correctly with the SQL schema in `infoman1`.

## 4. Test Configuration Isolation and Safe Logging Decisions

`application-test.properties` was added in the `src/test/resources` folder so that tests run on disposable database `vetclinic_f4_test` (`jdbc:mysql://localhost:3307/vetclinic_f4_test`). Tests are run as the limited user `vetclinic_app` so no changes can be made to `webdev2_week8`.

`GlobalExceptionHandler` catches unexpected `500` errors and returns a clear JSON error response containing `status`, `message`, `timestamp`, and `errors`, while not returning stack traces, passwords, or session cookies.
