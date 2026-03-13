import { render, screen } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import React from "react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "../context/AuthProvider";
import { ProtectedRoute } from "./ProtectedRoute";
import * as authApi from "../api/authApi";

vi.mock("../api/authApi");
const mockedAuthApi = vi.mocked(authApi);

function renderWithRoute(initialPath: string, authenticated: boolean) {
  if (authenticated) {
    mockedAuthApi.login.mockResolvedValue({
      authenticated: true,
      username: "u",
      roles: [],
    });
    mockedAuthApi.fetchCsrf
      .mockRejectedValueOnce(new Error())
      .mockResolvedValueOnce({
        csrfToken: "x",
        headerName: "X-CSRF-TOKEN",
        parameterName: "_csrf",
      });
  } else {
    mockedAuthApi.fetchCsrf.mockRejectedValue(new Error("No session"));
  }

  return render(
    React.createElement(
      MemoryRouter,
      { initialEntries: [initialPath] },
      React.createElement(
        AuthProvider,
        null,
        React.createElement(
          Routes,
          null,
          React.createElement(Route, {
            path: "/login",
            element: React.createElement("div", null, "Login page"),
          }),
          React.createElement(Route, {
            path: "/dashboard",
            element: React.createElement(
              ProtectedRoute,
              null,
              React.createElement("div", null, "Dashboard"),
            ),
          }),
        ),
      ),
    ),
  );
}

describe("ProtectedRoute", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("redirects to /login when unauthenticated", async () => {
    renderWithRoute("/dashboard", false);
    // Wait for init fetchCsrf to settle then check redirect
    await screen.findByText("Login page");
    expect(screen.queryByText("Dashboard")).not.toBeInTheDocument();
  });
});
