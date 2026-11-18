const API_BASE = window.location.origin === "http://localhost:5500" ? "http://localhost:8080" : "";

let currentPage = 0;
let totalPages = 1;
let currentSpecies = "";
let editingPetId = null;
let editingOwnerId = null;

const petForm = document.querySelector("#pet-form");
const petFormTitle = document.querySelector("#pet-form-title");
const petMicrochipInput = document.querySelector("#pet-microchip");
const petNameInput = document.querySelector("#pet-name");
const petSpeciesInput = document.querySelector("#pet-species");
const petBreedInput = document.querySelector("#pet-breed");
const petOwnerSelect = document.querySelector("#pet-owner-select");
const petSaveBtn = document.querySelector("#pet-save-btn");
const petCancelBtn = document.querySelector("#pet-cancel-btn");
const petFormStatus = document.querySelector("#pet-form-status");

const petsStatus = document.querySelector("#pets-status");
const petsBody = document.querySelector("#pets-body");
const pageInfo = document.querySelector("#page-info");
const prevBtn = document.querySelector("#prev-btn");
const nextBtn = document.querySelector("#next-btn");
const speciesInput = document.querySelector("#species-input");

const detailStatus = document.querySelector("#detail-status");
const petDetail = document.querySelector("#pet-detail");
const petIdInput = document.querySelector("#pet-id-input");

const ownerForm = document.querySelector("#owner-form");
const ownerFormTitle = document.querySelector("#owner-form-title");
const ownerNameInput = document.querySelector("#owner-name");
const ownerPhoneInput = document.querySelector("#owner-phone");
const ownerSaveBtn = document.querySelector("#owner-save-btn");
const ownerCancelBtn = document.querySelector("#owner-cancel-btn");
const ownersStatus = document.querySelector("#owners-status");
const ownersBody = document.querySelector("#owners-body");

const bookingForm = document.querySelector("#booking-form");
const bookingPetSelect = document.querySelector("#booking-pet-select");
const bookingVetSelect = document.querySelector("#booking-vet-select");
const bookingDateInput = document.querySelector("#booking-date");
const bookingReasonInput = document.querySelector("#booking-reason");
const bookingFeeInput = document.querySelector("#booking-fee");
const bookingSaveBtn = document.querySelector("#booking-save-btn");
const bookingStatus = document.querySelector("#booking-status");
const appointmentsBody = document.querySelector("#appointments-body");

async function request(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, options);
    const type = response.headers.get("content-type") || "";
    let body = null;
    if (response.status !== 204) {
        if (type.includes("application/json")) {
            body = await response.json();
        } else {
            body = await response.text();
        }
    }

    if (!response.ok) {
        let message = `Request failed (${response.status})`;
        if (typeof body === "object" && body !== null) {
            if (Array.isArray(body.errors) && body.errors.length > 0) {
                message = body.errors.map((err) => `${err.field}: ${err.message}`).join("; ");
            } else if (body.message) {
                message = body.message;
            }
        }
        const error = new Error(message);
        error.status = response.status;
        error.details = body;
        throw error;
    }
    return body;
}

function setStatus(element, text, type = "") {
    element.className = type ? `status ${type}` : "status";
    element.textContent = text;
}

function resetPetForm() {
    editingPetId = null;
    petFormTitle.textContent = "Add Pet";
    petSaveBtn.textContent = "Save Pet";
    petCancelBtn.hidden = true;
    petForm.reset();
}

function startEditPet(pet) {
    editingPetId = pet.id;
    petFormTitle.textContent = `Edit Pet #${pet.id}`;
    petSaveBtn.textContent = "Update Pet";
    petCancelBtn.hidden = false;
    petMicrochipInput.value = pet.microchipNumber || "";
    petNameInput.value = pet.name || "";
    petSpeciesInput.value = pet.species || "";
    petBreedInput.value = pet.breed || "";
    petOwnerSelect.value = pet.ownerId ? String(pet.ownerId) : "";
    setStatus(petFormStatus, "");
}

