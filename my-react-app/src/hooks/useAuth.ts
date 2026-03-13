// useAuth hook — exposes auth context; only import from hooks/useAuth (AR-F2)
import { useContext } from "react";
import {
  AuthContext,
  type AuthContextValue,
  type AuthUser,
} from "../context/AuthContext";

export type { AuthContextValue, AuthUser };
export { AuthProvider } from "../context/AuthProvider";

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
