if (!Session.requireLogin()) throw new Error("Not logged in");
initNavbar();

const user = Session.get();
if (!user || !user.userId) { Session.clear(); location.href = "/index.html"; }

// ============================================================
// NOTIFICATION BELL
// ============================================================
function initNotifications() {
    var count     = user.newSchemesCount || 0;
    var names     = user.newSchemeNames  || [];
    var bell      = document.getElementById("notifBell");
    var badge     = document.getElementById("notifBadge");
    var dropdown  = document.getElementById("notifDropdown");
    var list      = document.getElementById("notifList");
    var clearBtn  = document.getElementById("notifClear");
    var wrapper   = document.getElementById("notifWrapper");

    // Show badge if there are new schemes
    if (count > 0) {
        badge.textContent = count > 9 ? "9+" : count;
        badge.style.display = "flex";
        bell.classList.add("notif-bell-active");

        // Build notification items
        list.innerHTML = "";

        // Header notification item
        var headerItem = document.createElement("div");
        headerItem.className = "notif-item notif-item-new";
        headerItem.innerHTML =
            '<div class="notif-item-icon">ℹ️</div>' +
            '<div class="notif-item-body">' +
                '<div class="notif-item-title">' + count + ' new scheme' + (count > 1 ? 's' : '') + ' matched your profile</div>' +
                '<div class="notif-item-sub">Added since your last visit</div>' +
            '</div>';
        list.appendChild(headerItem);

        // Individual scheme items (up to 5)
        names.forEach(function(name) {
            var item = document.createElement("div");
            item.className = "notif-item";
            item.innerHTML =
                '<div class="notif-item-icon">🆕</div>' +
                '<div class="notif-item-body">' +
                    '<div class="notif-item-title">' + esc(name) + '</div>' +
                    '<div class="notif-item-sub">New eligible scheme — tap to view</div>' +
                '</div>';
            item.addEventListener("click", function() {
                dropdown.classList.remove("show");
                // Switch to eligible tab so user can see the new scheme
                document.querySelector("[data-tab='eligible']").click();
            });
            list.appendChild(item);
        });

        // If more than 5
        if (count > names.length) {
            var moreItem = document.createElement("div");
            moreItem.className = "notif-item";
            moreItem.innerHTML =
                '<div class="notif-item-icon">🆕</div>' +
                '<div class="notif-item-body">' +
                    '<div class="notif-item-title">+' + (count - names.length) + ' more new schemes</div>' +
                    '<div class="notif-item-sub">View all in My Eligible Schemes tab</div>' +
                '</div>';
            list.appendChild(moreItem);
        }

    } else {
        list.innerHTML = '<div class="notif-empty">No new notifications</div>';
    }

    // Toggle dropdown on bell click
    bell.addEventListener("click", function(e) {
        e.stopPropagation();
        dropdown.classList.toggle("show");
    });

    // Close dropdown when clicking outside
    document.addEventListener("click", function(e) {
        if (!wrapper.contains(e.target)) {
            dropdown.classList.remove("show");
        }
    });

    // Clear all — hides badge and empties list
    clearBtn.addEventListener("click", function() {
        badge.style.display = "none";
        badge.textContent   = "0";
        bell.classList.remove("notif-bell-active");
        list.innerHTML = '<div class="notif-empty">No new notifications</div>';
        dropdown.classList.remove("show");
        // Clear from session so it doesn't come back on page refresh
        var u = Session.get();
        u.newSchemesCount = 0;
        u.newSchemeNames  = [];
        Session.set(u);
    });
}

// ============================================================
// STATE
// ============================================================
var allEligible       = [];
var filteredList      = [];
var activeCategory    = "ALL";
var activeTab         = "eligible";
var searchTimer       = null;
var visibleCount      = 20;
var lastBrowseResults = [];
var lastBrowseQuery   = "";
var PAGE_SIZE         = 20;

// ============================================================
// TAB SWITCHING
// ============================================================
document.querySelectorAll(".scheme-tab").forEach(function(tab) {
    tab.addEventListener("click", function() {
        document.querySelectorAll(".scheme-tab").forEach(function(t) { t.classList.remove("active"); });
        tab.classList.add("active");
        activeTab = tab.dataset.tab;
        document.getElementById("eligibleSection").style.display = activeTab === "eligible" ? "block" : "none";
        document.getElementById("browseSection").style.display   = activeTab === "browse"   ? "block" : "none";
    });
});

