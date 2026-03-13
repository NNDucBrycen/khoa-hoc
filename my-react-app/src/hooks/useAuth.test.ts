import { renderHook, waitFor, act } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import React from "react";
import { AuthProvider } from "../context/AuthProvider";
import { useAuth } from "./useAuth";
import * as authApi from "../api/authApi";

vi.mock("../api/authApi");

const mockedAuthApi = vi.mocked(authApi);

function wrapper({ children }: { children: React.ReactNode }) {
  return React.createElement(AuthProvider, null, children);
}

describe("useAuth", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    // Default: no active session on init
    mockedAuthApi.fetchCsrf.mockRejectedValue(
      Object.assign(new Error("No session"), { status: 401 }),
    );
  });

  it("starts unauthenticated when no session exists", async () => {
    const { result } = renderHook(() => useAuth(), { wrapper });
    await waitFor(() => {
      expect(result.current.isAuthenticated).toBe(false);
      expect(result.current.user).toBeNull();
    });
  });

  it("sets user and csrfToken after successful login", async () => {
    mockedAuthApi.login.mockResolvedValue({
      authenticated: true,
      username: "user01",
      roles: ["USER"],
    });
    mockedAuthApi.fetchCsrf
      .mockRejectedValueOnce(new Error("No session")) // init call
      .mockResolvedValueOnce({
        csrfToken: "tok123",
        headerName: "X-CSRF-TOKEN",
        parameterName: "_csrf",
      });

    const { result } = renderHook(() => useAuth(), { wrapper });

    await act(async () => {
      await result.current.login("user01", "User@123");
    });

    expect(result.current.isAuthenticated).toBe(true);
    expect(result.current.user?.username).toBe("user01");
    expect(result.current.csrfToken).toBe("tok123");
  });

  it("clears state after logout", async () => {
    // Setup: already authenticated
    mockedAuthApi.login.mockResolvedValue({
      authenticated: true,
      username: "user01",
      roles: ["USER"],
    });
    mockedAuthApi.fetchCsrf
      .mockRejectedValueOnce(new Error("No session"))
      .mockResolvedValueOnce({
        csrfToken: "tok123",
        headerName: "X-CSRF-TOKEN",
        parameterName: "_csrf",
      });
    mockedAuthApi.logout.mockResolvedValue({ success: true });

    const { result } = renderHook(() => useAuth(), { wrapper });
    await act(async () => {
      await result.current.login("user01", "User@123");
    });

    await act(async () => {
      await result.current.logout();
    });

    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.user).toBeNull();
    expect(result.current.csrfToken).toBeNull();
  });

  it("throws when called outside AuthProvider", () => {
    expect(() => renderHook(() => useAuth())).toThrow(
      "useAuth must be used within AuthProvider",
    );
  });
});
