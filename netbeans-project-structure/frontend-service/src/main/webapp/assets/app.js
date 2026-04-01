// Use the same host as the frontend (localhost vs 127.0.0.1 are different origins for fetch/CORS).
const API_ORIGIN = window.location.origin;
const API = {
  auth: `${API_ORIGIN}/auth-service/api`,
  catalog: `${API_ORIGIN}/catalog-service/api`,
  reservation: `${API_ORIGIN}/reservation-service/api`,
  checkout: `${API_ORIGIN}/checkout-penalty-service/api`,
  inventory: `${API_ORIGIN}/inventory-service/api`
};

function appBasePath() {
  const segs = window.location.pathname.split("/").filter(Boolean);
  return segs.length > 0 ? `/${segs[0]}` : "";
}

// Cookie name shared with backend JwtUtil.TOKEN_COOKIE_NAME (sent automatically on same-origin fetch).
const TOKEN_COOKIE = "lab5_token";
const TOKEN_COOKIE_MAX_AGE_SEC = 3600;

function setTokenCookie(token) {
  if (!token) {
    document.cookie = `${TOKEN_COOKIE}=; Path=/; Max-Age=0; SameSite=Lax`;
    return;
  }
  const enc = encodeURIComponent(token);
  document.cookie = `${TOKEN_COOKIE}=${enc}; Path=/; Max-Age=${TOKEN_COOKIE_MAX_AGE_SEC}; SameSite=Lax`;
}

function getTokenCookie() {
  const prefix = `${TOKEN_COOKIE}=`;
  const parts = document.cookie.split(";");
  for (let i = 0; i < parts.length; i++) {
    const c = parts[i].trim();
    if (c.startsWith(prefix)) {
      const raw = c.substring(prefix.length);
      try {
        return decodeURIComponent(raw);
      } catch (_) {
        return raw;
      }
    }
  }
  return "";
}

function setToken(token) {
  if (token) {
    sessionStorage.setItem("lab5_token", token);
    setTokenCookie(token);
  } else {
    sessionStorage.removeItem("lab5_token");
    setTokenCookie("");
  }
}

function getToken() {
  return sessionStorage.getItem("lab5_token") || getTokenCookie() || "";
}

function setUser(user) {
  if (user) sessionStorage.setItem("lab5_user", user);
  else sessionStorage.removeItem("lab5_user");
}

function getUser() {
  return sessionStorage.getItem("lab5_user") || "";
}

function ensureAuthOrRedirect() {
  blockPrivateModeIfNeeded();
  if (!getToken()) {
    window.location.href = `${appBasePath()}/login.jsp`;
    return;
  }
  verifyTokenAccessOrClear();
}

function logout() {
  setToken("");
  setUser("");
  window.location.href = `${appBasePath()}/login.jsp`;
}

async function requestJson(url, options = {}) {
  try {
    const token = getToken();
    const headers = Object.assign({}, options.headers || {});
    // Skip auth header injection for the login endpoint itself.
    if (token && !url.includes("/auth/login")) headers["Authorization"] = `Bearer ${token}`;

    const fetchOptions = Object.assign({}, options, {
      headers,
      credentials: "same-origin"
    });
    const response = await fetch(url, fetchOptions);
    const text = await response.text();
    let data;
    try { data = JSON.parse(text); } catch (_) { data = text; }
    return { ok: response.ok, status: response.status, data };
  } catch (err) {
    return { ok: false, status: 0, data: { error: String(err) } };
  }
}

function pretty(targetId, value) {
  const el = document.getElementById(targetId);
  if (!el) return;
  el.textContent = typeof value === "string" ? value : JSON.stringify(value, null, 2);
}

function renderText(targetId, title, lines) {
  const content = [title, "----------------------------------------", ...lines].join("\n");
  pretty(targetId, content);
}

