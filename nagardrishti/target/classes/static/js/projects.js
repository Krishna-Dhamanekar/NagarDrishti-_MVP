if (!Session.requireLogin()) throw new Error("Not logged in");
initNavbar();

const user = Session.get();
const pincodeInput = document.getElementById("pincodeInput");
const btnSearch    = document.getElementById("btnSearch");
let allProjects    = [];
let catFilter      = "ALL";

if (user?.pincode) pincodeInput.value = user.pincode;

btnSearch.addEventListener("click", search);
pincodeInput.addEventListener("keydown", e => { if(e.key==="Enter") search(); });

async function search() {
    const pin = pincodeInput.value.trim();
    if (!/^\d{6}$/.test(pin)) {
        document.getElementById("projectsContainer").innerHTML =
            `<div class="empty-state"><div class="empty-icon">⚠️</div><div class="empty-title">Invalid pincode</div><p>Please enter a 6-digit pincode.</p></div>`;
        return;
    }
    document.getElementById("projectsContainer").innerHTML = `<div class="loading"><div class="spinner"></div><div>Loading...</div></div>`;
    document.getElementById("statsGrid").style.display = "none";
    document.getElementById("filterBar").style.display = "none";
    try {
        const [projects, stats] = await Promise.all([
            apiGet(`/projects/locality?pincode=${pin}`),
            apiGet(`/projects/stats?pincode=${pin}`)
        ]);
        allProjects = projects || [];
        renderStats(stats);
        catFilter = "ALL";
        document.querySelectorAll(".filter-chip").forEach(c => c.classList.toggle("active", c.dataset.value==="ALL"));
        renderProjects();
    } catch(e) {
        document.getElementById("projectsContainer").innerHTML =
            `<div class="empty-state"><div class="empty-icon">⚠️</div><div class="empty-title">Error</div><p>${esc(e.message)}</p></div>`;
    }
}

function renderStats(s) {
    document.getElementById("statTotal").textContent     = s.totalProjects ?? 0;
    document.getElementById("statBudget").textContent    = formatINR(s.totalBudgetAllocated);
    document.getElementById("statSpent").textContent     = "Spent: " + formatINR(s.totalBudgetSpent);
    document.getElementById("statCompleted").textContent = s.completedProjects ?? 0;
    document.getElementById("statProgress").textContent  = s.inProgressProjects ?? 0;
    document.getElementById("statDelayed").textContent   = s.delayedProjects ?? 0;
    document.getElementById("statsGrid").style.display = s.totalProjects > 0 ? "grid" : "none";
    document.getElementById("filterBar").style.display = s.totalProjects > 0 ? "flex" : "none";
}

document.querySelectorAll(".filter-chip").forEach(c => c.addEventListener("click", () => {
    document.querySelectorAll(".filter-chip").forEach(x => x.classList.remove("active"));
    c.classList.add("active"); catFilter = c.dataset.value; renderProjects();
}));

const STATUS_BADGE = {COMPLETED:"badge-green",IN_PROGRESS:"badge-blue",DELAYED:"badge-red",PLANNED:"badge-gray"};
const CAT_BADGE    = {ROAD:"badge-sky",WATER:"badge-blue",HEALTH:"badge-red",HOUSING:"badge-amber",SCHOOL:"badge-blue",WOMEN:"badge-blue",AGRICULTURE:"badge-green",ELECTRICITY:"badge-amber"};

function renderProjects() {
    const filtered = catFilter === "ALL" ? allProjects : allProjects.filter(p => p.category === catFilter);
    if (!filtered.length) {
        document.getElementById("projectsContainer").innerHTML =
            `<div class="empty-state"><div class="empty-icon">📭</div><div class="empty-title">No projects found</div><p>${allProjects.length ? "Try another category." : "No projects recorded for this pincode."}</p></div>`;
        return;
    }
    document.getElementById("projectsContainer").innerHTML =
        `<div class="card-grid">${filtered.map(p => projectCard(p)).join("")}</div>`;
    filtered.forEach(p => loadFeedbackSummary(p.id, `fb-${p.id}`));
}

function projectCard(p) {
    const alloc = p.budgetAllocated || 0, spent = p.budgetSpent || 0;
    const spentPct = alloc > 0 ? Math.min(100, Math.round(spent/alloc*100)) : 0;
    const fillColor = p.status==="COMPLETED" ? "green" : p.status==="DELAYED" ? "red" : "";
    return `
    <div class="card">
      <div class="card-header">
        <div><div class="card-title">${esc(p.name)}</div><div class="card-sub">${esc(p.sanctioningAuthority||"")}</div></div>
        <span class="badge ${STATUS_BADGE[p.status]||"badge-gray"}">${esc(prettyStatus(p.status))}</span>
      </div>
      <div class="card-desc">${esc(p.description||"")}</div>
      <span class="badge ${CAT_BADGE[p.category]||"badge-gray"}" style="margin-bottom:12px">${esc(p.category)}</span>
      <div class="budget-labels"><span>Budget Allocated</span><span>Spent</span></div>
      <div class="budget-values"><span>${formatINR(alloc)}</span><span>${formatINR(spent)}</span></div>
      <div class="progress-bar"><div class="progress-fill ${fillColor}" style="width:${spentPct}%"></div></div>
      <div style="display:flex;justify-content:space-between;font-size:.85rem;margin-top:6px">
        <span>Completion</span><strong>${p.completionPercentage??0}%</strong>
      </div>
      <div class="progress-bar"><div class="progress-fill ${fillColor}" style="width:${p.completionPercentage??0}%"></div></div>
      <div class="feedback-section">
        <div class="feedback-summary" id="fb-${esc(p.id)}"><span class="feedback-pill">Loading feedback...</span></div>
        <button class="btn-feedback" onclick="goToFeedback('${esc(p.id)}','${esc(p.name)}','PROJECT_COMPLAINT')">
          ⚠ Report issue
        </button>
      </div>
      <div class="card-footer">
        <span class="card-meta">Expected: ${formatDate(p.expectedCompletionDate)}</span>
        <span class="card-meta" style="font-weight:600">${esc(p.contractor||"")}</span>
      </div>
    </div>`;
}

if (pincodeInput.value) search();
