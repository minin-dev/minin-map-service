const STORAGE_BASE_URL = "debugBaseUrl";
const STORAGE_BEARER = "debugBearer";

function getBaseUrl() {
  return localStorage.getItem(STORAGE_BASE_URL) || "http://localhost:8080/api/v1";
}

function setBaseUrl(value) {
  localStorage.setItem(STORAGE_BASE_URL, value.trim().replace(/\/$/, ""));
}

function getBearerToken() {
  return localStorage.getItem(STORAGE_BEARER) || "";
}

function setBearerToken(value) {
  localStorage.setItem(STORAGE_BEARER, value.trim());
}

function formatJson(value) {
  if (typeof value === "string") {
    return value;
  }
  return JSON.stringify(value, null, 2);
}

function buildUrl(path, query) {
  return `${getBaseUrl()}${path}${query ? `?${query}` : ""}`;
}

function normalizeErrorPayload(status, statusText, payload, url) {
  if (payload && typeof payload === "object") {
    const details = [];
    if (Array.isArray(payload.validationErrors) && payload.validationErrors.length > 0) {
      details.push("validationErrors:");
      payload.validationErrors.forEach((item) => details.push(`- ${item}`));
    }
    return {
      status,
      statusText,
      message: payload.message || payload.error || "Request failed",
      path: payload.path || url,
      details,
      payload
    };
  }

  return {
    status,
    statusText,
    message: typeof payload === "string" && payload.length > 0 ? payload : "Request failed",
    path: url,
    details: [],
    payload
  };
}

async function fetchJson(config) {
  const path = config.path;
  const query = config.query || "";
  const url = buildUrl(path, query);

  const headers = Object.assign({}, config.headers || {});
  const token = getBearerToken();
  if (token && !headers.Authorization) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(url, {
    method: config.method || "GET",
    headers,
    body: config.body
  });

  const text = await response.text();
  let payload = text;
  try {
    payload = text ? JSON.parse(text) : null;
  } catch (e) {
    payload = text;
  }

  if (!response.ok) {
    throw normalizeErrorPayload(response.status, response.statusText, payload, url);
  }

  return {
    status: response.status,
    statusText: response.statusText,
    payload,
    url
  };
}

async function sendRequest(config) {
  const output = document.getElementById(config.outputId);
  const path = config.path;
  const query = config.query || "";
  const url = buildUrl(path, query);

  const headers = {};
  const token = getBearerToken();
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  let body;
  if (config.bodyId) {
    const raw = document.getElementById(config.bodyId).value.trim();
    if (raw.length > 0) {
      try {
        JSON.parse(raw);
        body = raw;
        headers["Content-Type"] = "application/json";
      } catch (e) {
        output.textContent = `Invalid JSON body: ${e.message}`;
        return;
      }
    }
  }

  output.textContent = `Sending ${config.method} ${url} ...`;
  const start = performance.now();

  try {
    const response = await fetch(url, {
      method: config.method,
      headers,
      body
    });

    const duration = (performance.now() - start).toFixed(0);
    const text = await response.text();
    let payload = text;

    try {
      payload = text ? JSON.parse(text) : "<empty>";
    } catch (e) {
      payload = text || "<empty>";
    }

    output.textContent = [
      `Status: ${response.status} ${response.statusText}`,
      `Time: ${duration} ms`,
      "",
      formatJson(payload)
    ].join("\n");
  } catch (error) {
    output.textContent = `Network error: ${error.message}`;
  }
}

function setupGlobalControls() {
  const baseInput = document.getElementById("baseUrl");
  const tokenInput = document.getElementById("bearer");

  if (baseInput) {
    baseInput.value = getBaseUrl();
  }
  if (tokenInput) {
    tokenInput.value = getBearerToken();
  }

  const saveButton = document.getElementById("saveGlobal");
  if (saveButton) {
    saveButton.addEventListener("click", () => {
      setBaseUrl(baseInput.value);
      setBearerToken(tokenInput.value);
      alert("Saved");
    });
  }
}

window.DebugApi = {
  sendRequest,
  setupGlobalControls,
  fetchJson,
  normalizeErrorPayload,
  formatJson
};

