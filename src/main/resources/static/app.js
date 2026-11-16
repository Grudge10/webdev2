const API_BASE = "http://localhost:8080";

let currentPage = 0;
let totalPages = 1;
let currentSpecies = "";

const petsStatus = document.querySelector("#pets-status");
const petsBody = document.querySelector("#pets-body");
const pageInfo = document.querySelector("#page-info");
const prevBtn = document.querySelector("#prev-btn");
const nextBtn = document.querySelector("#next-btn");
const speciesInput = document.querySelector("#species-input");

const detailStatus = document.querySelector("#detail-status");
const petDetail = document.querySelector("#pet-detail");
const petIdInput = document.querySelector("#pet-id-input");

const ownersStatus = document.querySelector("#owners-status");
const ownersList = document.querySelector("#owners-list");

async function getJson(path) {
    const response = await fetch(`${API_BASE}${path}`);
    if (!response.ok) {
        let message = `Request failed (${response.status})`;
        try {
            const data = await response.json();
            if (data && data.message) {
                message = data.message;
            }
        } catch (e) {
        }
        throw new Error(message);
    }
    return response.json();
}

async function loadPets() {
    petsBody.replaceChildren();
    petsStatus.className = "status";
    petsStatus.textContent = "Loading...";

    let url = `/api/pets?page=${currentPage}&size=5&sort=id&direction=asc`;
    if (currentSpecies) {
        url += `&species=${encodeURIComponent(currentSpecies)}`;
    }

    try {
        const page = await getJson(url);
        const rows = page.content;
        if (!Array.isArray(rows)) {
            throw new Error("Unexpected list response");
        }

        totalPages = Math.max(page.totalPages || 1, 1);
        pageInfo.textContent = `Page ${(page.number || 0) + 1} of ${totalPages}`;
        prevBtn.disabled = Boolean(page.first);
        nextBtn.disabled = Boolean(page.last);

        for (const pet of rows) {
            const tr = document.createElement("tr");
            const values = [pet.id, pet.name, pet.species, pet.ownerName || "None"];
            for (const val of values) {
                const td = document.createElement("td");
                td.textContent = val;
                tr.append(td);
            }
            const actionTd = document.createElement("td");
            const btn = document.createElement("button");
            btn.type = "button";
            btn.textContent = "View";
            btn.addEventListener("click", () => {
                petIdInput.value = pet.id;
                loadPetDetail(pet.id);
            });
            actionTd.append(btn);
            tr.append(actionTd);
            petsBody.append(tr);
        }

        petsStatus.textContent = rows.length ? "" : "No pets found.";
    } catch (error) {
        petsStatus.className = "status error";
        petsStatus.textContent = error.message;
    }
}

async function loadPetDetail(id) {
    petDetail.replaceChildren();
    if (!id) {
        detailStatus.className = "status error";
        detailStatus.textContent = "Please enter a pet ID.";
        return;
    }

    detailStatus.className = "status";
    detailStatus.textContent = "Loading details...";

    try {
        const pet = await getJson(`/api/pets/${id}`);
        detailStatus.textContent = "";
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
        detailStatus.className = "status error";
        detailStatus.textContent = error.message;
    }
}

async function loadOwners() {
    ownersList.replaceChildren();
    ownersStatus.className = "status";
    ownersStatus.textContent = "Loading...";

    try {
        const owners = await getJson("/api/owners");
        for (const owner of owners) {
            const li = document.createElement("li");
            li.textContent = `${owner.name} (${owner.phone || "No phone"})`;
            ownersList.append(li);
        }
        ownersStatus.textContent = owners.length ? "" : "No owners yet.";
    } catch (error) {
        ownersStatus.className = "status error";
        ownersStatus.textContent = error.message;
    }
}

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

loadPets();
loadOwners();
