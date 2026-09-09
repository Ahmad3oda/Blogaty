import React from "react";
import { useAuth } from "../hooks/useAuth";

function SettingsPage() {
  const { logout } = useAuth();
  const username = localStorage.getItem("username") || sessionStorage.getItem("username") || "User";
  const userId = localStorage.getItem("userId") || sessionStorage.getItem("userId") || "Unknown";

  return (
    <div className="container mt-4">
      <h3 className="mb-4">⚙️ Account Settings</h3>
      <div className="card shadow-sm p-4" style={{ maxWidth: "500px" }}>
        <div className="mb-3">
          <label className="form-label fw-bold">Username</label>
          <input className="form-control" value={username} disabled />
        </div>
        <div className="mb-3">
          <label className="form-label fw-bold">User ID</label>
          <input className="form-control" value={userId} disabled />
        </div>
        <hr className="my-4" />
        <button
          className="btn btn-danger"
          onClick={() => {
            logout();
            localStorage.clear();
            sessionStorage.clear();
            window.location.href = "/login";
          }}
        >
          Logout of Account
        </button>
      </div>
    </div>
  );
}

export default SettingsPage;
