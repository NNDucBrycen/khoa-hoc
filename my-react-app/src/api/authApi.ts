/**
 * Auth API client — the ONLY place that sets credentials: 'include' and X-CSRF-TOKEN (AR-F3).
 * All requests go through the Vite dev proxy /api → http://localhost:8080.
 */

export interface LoginResponse {
  authenticated: boolean;
  username: string;
  roles: string[];
}

export interface CsrfResponse {
  csrfToken: string;
  headerName: string;
  parameterName: string;
}

export interface LogoutResponse {
  success: boolean;
}

const BASE = "/api/v1/auth";

export async function login(
  username: string,
  password: string,
): Promise<LoginResponse> {
  const res = await fetch(`${BASE}/login`, {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw Object.assign(new Error(body.detail ?? "Login failed"), {
      status: res.status,
      body,
    });
  }
  return res.json() as Promise<LoginResponse>;
}

export async function fetchCsrf(): Promise<CsrfResponse> {
  const res = await fetch(`${BASE}/csrf`, {
    method: "GET",
    credentials: "include",
  });
  if (!res.ok) {
    throw Object.assign(new Error("Session expired or missing"), {
      status: res.status,
    });
  }
  return res.json() as Promise<CsrfResponse>;
}

export async function logout(csrfToken: string): Promise<LogoutResponse> {
  const res = await fetch(`${BASE}/logout`, {
    method: "POST",
    credentials: "include",
    headers: { "X-CSRF-TOKEN": csrfToken },
  });
  if (!res.ok) {
    throw Object.assign(new Error("Logout failed"), { status: res.status });
  }
  return res.json() as Promise<LogoutResponse>;
}
