/**
 * Shared feedback utilities — loaded on schemes.html and projects.html
 * Renders feedback summary pills + list of feedback cards on each card.
 */

async function loadFeedbackSummary(relatedId, containerId) {
    try {
        const s = await apiGet(`/feedback/summary/${relatedId}`);
        const el = document.getElementById(containerId);
        if (!el) return;
        if (s.totalFeedback === 0) {
            el.innerHTML = `<span class="feedback-pill">No feedback yet — be the first</span>`;
            return;
        }
        const pills = [];
        if (s.projectComplaints > 0)
            pills.push(`<span class="feedback-pill amber">⚠ ${s.projectComplaints} complaint${s.projectComplaints>1?'s':''}</span>`);
        if (s.schemeReceivedCount > 0)
            pills.push(`<span class="feedback-pill green">✓ ${s.schemeReceivedCount} received money</span>`);
        if (s.schemeNotReceivedCount > 0)
            pills.push(`<span class="feedback-pill red">✗ ${s.schemeNotReceivedCount} did NOT receive</span>`);
        if (s.verifiedFeedback > 0)
            pills.push(`<span class="feedback-pill">✓ ${s.verifiedFeedback} verified</span>`);
        el.innerHTML = pills.join("") || `<span class="feedback-pill">${s.totalFeedback} feedback</span>`;
    } catch(e) { /* silent */ }
}

async function loadFeedbackList(relatedId, listContainerId) {
    try {
        const feedbacks = await apiGet(`/feedback/related/${relatedId}`);
        const el = document.getElementById(listContainerId);
        if (!el) return;
        if (!feedbacks.length) {
            el.innerHTML = `<p style="color:var(--gray-500);font-size:.9rem;text-align:center;padding:16px 0">No feedback submitted yet for this item.</p>`;
            return;
        }
        el.innerHTML = `<div class="feedback-list">${feedbacks.map(f => feedbackCard(f)).join("")}</div>`;
        // Attach upvote handlers
        el.querySelectorAll("[data-upvote]").forEach(btn => {
            btn.addEventListener("click", () => upvoteFeedback(btn.dataset.upvote, btn));
        });
    } catch(e) { /* silent */ }
}

function feedbackCard(f) {
    const isComplaint     = f.type === "PROJECT_COMPLAINT";
    const isVerification  = f.type === "SCHEME_VERIFICATION";

    let typeBadge = isComplaint
        ? `<span class="badge badge-amber">Project Complaint</span>`
        : `<span class="badge badge-blue">Scheme Verification</span>`;

    let statusBadge = "";
    if (f.status === "VERIFIED") statusBadge = `<span class="badge badge-green">Verified</span>`;
    if (f.status === "REJECTED") statusBadge = `<span class="badge badge-red">Rejected</span>`;
    if (f.status === "PENDING")  statusBadge = `<span class="badge badge-gray">Pending review</span>`;

    let extraInfo = "";
    if (isComplaint && f.complaintCategory)
        extraInfo = `<span class="badge badge-amber" style="margin-bottom:8px">${esc(prettyStatus(f.complaintCategory))}</span>`;
    if (isVerification) {
        const got = f.schemeReceived;
        extraInfo = got
            ? `<div style="font-size:.85rem;color:#065F46;font-weight:600;margin-bottom:6px">✓ Received money${f.amountReceived ? " — " + formatINR(f.amountReceived) : ""}${f.receivedMonth ? " (" + esc(f.receivedMonth) + ")" : ""}</div>`
            : `<div style="font-size:.85rem;color:#991B1B;font-weight:600;margin-bottom:6px">✗ Did NOT receive scheme money</div>`;
    }

    let photoHtml = "";
    if (f.photoUrl)
        photoHtml = `<a href="${esc(f.photoUrl)}" target="_blank" style="font-size:.82rem;color:var(--sky)">📷 View proof photo</a>`;

    return `
    <div class="feedback-card">
        <div class="feedback-card-header">
            <div>
                ${typeBadge} ${statusBadge}
                <div class="feedback-card-user" style="margin-top:6px">${esc(f.userName)}</div>
            </div>
            <div class="feedback-card-date">${timeAgo(f.createdAt)}</div>
        </div>
        ${extraInfo}
        <div class="feedback-card-desc">${esc(f.description)}</div>
        <div class="feedback-card-footer">
            ${photoHtml}
            <button class="btn-upvote" data-upvote="${esc(f.id)}">
                👍 <span class="upvote-count">${f.upvoteCount || 0}</span>
            </button>
        </div>
    </div>`;
}

async function upvoteFeedback(feedbackId, btn) {
    try {
        const updated = await apiPut(`/feedback/${feedbackId}/upvote`, {});
        btn.querySelector(".upvote-count").textContent = updated.upvoteCount;
    } catch(e) { /* silent */ }
}

// Navigate to feedback submission page
function goToFeedback(relatedId, relatedName, type) {
    const user = Session.get();
    if (!user) { alert("Please login to submit feedback."); location.href="/index.html"; return; }
    const params = new URLSearchParams({ relatedId, relatedName, type });
    location.href = "/feedback.html?" + params.toString();
}
