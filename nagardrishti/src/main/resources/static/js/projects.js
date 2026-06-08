if (!Session.requireLogin()) throw new Error("Not logged in");
initNavbar();

const user = Session.get();
if (!user || !user.userId) { Session.clear(); location.href = "/index.html"; }

// ============================================================
// STATE
// ============================================================
var myAreaProjects  = [];
var browseProjects  = [];
var myAreaFilter    = "ALL";
var browseFilter    = "ALL";
var activeTab       = "myarea";

// ============================================================
// TAB SWITCHING
// ============================================================
document.querySelectorAll(".scheme-tab").forEach(function(tab) {
    tab.addEventListener("click", function() {
        document.querySelectorAll(".scheme-tab").forEach(function(t) { t.classList.remove("active"); });
        tab.classList.add("active");
        activeTab = tab.dataset.tab;
        document.getElementById("myAreaSection").style.display  = activeTab === "myarea" ? "block" : "none";
        document.getElementById("browseSection").style.display  = activeTab === "browse" ? "block" : "none";
    });
});

// ============================================================
// FILTER CHIPS
// ============================================================
document.querySelectorAll(".filter-chip").forEach(function(c) {
    c.addEventListener("click", function() {
        var tab = c.dataset.tab;
        document.querySelectorAll(".filter-chip[data-tab='" + tab + "']").forEach(function(x) {
            x.classList.remove("active");
        });
        c.classList.add("active");
        if (tab === "myarea") { myAreaFilter = c.dataset.value; renderMyArea(); }
        else                  { browseFilter = c.dataset.value; renderBrowse(); }
    });
});

// ============================================================
// MY AREA — AUTO-LOAD
// ============================================================
async function loadMyArea() {
    var container = document.getElementById("myAreaContainer");
    container.innerHTML = '<div class="loading"><div class="spinner"></div><div>Loading your area projects…</div></div>';

    if (!user.pincode && !user.zone) {
        container.innerHTML =
            '<div class="empty-state"><div class="empty-icon">📍</div>' +
            '<div class="empty-title">No area info in your profile</div>' +
            '<p>Please update your profile with a pincode or zone to see nearby projects.</p></div>';
        return;
    }

    try {
        var data = await apiGet("/projects/myarea/" + user.userId);
        myAreaProjects = data.projects || [];
        var stats      = data.stats    || {};
        var area       = data.zone || data.pincode || data.district || "your area";

        renderMyAreaStats(stats, area);
        renderCharts(stats, myAreaProjects);
        myAreaFilter = "ALL";
        document.querySelectorAll(".filter-chip[data-tab='myarea']").forEach(function(c) {
            c.classList.toggle("active", c.dataset.value === "ALL");
        });
        document.getElementById("myAreaFilterBar").style.display =
            myAreaProjects.length > 0 ? "flex" : "none";
        renderMyArea();
    } catch(err) {
        container.innerHTML =
            '<div class="empty-state"><div class="empty-icon">⚠️</div>' +
            '<div class="empty-title">Could not load projects</div>' +
            '<p style="color:var(--red)">' + esc(err.message) + '</p>' +
            '<button onclick="loadMyArea()" class="btn btn-sky" style="margin-top:16px;width:auto;padding:10px 24px">Retry</button></div>';
    }
}

function renderMyAreaStats(s, area) {
    document.getElementById("maStatTotal").textContent     = s.totalProjects     || 0;
    document.getElementById("maStatBudget").textContent    = formatINR(s.totalBudgetAllocated || 0);
    document.getElementById("maStatAwarded").textContent   = "Awarded: " + formatINR(s.totalBudgetSpent || 0);
    document.getElementById("maStatCompleted").textContent = s.completedProjects  || 0;
    document.getElementById("maStatProgress").textContent  = s.inProgressProjects || 0;
    document.getElementById("maStatDelayed").textContent   = s.delayedProjects    || 0;
    document.getElementById("maStatArea").textContent      = "In " + area;
    document.getElementById("myAreaStats").style.display   = (s.totalProjects || 0) > 0 ? "grid" : "none";
}

function renderMyArea() {
    var filtered = myAreaFilter === "ALL" ? myAreaProjects
        : myAreaProjects.filter(function(p) {
            return (p.category     && p.category.toLowerCase()     === myAreaFilter.toLowerCase()) ||
                   (p.workCategory && p.workCategory.toLowerCase() === myAreaFilter.toLowerCase());
          });
    renderProjectCards(filtered, "myAreaContainer", myAreaProjects.length);
}

