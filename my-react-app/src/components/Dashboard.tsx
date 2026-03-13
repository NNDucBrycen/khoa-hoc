import { useAuth } from "../hooks/useAuth";

export function Dashboard() {
  const { user, csrfToken, logout } = useAuth();

  async function handleLogout() {
    await logout();
  }

  return (
    <div style={{ maxWidth: 800, margin: "40px auto", padding: 24 }}>
      <header
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          borderBottom: "1px solid #ccc",
          paddingBottom: 12,
        }}
      >
        <span>My App</span>
        <span>
          {user?.username}&nbsp;
          <button onClick={handleLogout}>Logout</button>
        </span>
      </header>
      <main style={{ paddingTop: 24 }}>
        <p>
          Welcome, <strong>{user?.username}</strong>
        </p>
        <p>
          Session: <span style={{ color: "green" }}>Active</span> &nbsp;|&nbsp;
          CSRF token:{" "}
          <span style={{ color: csrfToken ? "green" : "red" }}>
            {csrfToken ? "Loaded" : "Not loaded"}
          </span>
        </p>
        <div style={{ marginTop: 16, display: "flex", gap: 8 }}>
          <button disabled>[Create]</button>
          <button disabled>[Update]</button>
          <button disabled>[Delete]</button>
        </div>
      </main>
    </div>
  );
}