// ============================================================
// FILTER CHIPS
// ============================================================
document.querySelectorAll(".filter-chip").forEach(function(c) {
    c.addEventListener("click", function() {
        document.querySelectorAll(".filter-chip").forEach(function(x) { x.classList.remove("active"); });
        c.classList.add("active");
        activeCategory = c.dataset.category;
        visibleCount   = PAGE_SIZE;
        renderEligible();
    });
});

// ============================================================
// SEARCH
// ============================================================
document.getElementById("schemeSearch").oninput = function() {
    clearTimeout(searchTimer);
    var q    = this.value.trim();
    var hint = document.getElementById("searchHint");
    if (q.length === 0) {
        if (hint) hint.style.display = "none";
        lastBrowseResults = [];
        document.getElementById("browseContainer").innerHTML = emptySearchHtml();
        return;
    }
    if (q.length < 3) {
        if (hint) { hint.textContent = "Type at least 3 characters..."; hint.style.display = "block"; }
        return;
    }
    if (hint) hint.style.display = "none";
    searchTimer = setTimeout(function() { runSearch(q); }, 400);
};

function emptySearchHtml() {
    return '<div class="empty-state"><div class="empty-icon">🔍</div>' +
           '<div class="empty-title">Search for schemes</div>' +
           '<p>Type a keyword — e.g. "farmer", "scholarship", "pension", "widow", "housing"</p></div>';
}

// ============================================================
// ELIGIBLE TAB
// ============================================================
async function loadEligible() {
    document.getElementById("schemesContainer").innerHTML =
        '<div class="loading"><div class="spinner"></div><div>Finding schemes for you...</div></div>';
    try {
        var r = await apiGet("/schemes/eligible/" + user.userId);
        if (!r || typeof r !== "object") throw new Error("Invalid response");
        allEligible = Array.isArray(r.schemes) ? r.schemes : [];
        document.getElementById("statCount").textContent    = r.totalEligibleSchemes || 0;
        document.getElementById("statBenefit").textContent  = formatINR(r.totalPotentialBenefit || 0);
        document.getElementById("statLocation").textContent = user.state || "—";
        document.getElementById("statPincode").textContent  = "Pincode: " + (user.pincode || "—");
        visibleCount = PAGE_SIZE;
        renderEligible();
    } catch(err) {
        document.getElementById("schemesContainer").innerHTML =
            '<div class="empty-state"><div class="empty-icon">⚠️</div>' +
            '<div class="empty-title">Could not load schemes</div>' +
            '<p style="color:var(--red);font-size:.85rem">' + esc(err.message) + '</p>' +
            '<button onclick="loadEligible()" class="btn btn-sky" style="margin-top:16px;width:auto;padding:10px 24px">Retry</button></div>';
    }
}

function renderEligible() {
    filteredList = activeCategory === "ALL"
        ? allEligible
        : allEligible.filter(function(s) {
            return Array.isArray(s.schemeCategory) &&
                   s.schemeCategory.some(function(c) { return c === activeCategory; });
          });

    var container = document.getElementById("schemesContainer");
    if (!filteredList.length) {
        container.innerHTML =
            '<div class="empty-state"><div class="empty-icon">🔍</div>' +
            '<div class="empty-title">No matching schemes</div>' +
            '<p>Try a different filter or update your profile.</p></div>';
        return;
    }

    var page    = filteredList.slice(0, visibleCount);
    var hasMore = filteredList.length > visibleCount;

    container.innerHTML =
        '<div style="font-size:.85rem;color:var(--gray-500);margin-bottom:14px;padding:10px 14px;' +
        'background:var(--sky-bg);border-radius:8px;border-left:3px solid var(--sky)">' +
        '<strong>' + filteredList.length + '</strong> schemes matched to your profile — sorted by best match first. ' +
        'Universal schemes are in <a href="#" onclick="document.querySelector(\'[data-tab=browse]\').click();return false" ' +
        'style="color:var(--royal);font-weight:600">Browse All →</a></div>' +
        '<div class="card-grid">' + page.map(schemeCard).join("") + '</div>' +
        (hasMore ?
            '<div style="text-align:center;margin-top:24px">' +
            '<button id="loadMoreEligibleBtn" class="btn btn-outline" style="padding:12px 32px;font-weight:600">' +
            'Load More (' + (filteredList.length - visibleCount) + ' remaining)</button></div>'
            : "");

    attachCardListeners(page);

    var moreBtn = document.getElementById("loadMoreEligibleBtn");
    if (moreBtn) {
        moreBtn.addEventListener("click", function() {
            visibleCount += PAGE_SIZE;
            renderEligible();
            window.scrollBy({ top: 400, behavior: "smooth" });
        });
    }
}