// ============================================================
// BROWSE — DUAL SEARCH
// ============================================================
document.getElementById("btnBrowse").addEventListener("click", doBrowseSearch);
document.getElementById("browsePin").addEventListener("keydown",  function(e) { if (e.key === "Enter") doBrowseSearch(); });
document.getElementById("browseZone").addEventListener("keydown", function(e) { if (e.key === "Enter") doBrowseSearch(); });

async function doBrowseSearch() {
    var pin  = document.getElementById("browsePin").value.trim();
    var zone = document.getElementById("browseZone").value.trim();

    if (!pin && !zone) {
        document.getElementById("browseContainer").innerHTML =
            '<div class="empty-state"><div class="empty-icon">🔍</div>' +
            '<div class="empty-title">Enter a pincode or zone</div></div>';
        return;
    }

    document.getElementById("browseContainer").innerHTML =
        '<div class="loading"><div class="spinner"></div><div>Searching projects…</div></div>';
    document.getElementById("browseStats").style.display    = "none";
    document.getElementById("browseFilterBar").style.display = "none";

    try {
        var params = [];
        if (pin)  params.push("pincode=" + encodeURIComponent(pin));
        if (zone) params.push("zone="    + encodeURIComponent(zone));

        var [projects, stats] = await Promise.all([
            apiGet("/projects/locality?" + params.join("&")),
            apiGet("/projects/stats?"    + params.join("&"))
        ]);

        browseProjects = projects || [];
        renderBrowseStats(stats, pin || zone);
        browseFilter = "ALL";
        document.querySelectorAll(".filter-chip[data-tab='browse']").forEach(function(c) {
            c.classList.toggle("active", c.dataset.value === "ALL");
        });
        document.getElementById("browseFilterBar").style.display =
            browseProjects.length > 0 ? "flex" : "none";
        renderBrowse();
    } catch(err) {
        document.getElementById("browseContainer").innerHTML =
            '<div class="empty-state"><div class="empty-icon">⚠️</div>' +
            '<div class="empty-title">Search failed</div>' +
            '<p style="color:var(--red)">' + esc(err.message) + '</p></div>';
    }
}

function renderBrowseStats(s, area) {
    document.getElementById("brStatTotal").textContent     = s.totalProjects     || 0;
    document.getElementById("brStatBudget").textContent    = formatINR(s.totalBudgetAllocated || 0);
    document.getElementById("brStatAwarded").textContent   = "Awarded: " + formatINR(s.totalBudgetSpent || 0);
    document.getElementById("brStatCompleted").textContent = s.completedProjects  || 0;
    document.getElementById("brStatProgress").textContent  = s.inProgressProjects || 0;
    document.getElementById("brStatDelayed").textContent   = s.delayedProjects    || 0;
    document.getElementById("brStatArea").textContent      = "For " + area;
    document.getElementById("browseStats").style.display   = (s.totalProjects || 0) > 0 ? "grid" : "none";
}

function renderBrowse() {
    var filtered = browseFilter === "ALL" ? browseProjects
        : browseProjects.filter(function(p) {
            return (p.category     && p.category.toLowerCase()     === browseFilter.toLowerCase()) ||
                   (p.workCategory && p.workCategory.toLowerCase() === browseFilter.toLowerCase());
          });
    renderProjectCards(filtered, "browseContainer", browseProjects.length);
}

// ============================================================
// CHARTS
// ============================================================
function renderCharts(stats, projects) {
    var hasData = (stats.totalProjects || 0) > 0;
    document.getElementById("chartsRow").style.display = hasData ? "block" : "none";
    if (!hasData) return;

    drawStatusDonut(stats);
    drawBudgetChart(stats);
}

