// AuthProvider component — the only component export in this file (satisfies react-refresh)
import { useCallback, useEffect, useReducer, type ReactNode } from "react";
import {
  AuthContext,
  type AuthContextValue,
  type AuthUser,
} from "./AuthContext";
import * as authApi from "../api/authApi";

interface AuthState {
  user: AuthUser | null;
  csrfToken: string | null;
}

type AuthAction =
  | { type: "LOGIN"; user: AuthUser; csrfToken: string }
  | { type: "LOGOUT" }
  | { type: "SET_CSRF"; csrfToken: string };

function authReducer(state: AuthState, action: AuthAction): AuthState {
  switch (action.type) {
    case "LOGIN":
      return { user: action.user, csrfToken: action.csrfToken };
    case "LOGOUT":
      return { user: null, csrfToken: null };
    case "SET_CSRF":
      return { ...state, csrfToken: action.csrfToken };
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(authReducer, {
    user: null,
    csrfToken: null,
  });

  // On app init: restore CSRF if session still valid (R-4 — CSRF not lost on reload)
  useEffect(() => {
    authApi
      .fetchCsrf()
      .then((csrf) => {
        dispatch({ type: "SET_CSRF", csrfToken: csrf.csrfToken });
      })
      .catch(() => {
        // 401 → no active session; stay unauthenticated
      });
  }, []);

  const login = useCallback(async (username: string, password: string) => {
    const loginRes = await authApi.login(username, password);
    const csrfRes = await authApi.fetchCsrf();
    dispatch({
      type: "LOGIN",
      user: { username: loginRes.username, roles: loginRes.roles },
      csrfToken: csrfRes.csrfToken,
    });
  }, []);

  const logout = useCallback(async () => {
    if (state.csrfToken) {
      await authApi.logout(state.csrfToken);
    }
    dispatch({ type: "LOGOUT" });
  }, [state.csrfToken]);

  const value: AuthContextValue = {
    user: state.user,
    csrfToken: state.csrfToken,
    isAuthenticated: state.user !== null,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