function renderAuthResult(targetId, result) {
  if (!result.ok) {
    renderText(targetId, "Login Failed", [
      `HTTP Status: ${result.status}`,
      "Reason: Authentication service could not process this request.",
      `Details: ${JSON.stringify(result.data)}`
    ]);
    return;
  }
  const success = result.data && result.data.success;
  renderText(targetId, success ? "Login Successful" : "Login Rejected", [
    `HTTP Status: ${result.status}`,
    `Success: ${success ? "Yes" : "No"}`,
    `Message: ${result.data && result.data.message ? result.data.message : "No message returned."}`,
    `Token Present: ${result.data && result.data.token ? "Yes" : "No"}`
  ]);
}

function renderHealthResult(targetId, checks) {
  const names = ["Auth", "Catalog", "Inventory", "Reservation", "Checkout"];
  const lines = checks.map((c, idx) => {
    const state = c.ok ? "UP" : "DOWN";
    const payload = typeof c.data === "string" ? c.data : JSON.stringify(c.data);
    return `${names[idx]}: ${state} (HTTP ${c.status}) - ${payload}`;
  });
  renderText(targetId, "Service Health Overview", lines);
}

function renderCatalogResult(targetId, result) {
  if (!result.ok) {
    renderText(targetId, "Catalog Search Failed", [
      `HTTP Status: ${result.status}`,
      `Details: ${JSON.stringify(result.data)}`
    ]);
    return;
  }
  const items = Array.isArray(result.data) ? result.data : [];
  if (items.length === 0) {
    renderText(targetId, "Catalog Search Result", ["No equipment matched the provided query."]);
    return;
  }
  const lines = items.map((item, idx) => `${idx + 1}. ${item.name} | Category: ${item.category} | Equipment ID: ${item.id}`);
  renderText(targetId, "Catalog Search Result", lines);
}

function renderReservationResult(targetId, action, result) {
  if (!result.ok) {
    renderText(targetId, `${action} Failed`, [
      `HTTP Status: ${result.status}`,
      `Details: ${JSON.stringify(result.data)}`
    ]);
    return;
  }
  if (Array.isArray(result.data)) {
    if (!result.data.length) {
      renderText(targetId, "Reservation List", ["No reservations found yet."]);
      return;
    }
    const lines = result.data.map((r) => `Reservation #${r.id} | User: ${r.username} | Status: ${r.status} | Start: ${r.startDate} | End: ${r.endDate}`);
    renderText(targetId, "Reservation List", lines);
    return;
  }
  const r = result.data || {};
  renderText(targetId, action, [
    `Reservation ID: ${r.id ?? "N/A"}`,
    `User: ${r.username ?? "N/A"}`,
    `Equipment ID: ${r.equipmentId ?? "N/A"}`,
    `Dates: ${r.startDate ?? "N/A"} to ${r.endDate ?? "N/A"}`,
    `Status: ${r.status ?? "N/A"}`
  ]);
}

function renderCheckoutResult(targetId, action, result) {
  if (!result.ok) {
    renderText(targetId, `${action} Failed`, [
      `HTTP Status: ${result.status}`,
      `Details: ${JSON.stringify(result.data)}`
    ]);
    return;
  }
  const r = result.data || {};
  renderText(targetId, action, [
    `Rental ID: ${r.id ?? "N/A"}`,
    `User: ${r.username ?? "N/A"}`,
    `Asset Tag: ${r.assetTag ?? "N/A"}`,
    `Due Date: ${r.dueDate ?? "N/A"}`,
    `Return Date: ${r.returnDate ?? "Not returned yet"}`,
    `Fine Amount: $${(r.fineAmount ?? 0).toFixed ? r.fineAmount.toFixed(2) : r.fineAmount}`
  ]);
}

function renderHistoryResult(targetId, result) {
  if (!result.ok) {
    renderText(targetId, "History Lookup Failed", [
      `HTTP Status: ${result.status}`,
      `Details: ${JSON.stringify(result.data)}`
    ]);
    return;
  }
  const history = Array.isArray(result.data) ? result.data : [];
  if (!history.length) {
    renderText(targetId, "Rental History", ["No rental records found for this user."]);
    return;
  }
  const lines = history.map((h) => `Rental #${h.id} | ${h.assetTag} | Due: ${h.dueDate} | Return: ${h.returnDate || "Pending"} | Fine: $${h.fineAmount ?? 0}`);
  renderText(targetId, "Rental History", lines);
}

