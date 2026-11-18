# WEBDEV2 Week 16 Test Matrix (F4)

| ID | Category | Test / Scenario | Preconditions / Input | Expected | Actual Observed Result | Source / Evidence |
|---|---|---|---|---|---|---|
| SVC-1 | Service Unit | `rejectsOwnerDeleteWhenOwnerHasRegisteredPets` | `ownerId=1` has pets (mocked) | Throws `ConflictException`, never deletes | Passed; `deleteById` never called | `ClinicServiceUnitTests.java` |
| SVC-2 | Service Unit | `rejectsAppointmentWhenVeterinarianHasNoAvailableSlots` | `vet` has `0` slots (`decrementSlot` returns `0`) | Throws `ConflictException`, never saves appt | Passed; `saveAndFlush` never called | `ClinicServiceUnitTests.java` |
| SVC-3 | Service Unit | `rejectsAppointmentWhenPetAlreadyBookedOnSameDate` | Pet already has appointment on `2026-11-18` | Throws `ConflictException` | Passed; `saveAndFlush` never called | `ClinicServiceUnitTests.java` |
| SVC-4 | Service Unit | `rejectsUnsupportedSortFieldOnPetList` | `sort="unknownField"` | Throws `IllegalArgumentException` | Passed (`400` in API) | `ClinicServiceUnitTests.java` |
| API-1 | API / Security | `rejectsAnonymousProtectedReadWithUnauthorized` | Unauthenticated `GET /api/pets` | `401` JSON | `401` `{"status":401,...}` | `Week16ApiSecurityTests.java` |
| API-2 | API / Security | `rejectsReaderMutationEvenWithValidCsrf` | `READER` + `.with(csrf())` `POST /api/pets` | `403` JSON | `403` `{"status":403,...}` | `Week16ApiSecurityTests.java` |
| API-3 | API / Security | `rejectsInvalidPetPayloadWithValidationErrors` | `MANAGER` + CSRF, name `"A"` | `400` with `errors[0].field = "name"` | `400` field error | `Week16ApiSecurityTests.java` |
| API-4 | API / Security | `rejectsDuplicateMicrochipAndReturnsNotFoundForMissingPet` | Duplicate `"MC-1001"`; `GET /api/pets/999999` | `409` then `404` | `409` and `404` | `Week16ApiSecurityTests.java` |
| API-5 | API / Security | `rejectsInvalidSortParameterWithBadRequest` | `GET /api/pets?sort=dropTable` | `400` JSON | `400` Bad Request | `Week16ApiSecurityTests.java` |
| API-6 | API / Security | `allowsManagerToCreateUpdateAndDeletePet` | `MANAGER` + CSRF create, `PUT`, `DELETE` | `201`, `200`, `204` | All statuses matched | `Week16ApiSecurityTests.java` |
| DB-1 | MySQL Integration | `commitsAppointmentAndDecrementsVeterinarianSlotInMysql` | `vetId=1`, `petId=4`, `2026-12-15`, `700.00` | Vet slots `3 -> 2`, appt count `+1` | Committed in `vetclinic_f4_test`, cleaned up | `Week16MysqlTransactionTests.java` |
| DB-2 | MySQL Integration | `rollsBackVeterinarianSlotWhenSecondWriteFailsAfterDecrement` | `vetId=2`, missing `petId=999999` after decrement | `ResourceNotFoundException`, vet slots `3`, appts unchanged | Rolled back cleanly in `vetclinic_f4_test` | `Week16MysqlTransactionTests.java` |
| UI-1 | Browser UAT | Signed-out & wrong password login | Open `/`, submit bad password | Workspace hidden; `401` message shown | `#clinic-workspace` hidden; `"Invalid username or password"` | `index.html`, `app.js` |
| UI-2 | Browser UAT | `READER` login, empty `"Bird"` search, logout | Login `clinic_viewer`, filter `"Bird"`, Sign Out | Forms hidden, `"No pets found."`, clean logout | Empty state shown; `#user-panel` hid on sign out | `app.js`, `styles.css` |
| UI-3 | Browser UAT | `MANAGER` create, detail sync on `PUT`, reload | Create `"Mocha"`, View, Edit breed, reload | `#pet-detail` updates live; persists after reload | `Breed: Siamese Mix` updated & survived reload | `app.js` |
| UI-4 | Browser UAT | Invalid form validation (`400`) | Submit Pet `"MC-F4BAD"`, name `"A"` | `400` message, inputs stay filled | Field error shown; inputs preserved | `app.js`, `PetController.java` |
| UI-5 | Browser UAT | Delete `409` vs `204` + `#pet-detail` clear | Delete `"Buddy"`, then View & Delete `"Mocha"` | `409` blocked; `204` deletes & clears detail | `"Cannot delete pet..."` then `"Select a pet..."` | `PetService.java`, `app.js` |
| UI-6 | Browser UAT | Atomic booking commit & duplicate rollback | Book Milo + Dr. Reyes (`2026-12-03`); Buddy duplicate (`2026-11-18`) | `201` (`3 -> 2` slots); `409` (Dr. Villanueva stays `3`) | Observed slots `2` and `3`, appt ID `11` | `AppointmentService.java` |
| UI-7 | Browser UAT | Small viewport (`375x812`) & keyboard Tab navigation | Resize to `375x812`, Tab/Enter Filter & Reset | No page overflow (`375px`), visible focus ring | `.table-wrap` scrolled tables inside `301px`; `Enter` worked | `styles.css`, `index.html` |
| UI-8 | Browser UAT | Logout clears DOM & session | Click `Sign Out` | `POST /api/auth/logout` `204`, workspace hidden | Tables cleared (`0` rows), login panel returned | `AuthController.java`, `app.js` |
