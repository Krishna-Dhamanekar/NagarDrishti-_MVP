const API = "/api";

const Session = {
    key: "nd_user",
    set(u) { localStorage.setItem(this.key, JSON.stringify(u)); },
    get() { try { return JSON.parse(localStorage.getItem(this.key)); } catch { return null; } },
    clear() { localStorage.removeItem(this.key); },
    isLoggedIn() { return !!this.get(); },
    requireLogin() { if (!this.isLoggedIn()) { location.href="/index.html"; return false; } return true; }
};

async function apiGet(path) {
    const r = await fetch(API + path);
    const d = await r.json().catch(() => ({}));
    if (!r.ok) throw new Error(d.error || "Request failed");
    return d;
}
async function apiPost(path, body) {
    const r = await fetch(API + path, { method:"POST", headers:{"Content-Type":"application/json"}, body:JSON.stringify(body) });
    const d = await r.json().catch(() => ({}));
    if (!r.ok) throw new Error(d.error || Object.values(d).filter(v=>typeof v==="string").join(", ") || "Request failed");
    return d;
}
async function apiPut(path, body) {
    const r = await fetch(API + path, { method:"PUT", headers:{"Content-Type":"application/json"}, body:JSON.stringify(body) });
    const d = await r.json().catch(() => ({}));
    if (!r.ok) throw new Error(d.error || "Request failed");
    return d;
}

function formatINR(n) {
    if (!n) return "₹0";
    if (n >= 1e7) return "₹" + (n/1e7).toFixed(2) + " Cr";
    if (n >= 1e5) return "₹" + (n/1e5).toFixed(2) + " L";
    if (n >= 1e3) return "₹" + (n/1e3).toFixed(1) + "K";
    return "₹" + Number(n).toLocaleString("en-IN");
}
function formatDate(d) {
    if (!d) return "—";
    try { return new Date(d).toLocaleDateString("en-IN",{day:"numeric",month:"short",year:"numeric"}); }
    catch { return d; }
}
function timeAgo(d) {
    if (!d) return "";
    const diff = Date.now() - new Date(d).getTime();
    const mins = Math.floor(diff/60000);
    if (mins < 1) return "just now";
    if (mins < 60) return mins + " min ago";
    const hrs = Math.floor(mins/60);
    if (hrs < 24) return hrs + "h ago";
    const days = Math.floor(hrs/24);
    return days + "d ago";
}
function prettyStatus(s) {
    return (s || "").replace(/_/g," ").split(" ").map(w=>w.charAt(0).toUpperCase()+w.slice(1).toLowerCase()).join(" ");
}
function esc(s) {
    return String(s||"").replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;");
}

function initNavbar() {
    const u = Session.get();
    const el = document.getElementById("navUserName");
    if (el && u) el.textContent = u.name || "User";
    const lb = document.getElementById("btnLogout");
    if (lb) lb.onclick = () => { Session.clear(); location.href="/index.html"; };
}

function showAlert(msg, type="error", id="alertBox") {
    const b = document.getElementById(id);
    if (!b) { alert(msg); return; }
    b.textContent = msg;
    b.className = `alert alert-${type} show`;
    if (type==="success") setTimeout(() => b.classList.remove("show"), 4000);
}