function updateHeaderToken() {
  const badge = document.getElementById("tokenBadge");
  const userLabel = document.getElementById("userLabel");
  if (!badge) return;
  const token = getToken();
  if (token) {
    badge.classList.remove("err");
    badge.classList.add("ok");
    badge.textContent = "Token active";
  } else {
    badge.classList.remove("ok");
    badge.classList.add("err");
    badge.textContent = "Token not set";
  }
  if (userLabel) userLabel.textContent = getUser() ? `User: ${getUser()}` : "User: guest";
}

function redirectBlockedByPrivate(isLoginPage) {
  try { setToken(""); } catch (_) {}
  try { setUser(""); } catch (_) {}

  const badge = document.getElementById("tokenBadge");
  if (badge) badge.textContent = "Private browsing not allowed";

  if (!isLoginPage || window.location.search.indexOf("private=1") === -1) {
    window.location.href = `${appBasePath()}/login.jsp?private=1`;
  }
}

function blockPrivateModeIfNeeded() {
  // Best-effort heuristic only. Detecting private/incognito reliably is not supported by browsers.
  const params = new URLSearchParams(window.location.search);
  if (params.get("private") === "1") return;

  const probeKey = "lab5_private_window_probe";
  const probeMode = params.get("privateProbe");
  const isLoginPage = window.location.pathname.endsWith("/login.jsp") || window.location.pathname.endsWith("login.jsp");

  const probeDoneKey = "lab5_private_probe_done";
  let alreadyProbed = false;
  try { alreadyProbed = window.sessionStorage.getItem(probeDoneKey) === "1"; } catch (_) {}

  // If we are not in the probe-return step, skip after first probe.
  if (probeMode !== "done" && alreadyProbed) return;

  if (probeMode !== "done") {
    // First load: set a localStorage marker, then reload once to see if it disappears.
    try {
      window.localStorage.setItem(probeKey, Date.now().toString());
      window.sessionStorage.setItem(probeDoneKey, "1");
    } catch (e) {
      redirectBlockedByPrivate(isLoginPage);
      return;
    }

    params.set("privateProbe", "done");
    const next = `${window.location.pathname}?${params.toString()}`;
    window.location.replace(next);
    return;
  }

  // Second load: if localStorage marker disappeared, treat as private.
  try {
    const exists = window.localStorage.getItem(probeKey) != null;
    window.localStorage.removeItem(probeKey);
    if (!exists) redirectBlockedByPrivate(isLoginPage);
  } catch (e) {
    redirectBlockedByPrivate(isLoginPage);
  }
}

// Run the private-mode guard on every page load.
blockPrivateModeIfNeeded();

// If we redirected here because of private mode, show a message on the login page.
(() => {
  try {
    const params = new URLSearchParams(window.location.search);
    if (params.get("private") !== "1") return;
    renderText("loginOut", "Private browsing not allowed", ["Please open this site in a normal (non-private) window and try again."]);
  } catch (_) {}
})();

async function verifyTokenAccessOrClear() {
  const token = getToken();
  if (!token) return;

  // If the backend is still issuing the old "token" format (base64, etc),
  // don't attempt JWT-only verification (and don't clear it).
  const looksLikeJwt = /^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$/;
  if (!looksLikeJwt.test(token)) return;

  // Lightweight probe: if the token is not being injected/accepted, server will return 401.
  try {
    const badge = document.getElementById("tokenBadge");
    if (badge) badge.textContent = "Verifying token...";

    const res = await requestJson(`${API.catalog}/catalog/equipment?query=__token_probe__`);
    if (!res.ok && res.status === 401) {
      setToken("");
      setUser("");
      if (badge) badge.textContent = "Token invalid";
      window.location.href = `${appBasePath()}/login.jsp`;
      return;
    }

    if (badge) badge.textContent = "Token verified";
  } catch (_) {
    // Don't block the app if the probe fails for network/DB reasons.
  }
}
