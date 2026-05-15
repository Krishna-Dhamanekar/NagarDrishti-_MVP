if (Session.isLoggedIn()) location.href = "/schemes.html";

const tabs = document.querySelectorAll(".auth-tab");
const loginForm = document.getElementById("loginForm");
const regForm = document.getElementById("registerForm");

tabs.forEach(t => t.addEventListener("click", () => {
    tabs.forEach(x => x.classList.remove("active"));
    t.classList.add("active");
    document.getElementById("alertBox").classList.remove("show");
    loginForm.style.display = t.dataset.tab === "login" ? "block" : "none";
    regForm.style.display   = t.dataset.tab === "login" ? "none"  : "block";
}));

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
    } catch(err) { showAlert(err.message, "error"); }
    finally { btn.disabled = false; btn.textContent = "Login"; }
});

regForm.addEventListener("submit", async e => {
    e.preventDefault();
    const btn = regForm.querySelector("button[type='submit']");
    btn.disabled = true; btn.textContent = "Creating account...";
    try {
        const d = await apiPost("/auth/register", {
            name:         document.getElementById("regName").value.trim(),
            phoneNumber:  document.getElementById("regPhone").value.trim(),
            email:        document.getElementById("regEmail").value.trim() || null,
            password:     document.getElementById("regPassword").value,
            age:          parseInt(document.getElementById("regAge").value),
            gender:       document.getElementById("regGender").value,
            annualIncome: parseFloat(document.getElementById("regIncome").value),
            category:     document.getElementById("regCategory").value,
            maritalStatus:document.getElementById("regMarital").value,
            village:      document.getElementById("regVillage").value.trim(),
            pincode:      document.getElementById("regPincode").value.trim(),
            district:     document.getElementById("regDistrict").value.trim(),
            state:        document.getElementById("regState").value.trim()
        });
        Session.set(d);
        showAlert("Account created! Redirecting...", "success");
        setTimeout(() => location.href = "/schemes.html", 700);
    } catch(err) { showAlert(err.message, "error"); }
    finally { btn.disabled = false; btn.textContent = "Create Account"; }
});
