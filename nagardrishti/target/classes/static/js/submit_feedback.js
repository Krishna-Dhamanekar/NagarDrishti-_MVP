if (!Session.requireLogin()) throw new Error("Not logged in");
initNavbar();

const user   = Session.get();
const params = new URLSearchParams(location.search);
const relatedId   = params.get("relatedId")   || "";
const relatedName = params.get("relatedName") || "Item";
const typeParam   = params.get("type")        || "";

// Pre-fill from URL
document.getElementById("relatedName").textContent = relatedName;

// Pre-select type tab
let activeType = typeParam || "PROJECT_COMPLAINT";
selectType(activeType);

document.querySelectorAll(".type-tab").forEach(t => t.addEventListener("click", () => selectType(t.dataset.type)));

function selectType(type) {
    activeType = type;
    document.querySelectorAll(".type-tab").forEach(t => t.classList.toggle("active", t.dataset.type === type));
    document.getElementById("complaintFields").classList.toggle("show", type === "PROJECT_COMPLAINT");
    document.getElementById("schemeFields").classList.toggle("show", type === "SCHEME_VERIFICATION");
}

// Scheme received radio
let schemeReceived = null;
document.querySelectorAll(".radio-option").forEach(opt => {
    opt.addEventListener("click", () => {
        document.querySelectorAll(".radio-option").forEach(x => x.classList.remove("selected"));
        opt.classList.add("selected");
        schemeReceived = opt.dataset.value === "yes";
        document.getElementById("amountFields").classList.toggle("show", schemeReceived);
        document.getElementById("noReceiveMsg").classList.toggle("show", !schemeReceived);
    });
});

// Photo upload preview
document.getElementById("photoInput").addEventListener("change", e => {
    const file = e.target.files[0];
    if (!file) return;
    const preview = document.getElementById("photoPreview");
    preview.src = URL.createObjectURL(file);
    preview.style.display = "block";
    document.getElementById("photoLabel").textContent = file.name;
});

// Submit
document.getElementById("feedbackForm").addEventListener("submit", async e => {
    e.preventDefault();
    const btn = document.getElementById("submitBtn");
    btn.disabled = true; btn.textContent = "Submitting...";

    const desc = document.getElementById("description").value.trim();
    if (desc.length < 10) {
        showAlert("Description must be at least 10 characters.", "error");
        btn.disabled = false; btn.textContent = "Submit Feedback"; return;
    }

    const payload = {
        userId:      user.userId,
        type:        activeType,
        relatedId,
        relatedName,
        description: desc,
        complaintCategory: document.getElementById("complaintCategory")?.value || null,
        schemeReceived:    activeType === "SCHEME_VERIFICATION" ? schemeReceived : null,
        amountReceived:    parseFloat(document.getElementById("amountReceived")?.value) || null,
        amountExpected:    parseFloat(document.getElementById("amountExpected")?.value) || null,
        receivedMonth:     document.getElementById("receivedMonth")?.value || null,
        photoUrl:          null
    };

    try {
        await apiPost("/feedback", payload);
        showAlert("Feedback submitted! Thank you for contributing.", "success");
        setTimeout(() => history.back(), 1800);
    } catch(err) { showAlert(err.message, "error"); }
    finally { btn.disabled = false; btn.textContent = "Submit Feedback"; }
});
