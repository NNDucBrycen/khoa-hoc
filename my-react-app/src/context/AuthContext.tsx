// Context definition only — no components here (satisfies react-refresh/only-export-components)
import { createContext } from "react";

export interface AuthUser {
  username: string;
  roles: string[];
}

export interface AuthContextValue {
  user: AuthUser | null;
  csrfToken: string | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextValue | null>(null);