function resetOwnerForm() {
    editingOwnerId = null;
    ownerFormTitle.textContent = "Add Owner";
    ownerSaveBtn.textContent = "Save Owner";
    ownerCancelBtn.hidden = true;
    ownerForm.reset();
}

function startEditOwner(owner) {
    editingOwnerId = owner.id;
    ownerFormTitle.textContent = `Edit Owner #${owner.id}`;
    ownerSaveBtn.textContent = "Update Owner";
    ownerCancelBtn.hidden = false;
    ownerNameInput.value = owner.name || "";
    ownerPhoneInput.value = owner.phone || "";
    setStatus(ownersStatus, "");
}

async function loadPets() {
    petsBody.replaceChildren();
    setStatus(petsStatus, "Loading...");

    let url = `/api/pets?page=${currentPage}&size=5&sort=id&direction=asc`;
    if (currentSpecies) {
        url += `&species=${encodeURIComponent(currentSpecies)}`;
    }

    try {
        const page = await request(url);
        const rows = page.content;
        if (!Array.isArray(rows)) {
            throw new Error("Unexpected list response");
        }

        if (rows.length === 0 && currentPage > 0 && page.totalPages > 0) {
            currentPage = Math.max(page.totalPages - 1, 0);
            return loadPets();
        }

        totalPages = Math.max(page.totalPages || 1, 1);
        pageInfo.textContent = `Page ${(page.number || 0) + 1} of ${totalPages}`;
        prevBtn.disabled = Boolean(page.first);
        nextBtn.disabled = Boolean(page.last);

        for (const pet of rows) {
            const tr = document.createElement("tr");
            const values = [pet.id, pet.microchipNumber, pet.name, pet.species, pet.ownerName || "None"];
            for (const val of values) {
                const td = document.createElement("td");
                td.textContent = val;
                tr.append(td);
            }

            const actionTd = document.createElement("td");

            const viewBtn = document.createElement("button");
            viewBtn.type = "button";
            viewBtn.textContent = "View";
            viewBtn.addEventListener("click", () => {
                petIdInput.value = pet.id;
                loadPetDetail(pet.id);
            });

            const editBtn = document.createElement("button");
            editBtn.type = "button";
            editBtn.textContent = "Edit";
            editBtn.addEventListener("click", () => startEditPet(pet));

            const deleteBtn = document.createElement("button");
            deleteBtn.type = "button";
            deleteBtn.textContent = "Delete";
            deleteBtn.addEventListener("click", () => deletePet(pet, rows.length));

            actionTd.append(viewBtn, editBtn, deleteBtn);
            tr.append(actionTd);
            petsBody.append(tr);
        }

        setStatus(petsStatus, rows.length ? "" : "No pets found.");
    } catch (error) {
        setStatus(petsStatus, error.message, "error");
    }
}

async function deletePet(pet, rowCount) {
    if (!window.confirm(`Delete pet "${pet.name}" (ID ${pet.id})?`)) {
        return;
    }
    try {
        await request(`/api/pets/${pet.id}`, { method: "DELETE" });
        if (rowCount === 1 && currentPage > 0) {
            currentPage -= 1;
        }
        if (editingPetId === pet.id) {
            resetPetForm();
        }
        setStatus(petFormStatus, `Deleted pet "${pet.name}".`, "success");
        await loadPets();
        await loadPetOptions();
    } catch (error) {
        setStatus(petsStatus, error.message, "error");
    }
}

async function loadPetDetail(id) {
    petDetail.replaceChildren();
    if (!id) {
        setStatus(detailStatus, "Please enter a pet ID.", "error");
        return;
    }

    setStatus(detailStatus, "Loading details...");

    try {
        const pet = await request(`/api/pets/${id}`);
        setStatus(detailStatus, "");
        const lines = [
            `ID: ${pet.id}`,
            `Name: ${pet.name}`,
            `Species: ${pet.species}`,
            `Breed: ${pet.breed || "None"}`,
            `Microchip: ${pet.microchipNumber}`,
            `Owner: ${pet.ownerName || "None"}`
        ];
        for (const line of lines) {
            const p = document.createElement("p");
            p.textContent = line;
            petDetail.append(p);
        }
    } catch (error) {
        setStatus(detailStatus, error.message, "error");
    }
}

