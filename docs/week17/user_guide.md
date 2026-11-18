# Vet Clinic Manager User Guide

1. **Sign In (`Clinic Account`):** Open `http://localhost:8080/`. Enter a provisioned `READER` (`clinic_viewer`) or `MANAGER` (`clinic_manager`) account and click `Sign In`.
2. **Browse & Lookup (`READER` and `MANAGER`):**
   - View paginated **Pet List**, filter by species (`Filter` / `Reset`), or page with `Prev` / `Next`.
   - Click `View` on a row or search by Pet ID in **Pet Detail**.
   - Inspect **Owners** and **Scheduled Appointments**.
3. **Create, Edit, and Delete (`MANAGER` only):**
   - **Add / Edit Pet:** Enter unique microchip (`MC-XXXX`), name (`2–100` chars), species, optional breed, and choose an **Owner** from the dropdown. Click `Save Pet` (`Cancel Edit` resets without saving).
   - **Add / Edit Owner:** Enter name (`2–100` chars) and optional phone.
   - **Delete Rules:** Clicking `Delete` prompts confirmation. Deleting an owner with pets or a pet with appointments is blocked (`409 Conflict`) to preserve clinical history.
4. **Book Appointment (`MANAGER` only):**
   - Select a **Pet**, **Veterinarian** (showing live `availableSlots`), date, reason, and non-negative fee, then click `Book Appointment`.
   - One pet cannot hold two appointments on the same date (`409`), and veterinarians with `0` remaining slots reject bookings without partial updates.
5. **Sign Out:** Click `Sign Out` in the top panel to end your session and clear visible records.