function drawStatusDonut(s) {
    var canvas = document.getElementById("statusDonut");
    if (!canvas) return;
    var ctx    = canvas.getContext("2d");
    var cx = 60, cy = 60, r = 50, inner = 30;

    var segments = [
        { label: "Completed",   count: s.completedProjects  || 0, color: "#10B981" },
        { label: "In Progress", count: s.inProgressProjects || 0, color: "#4DA8DA" },
        { label: "Delayed",     count: s.delayedProjects    || 0, color: "#EF4444" },
        { label: "Planned",     count: s.plannedProjects    || 0, color: "#9CA3AF" }
    ].filter(function(seg) { return seg.count > 0; });

    var total = segments.reduce(function(a, b) { return a + b.count; }, 0);
    if (total === 0) return;

    var angle = -Math.PI / 2;
    ctx.clearRect(0, 0, 120, 120);

    segments.forEach(function(seg) {
        var slice = (seg.count / total) * Math.PI * 2;
        ctx.beginPath();
        ctx.moveTo(cx, cy);
        ctx.arc(cx, cy, r, angle, angle + slice);
        ctx.closePath();
        ctx.fillStyle = seg.color;
        ctx.fill();
        angle += slice;
    });

    // Inner white circle
    ctx.beginPath();
    ctx.arc(cx, cy, inner, 0, Math.PI * 2);
    ctx.fillStyle = "#fff";
    ctx.fill();

    // Center text
    ctx.fillStyle = "#1F3A5F";
    ctx.font = "bold 14px sans-serif";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    ctx.fillText(total, cx, cy);

    // Legend
    var legend = document.getElementById("statusLegend");
    legend.innerHTML = segments.map(function(seg) {
        return '<div style="display:flex;align-items:center;gap:6px;margin-bottom:6px">' +
            '<div style="width:10px;height:10px;border-radius:2px;background:' + seg.color + ';flex-shrink:0"></div>' +
            '<span style="color:var(--gray-700)">' + seg.label + '</span>' +
            '<span style="margin-left:auto;font-weight:700;color:var(--royal)">' + seg.count + '</span>' +
            '</div>';
    }).join("");
}

function drawBudgetChart(s) {
    var container = document.getElementById("budgetChart");
    if (!container) return;
    var alloc   = s.totalBudgetAllocated || 0;
    var awarded = s.totalBudgetSpent     || 0;  // using totalBudgetSpent as awarded
    var maxVal  = Math.max(alloc, awarded, 1);

    var allocPct  = Math.round((alloc   / maxVal) * 100);
    var awardPct  = Math.round((awarded / maxVal) * 100);
    var overBudget = awarded > alloc;

    container.innerHTML =
        '<div style="margin-bottom:12px">' +
            '<div style="display:flex;justify-content:space-between;font-size:.78rem;margin-bottom:4px">' +
                '<span style="color:var(--gray-500)">Estimated</span>' +
                '<strong style="color:var(--royal)">' + formatINR(alloc) + '</strong>' +
            '</div>' +
            '<div style="background:var(--gray-100);border-radius:999px;height:10px;overflow:hidden">' +
                '<div style="width:' + allocPct + '%;height:100%;background:var(--sky);border-radius:999px"></div>' +
            '</div>' +
        '</div>' +
        '<div>' +
            '<div style="display:flex;justify-content:space-between;font-size:.78rem;margin-bottom:4px">' +
                '<span style="color:var(--gray-500)">Awarded</span>' +
                '<strong style="color:' + (overBudget ? "var(--red)" : "var(--green)") + '">' +
                    formatINR(awarded) +
                    (overBudget ? ' <span style="font-size:.7rem">(over)</span>' : '') +
                '</strong>' +
            '</div>' +
            '<div style="background:var(--gray-100);border-radius:999px;height:10px;overflow:hidden">' +
                '<div style="width:' + awardPct + '%;height:100%;background:' +
                    (overBudget ? "var(--red)" : "var(--green)") +
                    ';border-radius:999px"></div>' +
            '</div>' +
        '</div>' +
        '<div style="margin-top:14px;font-size:.75rem;color:var(--gray-500);text-align:center">' +
            (overBudget
                ? '<span style="color:var(--red);font-weight:600">⚠ ' +
                  formatINR(awarded - alloc) + ' over estimate</span>'
                : '<span style="color:var(--green);font-weight:600">✓ Within budget</span>') +
        '</div>';
}

// ============================================================
// PROJECT CARD RENDERING
// ============================================================
var STATUS_BADGE = {
    COMPLETED:   "badge-green",
    IN_PROGRESS: "badge-blue",
    DELAYED:     "badge-red",
    PLANNED:     "badge-gray"
};

