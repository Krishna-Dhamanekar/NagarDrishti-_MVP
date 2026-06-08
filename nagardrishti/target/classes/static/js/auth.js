if (Session.isLoggedIn()) location.href = "/schemes.html";

// ── Tab switching ─────────────────────────────────────────────────────────────
const tabs      = document.querySelectorAll(".auth-tab");
const loginForm = document.getElementById("loginForm");
const regForm   = document.getElementById("registerForm");

tabs.forEach(t => t.addEventListener("click", () => {
    tabs.forEach(x => x.classList.remove("active"));
    t.classList.add("active");
    document.getElementById("alertBox").classList.remove("show");
    loginForm.style.display = t.dataset.tab === "login" ? "block" : "none";
    regForm.style.display   = t.dataset.tab === "login" ? "none"  : "block";
}));

// ── Conditional field visibility ──────────────────────────────────────────────
document.getElementById("regDisabled").addEventListener("change", function () {
    document.getElementById("disabilityPctGroup").style.display = this.checked ? "block" : "none";
    if (!this.checked) document.getElementById("regDisabilityPct").value = "";
});

document.getElementById("regOwnsLand").addEventListener("change", function () {
    document.getElementById("landAcresGroup").style.display = this.checked ? "block" : "none";
    if (!this.checked) document.getElementById("regLandAcres").value = "";
});

// ── Login ─────────────────────────────────────────────────────────────────────
loginForm.addEventListener("submit", async e => {
    e.preventDefault();
    const btn = loginForm.querySelector("button[type='submit']");
    btn.disabled = true; btn.textContent = "Logging in...";
    try {
        const d = await apiPost("/auth/login", {
            phoneNumber: document.getElementById("loginPhone").value.trim(),
            password:    document.getElementById("loginPassword").value
        });
        Session.set(d);
        showAlert("Login successful! Redirecting...", "success");
        setTimeout(() => location.href = "/schemes.html", 600);
    } catch (err) {
        showAlert(err.message, "error");
    } finally {
        btn.disabled = false; btn.textContent = "Login";
    }
});

// ── Register ──────────────────────────────────────────────────────────────────
regForm.addEventListener("submit", async e => {
    e.preventDefault();
    const btn = regForm.querySelector("button[type='submit']");
    btn.disabled = true; btn.textContent = "Creating account...";

    const isDisabled = document.getElementById("regDisabled").checked;
    const ownsLand   = document.getElementById("regOwnsLand").checked;

    const payload = {
        // Personal
        fullName:     document.getElementById("regName").value.trim(),
        phoneNumber:  document.getElementById("regPhone").value.trim(),
        email:        document.getElementById("regEmail").value.trim() || null,
        password:     document.getElementById("regPassword").value,
        age:          parseInt(document.getElementById("regAge").value),
        gender:       document.getElementById("regGender").value,

        // Location
        state:        document.getElementById("regState").value.trim(),
        district:     document.getElementById("regDistrict").value.trim() || null,
        pincode:      document.getElementById("regPincode").value.trim(),
        zone:         document.getElementById("regZone").value.trim() || null,
        ward:         document.getElementById("regWard").value.trim() || null,

        // Socio-economic
        category:        document.getElementById("regCategory").value,
        annualIncome:    parseFloat(document.getElementById("regIncome").value) || 0,
        bpl:             document.getElementById("regBpl").checked,
        educationLevel:  document.getElementById("regEducation").value,
        occupation:      document.getElementById("regOccupation").value.trim() || null,
        disabled:        isDisabled,
        disabilityPercentage: isDisabled
            ? (parseInt(document.getElementById("regDisabilityPct").value) || null)
            : null,
        ownsLand:       ownsLand,
        landInAcres:    ownsLand
            ? (parseFloat(document.getElementById("regLandAcres").value) || null)
            : null,

        // Family
        maritalStatus:        document.getElementById("regMarital").value,
        familyMembers:        parseInt(document.getElementById("regFamilyMembers").value) || 1,
        girlChildrenCount:    parseInt(document.getElementById("regGirlChildren").value) || 0,
        widow:                document.getElementById("regWidow").checked,
        seniorCitizenInFamily: document.getElementById("regSeniorCitizen").checked,

        // Documents
        aadhaarLinked:   document.getElementById("regAadhaar").checked,
        bankAccount:     document.getElementById("regBankAccount").checked,
        rationCard:      document.getElementById("regRationCard").checked,
        healthInsurance: document.getElementById("regHealthInsurance").checked,
    };

    try {
        const d = await apiPost("/auth/register", payload);
        Session.set(d);
        showAlert("Account created! Redirecting...", "success");
        setTimeout(() => location.href = "/schemes.html", 700);
    } catch (err) {
        showAlert(err.message, "error");
    } finally {
        btn.disabled = false; btn.textContent = "Create Account";
    }
});