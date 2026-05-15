if (!Session.requireLogin()) throw new Error("Not logged in");
initNavbar();

const user = Session.get();
if (user?.role !== "ADMIN") {
    document.getElementById("adminContent").innerHTML =
        `<div class="empty-state"><div class="empty-icon">🔒</div><div class="empty-title">Admin access only</div><p>You don't have permission to view this page.</p></div>`;
}

let allFeedback = [];
let activeTab   = "ALL";

async function loadAll() {
    try {
        allFeedback = await apiGet("/feedback/admin/all");
        updateStats();
        renderTable(allFeedback);
    } catch(e) { showAlert(e.message, "error"); }
}

function updateStats() {
    document.getElementById("statTotal").textContent    = allFeedback.length;
    document.getElementById("statPending").textContent  = allFeedback.filter(f=>f.status==="PENDING").length;
    document.getElementById("statVerified").textContent = allFeedback.filter(f=>f.status==="VERIFIED").length;
    document.getElementById("statComplaints").textContent    = allFeedback.filter(f=>f.type==="PROJECT_COMPLAINT").length;
    document.getElementById("statVerifications").textContent = allFeedback.filter(f=>f.type==="SCHEME_VERIFICATION").length;
}

document.querySelectorAll(".admin-tab").forEach(t => t.addEventListener("click", () => {
    document.querySelectorAll(".admin-tab").forEach(x => x.classList.remove("active"));
    t.classList.add("active"); activeTab = t.dataset.tab;
    const filtered = activeTab === "ALL" ? allFeedback
        : activeTab === "PENDING" || activeTab === "VERIFIED" || activeTab === "REJECTED"
            ? allFeedback.filter(f => f.status === activeTab)
            : allFeedback.filter(f => f.type === activeTab);
    renderTable(filtered);
}));

function renderTable(list) {
    const tbody = document.getElementById("feedbackBody");
    if (!list.length) { tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;color:var(--gray-500);padding:32px">No feedback found.</td></tr>`; return; }
    tbody.innerHTML = list.map(f => `
        <tr>
          <td>
            <div style="font-weight:600;font-size:.85rem">${esc(f.userName)}</div>
            <div style="color:var(--gray-500);font-size:.78rem">${timeAgo(f.createdAt)}</div>
          </td>
          <td><span class="badge ${f.type==="PROJECT_COMPLAINT"?"badge-amber":"badge-blue"}" style="font-size:.72rem">${esc(prettyStatus(f.type))}</span></td>
          <td style="font-size:.85rem">${esc(f.relatedName)}</td>
          <td style="font-size:.85rem;max-width:240px">${esc(f.description)}</td>
          <td><span class="badge ${f.status==="VERIFIED"?"badge-green":f.status==="REJECTED"?"badge-red":"badge-gray"}">${esc(f.status)}</span></td>
          <td style="font-size:.82rem">${f.upvoteCount||0} 👍</td>
          <td>
            ${f.status==="PENDING" ? `
              <button class="admin-action action-verify" onclick="updateStatus('${esc(f.id)}','VERIFIED')">Verify</button>
              <button class="admin-action action-reject" onclick="updateStatus('${esc(f.id)}','REJECTED')">Reject</button>
            ` : `<span style="color:var(--gray-500);font-size:.82rem">Done</span>`}
          </td>
        </tr>`).join("");
}

async function updateStatus(id, status) {
    if (!confirm(`Mark this feedback as ${status}?`)) return;
    try {
        await apiPut(`/feedback/${id}/status`, { status, adminUserId: user.userId });
        showAlert(`Marked as ${status}.`, "success");
        await loadAll();
    } catch(e) { showAlert(e.message, "error"); }
}

loadAll();
