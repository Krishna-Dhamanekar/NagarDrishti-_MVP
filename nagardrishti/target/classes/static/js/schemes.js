if (!Session.requireLogin()) throw new Error("Not logged in");
initNavbar();

const user = Session.get();
let allSchemes = [];
let activeCategory = "ALL";

async function loadSchemes() {
    try {
        const r = await apiGet(`/schemes/eligible/${user.userId}`);
        allSchemes = r.schemes || [];
        document.getElementById("statCount").textContent    = r.totalEligibleSchemes;
        document.getElementById("statBenefit").textContent  = formatINR(r.totalPotentialBenefit);
        document.getElementById("statLocation").textContent = user.state || "—";
        document.getElementById("statPincode").textContent  = "Pincode: " + (user.pincode || "—");
        renderSchemes();
    } catch(e) {
        document.getElementById("schemesContainer").innerHTML =
            `<div class="empty-state"><div class="empty-icon">⚠️</div><div class="empty-title">Could not load schemes</div><p>${esc(e.message)}</p></div>`;
    }
}

document.querySelectorAll(".filter-chip").forEach(c => c.addEventListener("click", () => {
    document.querySelectorAll(".filter-chip").forEach(x => x.classList.remove("active"));
    c.classList.add("active");
    activeCategory = c.dataset.category;
    renderSchemes();
}));

const BADGE = {WOMEN:"badge-blue",FARMER:"badge-green",STUDENT:"badge-sky",ELDERLY:"badge-gray",HOUSING:"badge-amber",HEALTH:"badge-red"};

function renderSchemes() {
    const filtered = activeCategory === "ALL" ? allSchemes : allSchemes.filter(s => s.category === activeCategory);
    if (!filtered.length) {
        document.getElementById("schemesContainer").innerHTML =
            `<div class="empty-state"><div class="empty-icon">🔍</div><div class="empty-title">No matching schemes</div><p>Try a different filter.</p></div>`;
        return;
    }
    document.getElementById("schemesContainer").innerHTML =
        `<div class="card-grid">${filtered.map(s => schemeCard(s)).join("")}</div>`;

    document.querySelectorAll(".btn-view").forEach(b => b.addEventListener("click", () => openModal(b.dataset.id)));
    // Load feedback summaries for each scheme
    filtered.forEach(s => loadFeedbackSummary(s.id, `fb-${s.id}`));
}

function schemeCard(s) {
    return `
    <div class="card">
      <div class="card-header">
        <div><div class="card-title">${esc(s.name)}</div><div class="card-sub">${esc(s.shortName||"")}</div></div>
        <span class="badge ${BADGE[s.category]||"badge-gray"}">${esc(s.category)}</span>
      </div>
      <div class="card-desc">${esc(s.description||"")}</div>
      <div class="card-benefit">
        <div class="card-benefit-label">Benefit</div>
        <div class="card-benefit-amount">${formatINR(s.benefitAmount)}</div>
        <div class="card-benefit-desc">${esc(prettyStatus(s.benefitType||""))}</div>
      </div>
      <div class="feedback-section">
        <div class="feedback-summary" id="fb-${esc(s.id)}"><span class="feedback-pill">Loading feedback...</span></div>
        <div style="display:flex;gap:8px;flex-wrap:wrap">
          <button class="btn-feedback" onclick="goToFeedback('${esc(s.id)}','${esc(s.name)}','SCHEME_VERIFICATION')">
            + Submit scheme feedback
          </button>
        </div>
      </div>
      <div class="card-footer">
        <span class="card-meta">${esc(s.scope||"CENTRAL")}</span>
        <button class="btn-view" data-id="${esc(s.id)}">View Details →</button>
      </div>
    </div>`;
}

// Modal
const modal = document.getElementById("schemeModal");
document.getElementById("modalClose").onclick = () => modal.classList.remove("show");
modal.addEventListener("click", e => { if(e.target===modal) modal.classList.remove("show"); });
document.addEventListener("keydown", e => { if(e.key==="Escape") modal.classList.remove("show"); });

async function openModal(id) {
    const s = allSchemes.find(x => x.id === id);
    if (!s) return;
    document.getElementById("modalTitle").textContent    = s.name;
    document.getElementById("modalShortName").textContent = s.shortName || "";
    const docs = (s.documentsRequired || []).map(d => `<li>${esc(d)}</li>`).join("");
    document.getElementById("modalBody").innerHTML = `
      <div class="modal-section"><div class="modal-section-title">About</div><p>${esc(s.description||"")}</p></div>
      <div class="card-benefit" style="margin-bottom:18px">
        <div class="card-benefit-label">Benefit</div>
        <div class="card-benefit-amount">${formatINR(s.benefitAmount)}</div>
        <div class="card-benefit-desc">${esc(s.benefitDescription||"")}</div>
      </div>
      <div class="modal-section"><div class="modal-section-title">Ministry</div><p>${esc(s.ministryDepartment||"—")}</p></div>
      <div class="modal-section"><div class="modal-section-title">How to Apply</div><p>${esc(s.howToApply||"—")}</p></div>
      ${docs?`<div class="modal-section"><div class="modal-section-title">Documents Required</div><ul class="docs-list">${docs}</ul></div>`:""}
      <div class="modal-section"><div class="modal-section-title">Website</div><p>${s.officialWebsite?`<a href="${esc(s.officialWebsite)}" target="_blank">${esc(s.officialWebsite)}</a>`:"—"}</p></div>
      <div class="modal-section"><div class="modal-section-title">Helpline</div><p>${esc(s.helplineNumber||"—")}</p></div>
      <hr style="border:none;border-top:1px solid var(--gray-100);margin:16px 0">
      <div class="modal-section-title">Citizen Feedback</div>
      <div id="modalFeedbackList"><div class="spinner"></div></div>`;
    modal.classList.add("show");
    await loadFeedbackList(id, "modalFeedbackList");
}

loadSchemes();