// ============================================================
// BROWSE TAB
// ============================================================
async function runSearch(q) {
    var container = document.getElementById("browseContainer");
    container.innerHTML = '<div class="loading"><div class="spinner"></div><div>Searching...</div></div>';
    try {
        var results       = await apiGet("/schemes/search?q=" + encodeURIComponent(q));
        lastBrowseResults = Array.isArray(results) ? results : [];
        lastBrowseQuery   = q;
        visibleCount      = PAGE_SIZE;
        renderBrowsePage();
    } catch(err) {
        container.innerHTML =
            '<div class="empty-state"><div class="empty-icon">⚠️</div>' +
            '<div class="empty-title">Search failed</div>' +
            '<p style="color:var(--red);font-size:.85rem">' + esc(err.message) + '</p></div>';
    }
}

function renderBrowsePage() {
    var container = document.getElementById("browseContainer");
    var schemes   = lastBrowseResults;
    var query     = lastBrowseQuery;

    if (!schemes.length) {
        container.innerHTML =
            '<div class="empty-state"><div class="empty-icon">🔍</div>' +
            '<div class="empty-title">No results for "' + esc(query) + '"</div>' +
            '<p>Try another keyword.</p></div>';
        return;
    }

    var page    = schemes.slice(0, visibleCount);
    var hasMore = schemes.length > visibleCount;

    container.innerHTML =
        '<div style="font-size:.85rem;color:var(--gray-500);margin-bottom:12px">' +
        '<strong>' + schemes.length + '</strong> results for "<strong>' + esc(query) + '</strong>"' +
        (hasMore ? ' — showing first <strong>' + page.length + '</strong>' : "") + '</div>' +
        '<div class="card-grid">' + page.map(schemeCard).join("") + '</div>' +
        (hasMore ?
            '<div style="text-align:center;margin-top:24px">' +
            '<button id="loadMoreBrowseBtn" class="btn btn-outline" style="padding:12px 32px;font-weight:600">' +
            'Load More (' + (schemes.length - visibleCount) + ' remaining)</button></div>'
            : "");

    attachCardListeners(page);

    var moreBtn = document.getElementById("loadMoreBrowseBtn");
    if (moreBtn) {
        moreBtn.addEventListener("click", function() {
            visibleCount += PAGE_SIZE;
            renderBrowsePage();
            window.scrollBy({ top: 400, behavior: "smooth" });
        });
    }
}

// ============================================================
// CARD RENDERING
// ============================================================
var LEVEL_BADGE = { "Central": "badge-blue", "State": "badge-amber" };
var CAT_COLORS  = {
    "Agriculture": "badge-green",
    "Agriculture,Rural & Environment": "badge-green",
    "Education": "badge-sky",
    "Health": "badge-red",
    "Women": "badge-blue",
    "Housing": "badge-amber",
    "Finance": "badge-green",
    "Social welfare & Empowerment": "badge-gray",
    "Social Welfare": "badge-gray"
};

function cleanBenefit(val) {
    if (!val) return null;
    var v = val.trim().replace(/[,\s]+$/, "").trim();
    if (v.length < 4) return null;
    var hasNumber  = /[\d\u20b9]/.test(v);
    var hasKeyword = /(free|lakh|crore|monthly|annual|once|per|subsidy|pension|cash|loan|scholar|assist|benefit|grant)/i.test(v);
    if (!hasNumber && !hasKeyword && v.length < 15) return null;
    return v.length > 80 ? v.substring(0, 77) + "..." : v;
}

