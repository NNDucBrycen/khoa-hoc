import { useState, type FormEvent } from "react";
import { useAuth } from "../hooks/useAuth";

export function LoginForm() {
  const { login } = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await login(username, password);
    } catch (err: unknown) {
      const detail = (err as { body?: { detail?: string } })?.body?.detail;
      setError(detail ?? "Sai tên đăng nhập hoặc mật khẩu");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div
      style={{
        maxWidth: 400,
        margin: "80px auto",
        border: "1px solid #ccc",
        padding: 32,
        borderRadius: 8,
      }}
    >
      <h2 style={{ textAlign: "center" }}>ĐĂNG NHẬP HỆ THỐNG</h2>
      <form onSubmit={handleSubmit} noValidate>
        <div style={{ marginBottom: 16 }}>
          <label htmlFor="username">Username</label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            autoComplete="username"
            style={{
              display: "block",
              width: "100%",
              padding: "8px",
              boxSizing: "border-box",
            }}
          />
        </div>
        <div style={{ marginBottom: 16 }}>
          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            autoComplete="current-password"
            style={{
              display: "block",
              width: "100%",
              padding: "8px",
              boxSizing: "border-box",
            }}
          />
        </div>
        {error && (
          <p role="alert" style={{ color: "red" }}>
            {error}
          </p>
        )}
        <button
          type="submit"
          disabled={loading}
          style={{ width: "100%", padding: "10px" }}
        >
          {loading ? "Đang đăng nhập..." : "ĐĂNG NHẬP"}
        </button>
      </form>
    </div>
  );
}