async function loadOwners() {
    ownersBody.replaceChildren();
    setStatus(ownersStatus, "Loading owners...");

    try {
        const owners = await request("/api/owners");
        const selectedOwner = petOwnerSelect.value;
        petOwnerSelect.replaceChildren();

        const noneOption = document.createElement("option");
        noneOption.value = "";
        noneOption.textContent = "No owner selected";
        petOwnerSelect.append(noneOption);

        for (const owner of owners) {
            const opt = document.createElement("option");
            opt.value = String(owner.id);
            opt.textContent = owner.name;
            petOwnerSelect.append(opt);

            const tr = document.createElement("tr");
            for (const val of [owner.id, owner.name, owner.phone || "None"]) {
                const td = document.createElement("td");
                td.textContent = val;
                tr.append(td);
            }
            const actionTd = document.createElement("td");

            const editBtn = document.createElement("button");
            editBtn.type = "button";
            editBtn.textContent = "Edit";
            editBtn.addEventListener("click", () => startEditOwner(owner));

            const deleteBtn = document.createElement("button");
            deleteBtn.type = "button";
            deleteBtn.textContent = "Delete";
            deleteBtn.addEventListener("click", () => deleteOwner(owner));

            actionTd.append(editBtn, deleteBtn);
            tr.append(actionTd);
            ownersBody.append(tr);
        }

        petOwnerSelect.value = selectedOwner;
        setStatus(ownersStatus, owners.length ? "" : "No owners yet.");
    } catch (error) {
        setStatus(ownersStatus, error.message, "error");
    }
}

async function deleteOwner(owner) {
    if (!window.confirm(`Delete owner "${owner.name}" (ID ${owner.id})?`)) {
        return;
    }
    try {
        await request(`/api/owners/${owner.id}`, { method: "DELETE" });
        if (editingOwnerId === owner.id) {
            resetOwnerForm();
        }
        await loadOwners();
        setStatus(ownersStatus, `Deleted owner "${owner.name}".`, "success");
    } catch (error) {
        setStatus(ownersStatus, error.message, "error");
    }
}

async function loadPetOptions() {
    try {
        const page = await request("/api/pets?page=0&size=100&sort=id&direction=asc");
        const previous = bookingPetSelect.value;
        bookingPetSelect.replaceChildren();
        for (const pet of page.content || []) {
            const opt = document.createElement("option");
            opt.value = String(pet.id);
            opt.textContent = `${pet.name} (${pet.microchipNumber})`;
            bookingPetSelect.append(opt);
        }
        if (previous) {
            bookingPetSelect.value = previous;
        }
    } catch (error) {
        setStatus(bookingStatus, error.message, "error");
    }
}

async function loadVets() {
    try {
        const vets = await request("/api/veterinarians");
        const previous = bookingVetSelect.value;
        bookingVetSelect.replaceChildren();
        for (const vet of vets) {
            const opt = document.createElement("option");
            opt.value = String(vet.id);
            opt.textContent = `${vet.name} - ${vet.specialty || "General"} (${vet.availableSlots} slots left)`;
            bookingVetSelect.append(opt);
        }
        if (previous) {
            bookingVetSelect.value = previous;
        }
    } catch (error) {
        setStatus(bookingStatus, error.message, "error");
    }
}

async function loadAppointments() {
    appointmentsBody.replaceChildren();
    try {
        const items = await request("/api/appointments");
        for (const item of items) {
            const tr = document.createElement("tr");
            const values = [
                item.id,
                item.appointmentDate,
                item.petName,
                item.ownerName || "None",
                item.vetName,
                item.reason,
                Number(item.fee).toFixed(2)
            ];
            for (const val of values) {
                const td = document.createElement("td");
                td.textContent = val;
                tr.append(td);
            }
            appointmentsBody.append(tr);
        }
    } catch (error) {
        setStatus(bookingStatus, error.message, "error");
    }
}

petForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const microchipNumber = petMicrochipInput.value.trim();
    const name = petNameInput.value.trim();
    const species = petSpeciesInput.value.trim();
    const breed = petBreedInput.value.trim();
    const ownerId = petOwnerSelect.value ? Number(petOwnerSelect.value) : null;

    if (!microchipNumber || !name || !species) {
        setStatus(petFormStatus, "Microchip number, name, and species are required.", "error");
        return;
    }

    petSaveBtn.disabled = true;
    setStatus(petFormStatus, "Saving pet...");

    try {
        const payload = { microchipNumber, name, species, breed: breed || null, ownerId };
        if (editingPetId === null) {
            await request("/api/pets", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            setStatus(petFormStatus, `Created pet "${name}".`, "success");
        } else {
            await request(`/api/pets/${editingPetId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            setStatus(petFormStatus, `Updated pet "${name}".`, "success");
        }
        resetPetForm();
        await loadPets();
        await loadPetOptions();
    } catch (error) {
        setStatus(petFormStatus, error.message, "error");
    } finally {
        petSaveBtn.disabled = false;
    }
});

petCancelBtn.addEventListener("click", () => {
    resetPetForm();
    setStatus(petFormStatus, "");
});

ownerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const name = ownerNameInput.value.trim();
    const phone = ownerPhoneInput.value.trim();

    if (!name) {
        setStatus(ownersStatus, "Owner name is required.", "error");
        return;
    }

    ownerSaveBtn.disabled = true;
    setStatus(ownersStatus, "Saving owner...");

    try {
        const payload = { name, phone: phone || null };
        if (editingOwnerId === null) {
            await request("/api/owners", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            resetOwnerForm();
            await loadOwners();
            setStatus(ownersStatus, `Created owner "${name}".`, "success");
        } else {
            await request(`/api/owners/${editingOwnerId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            resetOwnerForm();
            await loadOwners();
            await loadPets();
            await loadAppointments();
            setStatus(ownersStatus, `Updated owner "${name}".`, "success");
        }
    } catch (error) {
        setStatus(ownersStatus, error.message, "error");
    } finally {
        ownerSaveBtn.disabled = false;
    }
});

ownerCancelBtn.addEventListener("click", () => {
    resetOwnerForm();
    setStatus(ownersStatus, "");
});

bookingForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    if (!bookingPetSelect.value || !bookingVetSelect.value || !bookingDateInput.value) {
        setStatus(bookingStatus, "Select a pet, veterinarian, and date.", "error");
        return;
    }

    bookingSaveBtn.disabled = true;
    setStatus(bookingStatus, "Booking appointment...");

    try {
        const payload = {
            petId: Number(bookingPetSelect.value),
            vetId: Number(bookingVetSelect.value),
            appointmentDate: bookingDateInput.value,
            reason: bookingReasonInput.value.trim(),
            fee: Number(bookingFeeInput.value)
        };
        const booked = await request("/api/appointments/book", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        bookingReasonInput.value = "";
        await loadVets();
        await loadAppointments();
        setStatus(bookingStatus, `Booked appointment #${booked.id} for ${booked.petName} with ${booked.vetName}.`, "success");
    } catch (error) {
        setStatus(bookingStatus, error.message, "error");
    } finally {
        bookingSaveBtn.disabled = false;
    }
});

document.querySelector("#filter-form").addEventListener("submit", (e) => {
    e.preventDefault();
    currentSpecies = speciesInput.value.trim();
    currentPage = 0;
    loadPets();
});

document.querySelector("#reset-btn").addEventListener("click", () => {
    speciesInput.value = "";
    currentSpecies = "";
    currentPage = 0;
    loadPets();
});

prevBtn.addEventListener("click", () => {
    if (currentPage > 0) {
        currentPage -= 1;
        loadPets();
    }
});

nextBtn.addEventListener("click", () => {
    if (currentPage + 1 < totalPages) {
        currentPage += 1;
        loadPets();
    }
});

document.querySelector("#lookup-form").addEventListener("submit", (e) => {
    e.preventDefault();
    loadPetDetail(petIdInput.value.trim());
});

async function init() {
    await loadPets();
    await loadOwners();
    await loadPetOptions();
    await loadVets();
    await loadAppointments();
}

init();