function schemeCard(s) {
    var cats = (s.schemeCategory || []).slice(0, 2).map(function(c) {
        return '<span class="badge ' + (CAT_COLORS[c] || "badge-gray") + '" style="font-size:.68rem">' + esc(c) + '</span>';
    }).join(" ");
    var levelBadge = s.level
        ? '<span class="badge ' + (LEVEL_BADGE[s.level] || "badge-gray") + '" style="font-size:.68rem">' + esc(s.level) + '</span>'
        : "";
    var tags = (s.tags || []).slice(0, 4).map(function(t) {
        return '<span class="tag-pill">' + esc(t) + '</span>';
    }).join("");
    var stateInfo = (s.level === "State" && (s.beneficiaryState || []).length)
        ? '<div style="font-size:.76rem;color:var(--gray-500);margin-bottom:6px">📍 ' +
          s.beneficiaryState.slice(0, 3).map(esc).join(", ") +
          (s.beneficiaryState.length > 3 ? " +more" : "") + '</div>'
        : "";
    var benefit = cleanBenefit(s.benefitAmount) || cleanBenefit(s.benefitType) || "See details";
    var desc    = (s.description || "").substring(0, 150);

    return '<div class="card">' +
        '<div class="card-header">' +
            '<div style="flex:1;min-width:0">' +
                '<div class="card-title">' + esc(s.name) + '</div>' +
                (s.shortTitle ? '<div class="card-sub">' + esc(s.shortTitle) + '</div>' : "") +
            '</div>' +
            '<div style="flex-shrink:0">' + levelBadge + '</div>' +
        '</div>' +
        '<div style="margin-bottom:8px;display:flex;flex-wrap:wrap;gap:4px">' + cats + '</div>' +
        stateInfo +
        '<div class="card-desc">' + esc(desc) + (s.description && s.description.length > 150 ? "..." : "") + '</div>' +
        (tags ? '<div style="margin:6px 0 10px">' + tags + '</div>' : "") +
        '<div class="card-benefit">' +
            '<div class="card-benefit-label">Benefit</div>' +
            '<div class="card-benefit-amount">' + esc(benefit) + '</div>' +
        '</div>' +
        '<div class="feedback-section">' +
            '<div class="feedback-summary" id="fb-' + esc(s.id) + '"><span class="feedback-pill">Loading...</span></div>' +
            '<button class="btn-feedback" onclick="goToFeedback(\'' + esc(s.id) + '\',\'' + esc(s.name) + '\',\'SCHEME_VERIFICATION\')">+ Submit feedback</button>' +
        '</div>' +
        '<div class="card-footer">' +
            '<span class="card-meta">' + esc(s.schemeFor || "") + '</span>' +
            '<button class="btn-view" data-id="' + esc(s.id) + '">View Details →</button>' +
        '</div>' +
    '</div>';
}

function attachCardListeners(schemes) {
    document.querySelectorAll(".btn-view").forEach(function(btn) {
        btn.addEventListener("click", function() { openModal(btn.dataset.id, schemes); });
    });
    schemes.forEach(function(s) { loadFeedbackSummary(s.id, "fb-" + s.id); });
}

// ============================================================
// MODAL
// ============================================================
var modal       = document.getElementById("schemeModal");
var schemeCache = {};

if (modal) {
    document.getElementById("modalClose").onclick = function() { modal.classList.remove("show"); };
    modal.addEventListener("click", function(e) { if (e.target === modal) modal.classList.remove("show"); });
    document.addEventListener("keydown", function(e) { if (e.key === "Escape") modal.classList.remove("show"); });
}

