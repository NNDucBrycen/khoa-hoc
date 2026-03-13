import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi, beforeEach } from "vitest";
import React from "react";
import { MemoryRouter } from "react-router-dom";
import { AuthProvider } from "../context/AuthProvider";
import { LoginForm } from "./LoginForm";
import * as authApi from "../api/authApi";

vi.mock("../api/authApi");
const mockedAuthApi = vi.mocked(authApi);

function Wrapper({ children }: { children: React.ReactNode }) {
  return React.createElement(
    MemoryRouter,
    null,
    React.createElement(AuthProvider, null, children),
  );
}

describe("LoginForm", () => {
  beforeEach(() => {
    vi.resetAllMocks();
    // Default: no active session
    mockedAuthApi.fetchCsrf.mockRejectedValue(
      Object.assign(new Error("No session"), { status: 401 }),
    );
  });

  it("renders username, password fields and submit button", () => {
    render(React.createElement(LoginForm), { wrapper: Wrapper });
    expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /đăng nhập/i }),
    ).toBeInTheDocument();
  });

  it("displays error message when login rejects", async () => {
    mockedAuthApi.login.mockRejectedValue(
      Object.assign(new Error("Invalid"), {
        status: 401,
        body: { detail: "Invalid username or password." },
      }),
    );

    render(React.createElement(LoginForm), { wrapper: Wrapper });
    const user = userEvent.setup();

    await user.type(screen.getByLabelText(/username/i), "user01");
    await user.type(screen.getByLabelText(/password/i), "wrongpassword");
    await user.click(screen.getByRole("button", { name: /đăng nhập/i }));

    await waitFor(() =>
      expect(screen.getByRole("alert")).toHaveTextContent(
        "Invalid username or password.",
      ),
    );
  });

  it("disables button while loading", async () => {
    let resolve!: (v: authApi.LoginResponse) => void;
    mockedAuthApi.login.mockReturnValue(
      new Promise((r) => {
        resolve = r;
      }),
    );
    mockedAuthApi.fetchCsrf.mockResolvedValue({
      csrfToken: "x",
      headerName: "X-CSRF-TOKEN",
      parameterName: "_csrf",
    });

    render(React.createElement(LoginForm), { wrapper: Wrapper });
    const user = userEvent.setup();

    await user.type(screen.getByLabelText(/username/i), "user01");
    await user.type(screen.getByLabelText(/password/i), "User@123");
    await user.click(screen.getByRole("button", { name: /đăng nhập/i }));

    expect(
      screen.getByRole("button", { name: /đang đăng nhập/i }),
    ).toBeDisabled();
    resolve({ authenticated: true, username: "user01", roles: ["USER"] });
  });
});
