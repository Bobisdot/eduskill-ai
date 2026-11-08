const API_BASE = "http://localhost:8080";

function saveToken(token) { localStorage.setItem("eduskill_token", token); }
function getToken() { return localStorage.getItem("eduskill_token"); }
function clearToken() { localStorage.removeItem("eduskill_token"); }
function isLoggedIn() { return !!getToken(); }

function logout() {
  clearToken();
  window.location.href = "index.html";
}

function requireAuth() {
  if (!isLoggedIn()) window.location.href = "index.html";
}

// JWT Токеннен Email алу
function currentEmail() {
  const token = getToken();
  if (!token) return "";
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.sub || "";
  } catch (e) {
    return "";
  }
}

// JWT Токеннен РӨЛДІ (Role: COMPANY немесе STUDENT) алу
function currentRole() {
  const token = getToken();
  if (!token) return "STUDENT";
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    if (payload.authorities && payload.authorities.length > 0) {
      return payload.authorities[0].replace("ROLE_", "");
    }
    return payload.role ? payload.role.replace("ROLE_", "") : "STUDENT";
  } catch (e) {
    return "STUDENT";
  }
}

async function apiFetch(path, options = {}) {
  const headers = Object.assign({ "Content-Type": "application/json" }, options.headers || {});
  const token = getToken();
  if (token) headers["Authorization"] = "Bearer " + token;

  const res = await fetch(API_BASE + path, Object.assign({}, options, { headers }));

  if (res.status === 401 || res.status === 403) {
    if (path !== "/api/v1/auth/authenticate" && path !== "/api/v1/auth/register") {
      clearToken();
      window.location.href = "index.html";
    }
  }

  const contentType = res.headers.get("content-type") || "";
  const body = contentType.includes("application/json") ? await res.json().catch(() => null) : await res.text();

  if (!res.ok) {
    const message = (body && body.message) ? body.message : (typeof body === "string" && body) ? body : "Сұраныс орындалмады (" + res.status + ")";
    throw new Error(message);
  }
  return body;
}

// Навигациялық сайдбарды баптау және Компанияға артық (Профиль, AI, Курстар) сілтемелерін жасыру
function highlightNav() {
  const page = window.location.pathname.split("/").pop() || "index.html";
  const role = currentRole();

  // Егер Рөл = COMPANY болса:
  if (role === "COMPANY") {
    document.querySelectorAll(".nav a").forEach(a => {
      const href = a.getAttribute("href");
      // Студентке арналған беттерді сайдбардан жасырамыз
      if (href === "courses.html" || href === "ai.html" || href === "profile.html") {
        a.style.display = "none";
      }
    });

    // Егер Компания профиль бетін сілтеме арқылы ашпақшы болса, Жобаларға бағыттаймыз
    if (page === "profile.html") {
      window.location.href = "projects.html";
    }
  }

  document.querySelectorAll(".nav a").forEach(a => {
    if (a.getAttribute("href") === page) a.classList.add("active");
  });
}

function showError(el, message) {
  el.textContent = message;
  el.className = "alert alert-error";
  el.style.display = "block";
}

function showSuccess(el, message) {
  el.textContent = message;
  el.className = "alert alert-success";
  el.style.display = "block";
}

function hideAlert(el) {
  el.style.display = "none";
}