function renderProjectCards(list, containerId, totalCount) {
    var container = document.getElementById(containerId);
    if (!list.length) {
        container.innerHTML =
            '<div class="empty-state"><div class="empty-icon">📭</div>' +
            '<div class="empty-title">No projects found</div>' +
            '<p>' + (totalCount > 0 ? "Try another category." : "No projects recorded for this area.") + '</p></div>';
        return;
    }

    container.innerHTML =
        '<div style="font-size:.85rem;color:var(--gray-500);margin-bottom:14px">' +
        'Showing <strong>' + list.length + '</strong>' +
        (totalCount !== list.length ? ' of <strong>' + totalCount + '</strong>' : '') +
        ' projects</div>' +
        '<div class="card-grid">' + list.map(projectCard).join("") + '</div>';

    list.forEach(function(p) { loadFeedbackSummary(p.id, "fb-" + p.id); });
}

function projectCard(p) {
    var alloc      = p.budgetAllocated || 0;
    var awarded    = p.awardedValue    || 0;
    var pct        = p.completionPercentage || 0;
    var overBudget = awarded > 0 && alloc > 0 && awarded > alloc;
    var diff       = awarded > 0 && alloc > 0 ? Math.abs(awarded - alloc) : 0;

    var STATUS_COLOR = {
        COMPLETED:   { bg: "#D1FAE5", color: "#065F46", dot: "#10B981" },
        IN_PROGRESS: { bg: "#DBEAFE", color: "#1E40AF", dot: "#4DA8DA" },
        DELAYED:     { bg: "#FEE2E2", color: "#991B1B", dot: "#EF4444" },
        PLANNED:     { bg: "#F3F4F6", color: "#374151", dot: "#9CA3AF" }
    };
    var sc = STATUS_COLOR[p.status] || STATUS_COLOR["PLANNED"];

    // Progress bar color
    var barColor = p.status === "COMPLETED" ? "#10B981"
                 : p.status === "DELAYED"   ? "#EF4444"
                 : "#4DA8DA";

    // Location pills
    var locParts = [];
    if (p.zone)    locParts.push('<span style="background:#EAF5FC;color:#1F3A5F;font-size:.7rem;padding:2px 7px;border-radius:999px">' + esc(p.zone) + '</span>');
    if (p.pincode) locParts.push('<span style="background:#F3F4F6;color:#374151;font-size:.7rem;padding:2px 7px;border-radius:999px">PIN ' + esc(p.pincode) + '</span>');
    if (p.ward)    locParts.push('<span style="background:#F3F4F6;color:#374151;font-size:.7rem;padding:2px 7px;border-radius:999px">Ward ' + esc(p.ward) + '</span>');
    var locLine = locParts.length ? '<div style="display:flex;flex-wrap:wrap;gap:5px;margin-bottom:10px">' + locParts.join("") + '</div>' : "";

    // Department + category row
    var topBadges = "";
    if (p.departmentCode) topBadges += '<span class="badge badge-blue" style="font-size:.68rem">' + esc(p.departmentCode) + '</span> ';
    if (p.workCategory || p.category) topBadges += '<span class="badge badge-gray" style="font-size:.68rem">' + esc(p.workCategory || p.category) + '</span>';

    // Tender number short
    var tenderShort = p.tenderNumber ? p.tenderNumber.split("/").pop() : null;

    // Budget section
    var budgetHtml = "";
    if (alloc > 0 || awarded > 0) {
        budgetHtml =
            '<div style="background:var(--gray-50);border:1px solid var(--gray-100);border-radius:8px;padding:12px;margin-bottom:12px">' +
                '<div style="display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-bottom:8px">' +
                    '<div>' +
                        '<div style="font-size:.68rem;text-transform:uppercase;letter-spacing:.05em;color:var(--gray-500);margin-bottom:2px">Estimated</div>' +
                        '<div style="font-size:.95rem;font-weight:800;color:var(--royal)">' + (alloc > 0 ? formatINR(alloc) : "—") + '</div>' +
                    '</div>' +
                    (awarded > 0 ?
                    '<div>' +
                        '<div style="font-size:.68rem;text-transform:uppercase;letter-spacing:.05em;color:var(--gray-500);margin-bottom:2px">Awarded</div>' +
                        '<div style="font-size:.95rem;font-weight:800;color:' + (overBudget ? "var(--red)" : "#065F46") + '">' +
                            formatINR(awarded) +
                        '</div>' +
                    '</div>' : "") +
                '</div>' +
                (diff > 0 ?
                    '<div style="font-size:.72rem;color:' + (overBudget ? "var(--red)" : "#065F46") + ';font-weight:600">' +
                        (overBudget ? '▲ +' + formatINR(diff) + ' over estimate' : '▼ ' + formatINR(diff) + ' under estimate') +
                    '</div>'
                : "") +
            '</div>';
    }

    // Progress section
    var progressHtml = "";
    if (pct > 0) {
        progressHtml =
            '<div style="margin-bottom:12px">' +
                '<div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:5px">' +
                    '<span style="font-size:.75rem;color:var(--gray-500)">Completion progress</span>' +
                    '<span style="font-size:.82rem;font-weight:800;color:var(--royal)">' + pct + '%</span>' +
                '</div>' +
                '<div style="background:var(--gray-100);border-radius:999px;height:8px;overflow:hidden">' +
                    '<div style="width:' + Math.min(pct,100) + '%;height:100%;border-radius:999px;background:' + barColor + ';transition:width .4s ease"></div>' +
                '</div>' +
            '</div>';
    }

    // Dates row
    var datesHtml = "";
    var dates = [];
    if (p.publishedDate)        dates.push({ label: "Published",   val: p.publishedDate });
    if (p.lastBidDate)          dates.push({ label: "Bid closes",  val: p.lastBidDate });
    if (p.plannedCompletionDate || p.expectedCompletionDate)
        dates.push({ label: "Due", val: p.plannedCompletionDate || p.expectedCompletionDate });
    if (dates.length) {
        datesHtml = '<div style="display:flex;flex-wrap:wrap;gap:12px;margin-bottom:12px;padding:10px;background:var(--gray-50);border-radius:8px">' +
            dates.map(function(d) {
                return '<div><div style="font-size:.65rem;text-transform:uppercase;letter-spacing:.05em;color:var(--gray-500)">' + d.label + '</div>' +
                       '<div style="font-size:.78rem;font-weight:600;color:var(--royal)">' + esc(d.val) + '</div></div>';
            }).join("") +
        '</div>';
    }

    return '<div class="card" style="padding:0;overflow:hidden">' +

        // Header band with status color
        '<div style="background:' + sc.bg + ';padding:14px 18px 12px;border-bottom:1px solid rgba(0,0,0,.05)">' +
            '<div style="display:flex;justify-content:space-between;align-items:flex-start;gap:10px;margin-bottom:8px">' +
                '<div style="flex:1;min-width:0">' +
                    '<div style="font-size:.88rem;font-weight:700;color:var(--royal);line-height:1.4;margin-bottom:4px">' +
                        esc(p.name) + '</div>' +
                    '<div style="font-size:.72rem;color:var(--gray-500)">' +
                        esc(p.sanctioningAuthority || "") +
                        (tenderShort ? ' · <span style="font-family:monospace">' + esc(tenderShort) + '</span>' : "") +
                    '</div>' +
                '</div>' +
                '<div style="flex-shrink:0;display:flex;align-items:center;gap:5px;background:' + sc.bg + ';border:1.5px solid ' + sc.dot + ';border-radius:999px;padding:3px 10px">' +
                    '<div style="width:7px;height:7px;border-radius:50%;background:' + sc.dot + '"></div>' +
                    '<span style="font-size:.72rem;font-weight:700;color:' + sc.color + '">' + esc(prettyStatus(p.status || "PLANNED")) + '</span>' +
                '</div>' +
            '</div>' +
            '<div style="display:flex;flex-wrap:wrap;gap:4px">' + topBadges + '</div>' +
        '</div>' +

        // Body
        '<div style="padding:16px 18px">' +
            locLine +
            (p.description
                ? '<div style="font-size:.83rem;color:var(--gray-700);line-height:1.55;margin-bottom:12px">' +
                  esc(p.description.substring(0, 160)) + (p.description.length > 160 ? "…" : "") + '</div>'
                : "") +
            budgetHtml +
            progressHtml +
            datesHtml +

            // Feedback
            '<div class="feedback-section" style="padding-top:12px">' +
                '<div class="feedback-summary" id="fb-' + esc(p.id) + '">' +
                    '<span class="feedback-pill">Loading…</span>' +
                '</div>' +
                '<button class="btn-feedback" onclick="goToFeedback(\'' +
                    esc(p.id) + '\',\'' + esc(p.name) + '\',\'PROJECT_COMPLAINT\')">' +
                    '⚠ Report issue' +
                '</button>' +
            '</div>' +

            // Footer
            '<div style="display:flex;justify-content:space-between;align-items:center;padding-top:12px;margin-top:4px;border-top:1px solid var(--gray-100)">' +
                (p.location ? '<span style="font-size:.72rem;color:var(--gray-500)">📍 ' + esc(p.location) + '</span>' : '<span></span>') +
                (p.sourceLink
                    ? '<a href="' + esc(p.sourceLink) + '" target="_blank" rel="noopener" ' +
                      'style="font-size:.78rem;color:var(--sky);font-weight:600;padding:5px 10px;border:1px solid var(--sky-light);border-radius:6px;background:var(--sky-bg)">' +
                      'View Source →</a>'
                    : "") +
            '</div>' +
        '</div>' +
    '</div>';
}


