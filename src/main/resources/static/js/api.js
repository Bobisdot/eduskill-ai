const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "eduskill_token";
const THEME_KEY = "eduskill_theme";

/* ---------- Theme (system preference + manual override) ---------- */
document.documentElement.dataset.theme = localStorage.getItem(THEME_KEY) ||
  (window.matchMedia && matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light");

function updateThemeBtn() {
  const b = document.querySelector(".theme-toggle");
  if (!b) return;
  const dark = document.documentElement.dataset.theme === "dark";
  b.textContent = dark ? "☀ Жарық тақырып" : "☾ Қараңғы тақырып";
  b.setAttribute("aria-pressed", String(dark));
}
function toggleTheme() {
  const next = document.documentElement.dataset.theme === "dark" ? "light" : "dark";
  document.documentElement.dataset.theme = next;
  localStorage.setItem(THEME_KEY, next);
  updateThemeBtn();
}

/* ---------- Token / session ---------- */
function saveToken(t) { localStorage.setItem(TOKEN_KEY, t); }
function getToken() { return localStorage.getItem(TOKEN_KEY); }
function clearToken() { localStorage.removeItem(TOKEN_KEY); }

function parseJwt() {
  const t = getToken();
  if (!t) return null;
  try {
    const b = t.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
    return JSON.parse(decodeURIComponent(escape(atob(b))));
  } catch (e) { return null; }
}
function isLoggedIn() {
  if (!getToken()) return false;
  const p = parseJwt();
  return !(p && p.exp && p.exp * 1000 < Date.now());
}
function logout() { clearToken(); window.location.href = "index.html"; }
function requireAuth() {
  if (!isLoggedIn()) {
    const had = !!getToken();
    clearToken();
    window.location.href = had ? "index.html?expired=1" : "index.html";
  }
}
function currentEmail() { const p = parseJwt(); return (p && p.sub) || ""; }
function currentRole() {
  const p = parseJwt();
  if (!p) return "STUDENT";
  if (p.authorities && p.authorities.length) {
    const a = p.authorities[0];
    return String(typeof a === "string" ? a : (a.authority || "STUDENT")).replace("ROLE_", "");
  }
  return p.role ? String(p.role).replace("ROLE_", "") : "STUDENT";
}

/* ---------- Network ---------- */
async function apiFetch(path, options = {}) {
  const headers = Object.assign({ "Content-Type": "application/json" }, options.headers || {});
  const token = getToken();
  if (token) headers["Authorization"] = "Bearer " + token;

  let res;
  try {
    res = await fetch(API_BASE + path, Object.assign({}, options, { headers }));
  } catch (e) {
    throw new Error("Серверге қосылу мүмкін емес. Интернетті тексеріп, қайталап көріңіз.");
  }

  if ((res.status === 401 || res.status === 403) &&
      path !== "/api/v1/auth/authenticate" && path !== "/api/v1/auth/register") {
    clearToken();
    window.location.href = "index.html?expired=1";
  }

  const ct = res.headers.get("content-type") || "";
  const body = ct.includes("application/json") ? await res.json().catch(() => null) : await res.text();

  if (!res.ok) {
    const msg = (body && body.message) ? body.message
      : (typeof body === "string" && body) ? body
      : "Сұраныс орындалмады (" + res.status + ")";
    throw new Error(msg);
  }
  return body;
}

/* ---------- Safety & small utils ---------- */
function esc(s) {
  return String(s == null ? "" : s).replace(/[&<>"']/g, c =>
    ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}
function localDate(d) {
  return d.getFullYear() + "-" + String(d.getMonth() + 1).padStart(2, "0") + "-" + String(d.getDate()).padStart(2, "0");
}
function skeleton(n = 3, h = 90) {
  return Array.from({ length: n }, () => `<div class="skeleton" style="height:${h}px" aria-hidden="true"></div>`).join("");
}
function countUp(el, to, ms = 700) {
  if (window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches) { el.textContent = to; return; }
  const t0 = performance.now();
  const step = t => {
    const k = Math.min(1, (t - t0) / ms);
    el.textContent = Math.round(to * (1 - Math.pow(1 - k, 3)));
    if (k < 1) requestAnimationFrame(step);
  };
  requestAnimationFrame(step);
}
function setLoading(btn, on, label) {
  if (!btn) return;
  if (on) {
    btn.dataset.html = btn.innerHTML;
    btn.disabled = true;
    btn.setAttribute("aria-busy", "true");
    btn.innerHTML = '<span class="spinner" aria-hidden="true"></span>' + (label ? " " + esc(label) : "");
  } else {
    btn.disabled = false;
    btn.removeAttribute("aria-busy");
    if (btn.dataset.html) btn.innerHTML = btn.dataset.html;
  }
}

/* ---------- Feedback: alerts & toasts ---------- */
function toast(msg, type = "info", ms = 4500) {
  let w = document.querySelector(".toast-wrap");
  if (!w) {
    w = document.createElement("div");
    w.className = "toast-wrap";
    w.setAttribute("role", "status");
    w.setAttribute("aria-live", "polite");
    document.body.appendChild(w);
  }
  const t = document.createElement("div");
  t.className = "toast toast-" + type;
  t.innerHTML = `<span class="toast-ico" aria-hidden="true">${type === "success" ? "✓" : type === "error" ? "!" : "i"}</span><span>${esc(msg)}</span><button type="button" aria-label="Жабу">×</button>`;
  const rm = () => { t.classList.add("out"); setTimeout(() => t.remove(), 200); };
  t.querySelector("button").onclick = rm;
  w.appendChild(t);
  setTimeout(rm, ms);
}
function showError(el, message) {
  el.textContent = message;
  el.className = "alert alert-error";
  el.setAttribute("role", "alert");
  el.style.display = "block";
}
function showSuccess(el, message) {
  if (el) hideAlert(el);
  toast(message, "success");
}
function hideAlert(el) { el.style.display = "none"; }

/* ---------- Dialogs (accessible: focus trap, Esc, focus restore) ---------- */
function showModal(inner, label) {
  const prev = document.activeElement;
  const bd = document.createElement("div");
  bd.className = "dlg-backdrop";
  bd.innerHTML = `<div class="dlg" role="dialog" aria-modal="true" aria-label="${esc(label || "Диалог")}" tabindex="-1">${inner}</div>`;
  document.body.appendChild(bd);
  const dlg = bd.firstElementChild;
  let done;
  const closed = new Promise(r => { done = r; });
  const close = v => {
    document.removeEventListener("keydown", onKey);
    bd.remove();
    if (prev && prev.focus) prev.focus();
    done(v);
  };
  function onKey(e) {
    if (e.key === "Escape") { close(null); return; }
    if (e.key !== "Tab") return;
    const f = [...dlg.querySelectorAll("button,input,textarea,select,a[href]")].filter(x => !x.disabled);
    if (!f.length) return;
    const a = f[0], z = f[f.length - 1];
    if (e.shiftKey && document.activeElement === a) { e.preventDefault(); z.focus(); }
    else if (!e.shiftKey && document.activeElement === z) { e.preventDefault(); a.focus(); }
  }
  document.addEventListener("keydown", onKey);
  bd.addEventListener("mousedown", e => { if (e.target === bd) close(null); });
  dlg.querySelectorAll("[data-close]").forEach(b => b.addEventListener("click", () => close(null)));
  (dlg.querySelector("[autofocus]") || dlg).focus();
  return { el: dlg, close, closed };
}

function confirmDialog({ title, text, ok = "Иә", cancel = "Болдырмау", danger = false }) {
  const m = showModal(`
    <div class="dlg-head"><h3>${esc(title)}</h3></div>
    <p class="muted" style="font-size:.95rem">${esc(text || "")}</p>
    <div class="dlg-actions">
      <button type="button" class="btn btn-ghost" data-close>${esc(cancel)}</button>
      <button type="button" class="btn ${danger ? "btn-danger" : "btn-primary"}" data-ok autofocus>${esc(ok)}</button>
    </div>`, title);
  m.el.querySelector("[data-ok]").onclick = () => m.close(true);
  return m.closed.then(v => !!v);
}

function promptDialog({ title, label, placeholder = "", ok = "Жіберу", max = 500 }) {
  const m = showModal(`
    <div class="dlg-head"><h3>${esc(title)}</h3><button type="button" class="icon-btn" data-close aria-label="Жабу">×</button></div>
    <div class="field">
      <label class="label" for="dlgText">${esc(label)} <span class="counter" id="dlgCount">0/${max}</span></label>
      <textarea class="input" id="dlgText" rows="4" maxlength="${max}" placeholder="${esc(placeholder)}" autofocus></textarea>
    </div>
    <div class="dlg-actions">
      <button type="button" class="btn btn-ghost" data-close>Болдырмау</button>
      <button type="button" class="btn btn-primary" data-ok>${esc(ok)}</button>
    </div>`, title);
  const ta = m.el.querySelector("#dlgText");
  ta.addEventListener("input", () => { m.el.querySelector("#dlgCount").textContent = ta.value.length + "/" + max; });
  m.el.querySelector("[data-ok]").onclick = () => m.close(ta.value.trim());
  return m.closed; // string or null
}

/* ---------- App shell: nav, icons, skip link, theme toggle ---------- */
const NAV_ICONS = {
  "dashboard.html": '<path d="M3 11l9-8 9 8"/><path d="M5 10v10h5v-6h4v6h5V10"/>',
  "profile.html": '<circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 4-6 8-6s8 2 8 6"/>',
  "projects.html": '<rect x="3" y="7" width="18" height="13" rx="2"/><path d="M9 7V5a2 2 0 012-2h2a2 2 0 012 2v2"/>',
  "courses.html": '<path d="M4 5a2 2 0 012-2h13v16H6a2 2 0 00-2 2z"/><path d="M4 19V5"/>',
  "ai.html": '<path d="M12 3l2 6 6 2-6 2-2 6-2-6-6-2 6-2z"/>'
};

function enhanceShell() {
  if (!document.querySelector(".skip-link")) {
    const a = document.createElement("a");
    a.className = "skip-link"; a.href = "#main"; a.textContent = "Негізгі мазмұнға өту";
    document.body.prepend(a);
  }
  const main = document.querySelector(".main");
  if (main) { main.id = "main"; main.setAttribute("tabindex", "-1"); }
  const nav = document.querySelector(".nav");
  if (nav) nav.setAttribute("aria-label", "Негізгі навигация");
  document.querySelectorAll(".nav a").forEach(a => {
    if (a.querySelector(".ico")) return;
    const ic = NAV_ICONS[a.getAttribute("href")];
    if (ic) a.insertAdjacentHTML("afterbegin", `<svg class="ico" viewBox="0 0 24 24" aria-hidden="true">${ic}</svg>`);
  });
  const lo = document.querySelector(".sidebar .logout");
  if (lo && !lo.querySelector(".theme-toggle")) {
    const b = document.createElement("button");
    b.type = "button"; b.className = "theme-toggle"; b.onclick = toggleTheme;
    lo.prepend(b);
  }
  updateThemeBtn();
}

function highlightNav() {
  const page = window.location.pathname.split("/").pop() || "index.html";
  const role = currentRole();

  if (role === "COMPANY") {
    document.querySelectorAll(".nav a").forEach(a => {
      const h = a.getAttribute("href");
      if (h === "courses.html" || h === "ai.html" || h === "profile.html") a.style.display = "none";
    });
    if (page === "profile.html") window.location.href = "projects.html";
  }

  document.querySelectorAll(".nav a").forEach(a => {
    if (a.getAttribute("href") === page) { a.classList.add("active"); a.setAttribute("aria-current", "page"); }
  });
  enhanceShell();
}