async function openModal(id, sourceList) {
    var s = (sourceList || []).find(function(x) { return x.id === id; })
         || schemeCache[id]
         || allEligible.find(function(x) { return x.id === id; });
    if (!s) {
        try { s = await apiGet("/schemes/" + id); } catch(e) { return; }
    }
    if (!s) return;
    schemeCache[id] = s;

    document.getElementById("modalTitle").textContent     = s.name;
    document.getElementById("modalShortName").textContent = s.shortTitle || "";

    var cats = (s.schemeCategory || []).map(function(c) {
        return '<span class="badge ' + (CAT_COLORS[c] || "badge-gray") + '">' + esc(c) + '</span>';
    }).join(" ");
    var tags = (s.tags || []).map(function(t) { return '<span class="tag-pill">' + esc(t) + '</span>'; }).join("");
    var stateInfo = (s.level === "State" && (s.beneficiaryState || []).length)
        ? '<div class="modal-section"><div class="modal-section-title">Applicable States</div>' +
          '<p>' + s.beneficiaryState.map(esc).join(", ") + '</p></div>'
        : "";

    var el = [];
    if (s.minAge || s.maxAge) el.push("Age: " + (s.minAge || "any") + " to " + (s.maxAge || "any"));
    if (s.gender && s.gender !== "All") el.push("Gender: " + s.gender);
    if (s.maxIncome) el.push("Max annual income: " + formatINR(s.maxIncome));
    if (s.requiresBpl) el.push("BPL card required");
    if (s.requiresDisability) el.push("Disability certificate required");
    if (s.occupation) el.push("Occupation: " + s.occupation);
    if (s.maxLandAllowed) el.push("Max land: " + s.maxLandAllowed + " acres");
    if (s.targetEducationLevel) el.push("Education: " + s.targetEducationLevel);
    if ((s.eligibleCategories || []).length) el.push("Categories: " + s.eligibleCategories.join(", "));

    var elHtml = el.length
        ? '<ul style="margin:0;padding-left:18px">' +
          el.map(function(l) { return '<li style="font-size:.87rem;margin-bottom:4px">' + esc(l) + '</li>'; }).join("") + '</ul>'
        : '<p style="color:var(--gray-500);font-size:.87rem">Open to all eligible citizens.</p>';

    var benefitAmount = cleanBenefit(s.benefitAmount) || "See description below";

    document.getElementById("modalBody").innerHTML =
        '<div style="display:flex;flex-wrap:wrap;gap:4px;margin-bottom:14px">' + cats +
            (s.level ? '<span class="badge ' + (LEVEL_BADGE[s.level] || "badge-gray") + '">' + esc(s.level) + '</span>' : "") +
            (s.schemeFor ? '<span class="badge badge-gray">' + esc(s.schemeFor) + '</span>' : "") +
        '</div>' +
        '<div class="modal-section"><div class="modal-section-title">About</div>' +
            '<p style="font-size:.9rem;line-height:1.65">' + esc(s.description || "") + '</p></div>' +
        '<div class="card-benefit" style="margin-bottom:16px">' +
            '<div class="card-benefit-label">Benefit</div>' +
            '<div class="card-benefit-amount">' + esc(benefitAmount) + '</div>' +
            (s.benefitType ? '<div class="card-benefit-desc" style="white-space:pre-line;margin-top:6px;max-height:160px;overflow-y:auto">' + esc(s.benefitType) + '</div>' : "") +
        '</div>' +
        '<div class="modal-section"><div class="modal-section-title">Eligibility Criteria</div>' + elHtml + '</div>' +
        stateInfo +
        (tags ? '<div class="modal-section"><div class="modal-section-title">Tags</div><div style="margin-top:4px">' + tags + '</div></div>' : "") +
        '<div class="modal-section"><div class="modal-section-title">Official Website</div>' +
            '<p>' + (s.officialWebsite ? '<a href="' + esc(s.officialWebsite) + '" target="_blank" rel="noopener">' + esc(s.officialWebsite) + '</a>' : "—") + '</p></div>' +
        '<div class="modal-section"><div class="modal-section-title">Helpline</div>' +
            '<p>' + (s.helpline ? '<a href="tel:' + esc(s.helpline) + '">' + esc(s.helpline) + '</a>' : "—") + '</p></div>' +
        '<hr style="border:none;border-top:1px solid var(--gray-100);margin:16px 0">' +
        '<div class="modal-section-title">Citizen Feedback</div>' +
        '<div id="modalFeedbackList"><div class="loading"><div class="spinner"></div></div></div>';

    modal.classList.add("show");
    loadFeedbackList(id, "modalFeedbackList");
}

// ============================================================
// INIT
// ============================================================
document.getElementById("browseContainer").innerHTML = emptySearchHtml();
initNotifications();
loadEligible();