// ============================================================
// NOTIFICATION BELL
// ============================================================
var SCHEME_KEY  = "nd_notifications_"      + (user.userId || "");
var PROJECT_KEY = "nd_proj_notifications_" + (user.userId || "");

function initNotifBell() {
    var bell   = document.getElementById("notifBell");
    var badge  = document.getElementById("notifBadge");
    var list   = document.getElementById("notifList");
    var clear  = document.getElementById("notifClear");
    var drop   = document.getElementById("notifDropdown");
    var wrap   = document.getElementById("notifWrapper");

    // Load stored notifications
    var schemes  = loadStored(SCHEME_KEY);
    var projects = loadStored(PROJECT_KEY);

    // Merge new project notifications from login response
    var newProjCount = user.newProjectsCount || 0;
    var newProjNames = user.newProjectNames  || [];
    if (newProjCount > 0) {
        var storedCount = projects ? projects.count : 0;
        var storedNames = projects ? projects.names : [];
        var merged = storedNames.slice();
        newProjNames.forEach(function(n) { if (merged.indexOf(n) === -1) merged.push(n); });
        projects = { count: storedCount + newProjCount, names: merged };
        saveStored(PROJECT_KEY, projects);
    }

    var totalCount = (schemes  ? schemes.count  : 0) +
                     (projects ? projects.count : 0);

    if (totalCount > 0) {
        badge.textContent   = totalCount > 9 ? "9+" : totalCount;
        badge.style.display = "flex";
        bell.classList.add("notif-bell-active");
        list.innerHTML = "";

        // Scheme notifications
        if (schemes && schemes.count > 0) {
            schemes.names.slice(0, 3).forEach(function(name) {
                list.appendChild(makeNotifItem("🆕", name, "New eligible scheme", function() {
                    location.href = "/schemes.html";
                }));
            });
        }

        // Project notifications
        if (projects && projects.count > 0) {
            projects.names.slice(0, 3).forEach(function(name) {
                list.appendChild(makeNotifItem("🏗️", name, "New project in your area", function() {
                    drop.classList.remove("show");
                    document.querySelector("[data-tab='myarea']").click();
                }));
            });
        }
    }

    bell.addEventListener("click", function(e) {
        e.stopPropagation();
        drop.classList.toggle("show");
    });

    document.addEventListener("click", function(e) {
        if (!wrap.contains(e.target)) drop.classList.remove("show");
    });

    clear.addEventListener("click", function() {
        localStorage.removeItem(SCHEME_KEY);
        localStorage.removeItem(PROJECT_KEY);
        badge.style.display = "none";
        bell.classList.remove("notif-bell-active");
        list.innerHTML = '<div class="notif-empty">No new notifications</div>';
        drop.classList.remove("show");
        var u = Session.get();
        if (u) {
            u.newSchemesCount = 0; u.newSchemeNames  = [];
            u.newProjectsCount = 0; u.newProjectNames = [];
            Session.set(u);
        }
    });
}

function makeNotifItem(icon, title, sub, onClick) {
    var item = document.createElement("div");
    item.className = "notif-item";
    item.innerHTML =
        '<div class="notif-item-icon">' + icon + '</div>' +
        '<div class="notif-item-body">' +
            '<div class="notif-item-title">' + esc(title) + '</div>' +
            '<div class="notif-item-sub">'   + sub         + '</div>' +
        '</div>';
    item.addEventListener("click", onClick);
    return item;
}

function loadStored(key) {
    try { var r = localStorage.getItem(key); return r ? JSON.parse(r) : null; }
    catch(e) { return null; }
}

function saveStored(key, data) {
    try { localStorage.setItem(key, JSON.stringify(data)); } catch(e) {}
}

// ============================================================
// INIT
// ============================================================
initNotifBell();
loadMyArea();