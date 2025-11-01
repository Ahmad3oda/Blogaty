import { ReactNode, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  getFollowersCount,
  getSuggestions,
  followUser,
} from "../api/followApi";
import "../styles/Layout.css";

interface LayoutProps {
  children: ReactNode;
}

interface Suggestion {
  id: number;
  username: string;
  avatar?: string;
}

function Layout({ children }: LayoutProps) {
  const navigate = useNavigate();
  const username = sessionStorage.getItem("username");
  const userId = sessionStorage.getItem("userId");

  const [followers, setFollowers] = useState<number>(0);
  const [following, setFollowing] = useState<number>(0);
  const [suggestions, setSuggestions] = useState<Suggestion[]>([]);
  const [loadingSuggestions, setLoadingSuggestions] = useState<boolean>(true);
  const [actionLoading, setActionLoading] = useState<number[]>([]);

  // Fetch follower counts
  useEffect(() => {
    const fetchCounts = async () => {
      try {
        if (!userId) return;
        const data = await getFollowersCount(Number(userId));
        setFollowers(data.followers || 0);
        setFollowing(data.following || 0);
      } catch (err) {
        console.error("Failed to load follower counts:", err);
      }
    };
    fetchCounts();
  }, [userId]);

  // Fetch suggestions
  useEffect(() => {
    const fetchSuggestions = async () => {
      try {
        if (!userId) return;
        setLoadingSuggestions(true);
        const res = await getSuggestions(Number(userId));
        setSuggestions(res.suggestions || res || []); // handle both formats
      } catch (err) {
        console.error("Failed to load suggestions:", err);
      } finally {
        setLoadingSuggestions(false);
      }
    };
    fetchSuggestions();
  }, [userId]);

  // Follow button handler
  const handleFollow = async (targetId: number) => {
    if (!userId) return;
    try {
      setActionLoading((prev) => [...prev, targetId]);
      await followUser(Number(userId), targetId);
      setSuggestions((prev) => prev.filter((u) => u.id !== targetId));
    } catch (err) {
      console.error("Follow failed:", err);
    } finally {
      setActionLoading((prev) => prev.filter((id) => id !== targetId));
    }
  };

  return (
    <div className="layout-container d-flex">
      {/* Sidebar */}
      <aside className="sidebar bg-light p-3 shadow-sm">
        <div
          className="profile-box text-center mb-4"
          onClick={() => navigate(`/profile/${userId}`)}
          style={{ cursor: "pointer" }}
        >
          <img
            src={`https://ui-avatars.com/api/?name=${username}`}
            alt="profile"
            className="rounded-circle mb-2"
            width="80"
          />
          <h5>@{username}</h5>
          <p className="mb-1 text-muted">
            Followers: <strong>{followers}</strong>
          </p>
          <p className="mb-3 text-muted">
            Following: <strong>{following}</strong>
          </p>
        </div>

        <div className="sidebar-links">
          <button
            className="btn btn-outline-primary w-100 mb-2"
            onClick={() => navigate("/blogs")}
          >
            Home
          </button>
          <button
            className="btn btn-outline-secondary w-100 mb-2"
            onClick={() => navigate("/bookmarks")}
          >
            Bookmarks
          </button>
          <button
            className="btn btn-outline-success w-100 mb-2"
            onClick={() => navigate("/follow")}
          >
            Followers
          </button>
          <button
            className="btn btn-outline-dark w-100"
            onClick={() => navigate("/settings")}
          >
            Settings
          </button>
        </div>
      </aside>

      {/* Main content */}
      <div className="main-content flex-grow-1">
        {/* Navbar */}
        <nav className="navbar bg-white shadow-sm px-4 py-2 d-flex align-items-center justify-content-between fixed-top full-width-nav">
          <div className="d-flex align-items-center gap-3">
            <button
              className="btn btn-outline-primary"
              onClick={() => navigate("/bookmarks")}
            >
              🔖
            </button>
            <button
              className="btn btn-outline-success"
              onClick={() => navigate("/follow")}
            >
              👥
            </button>
          </div>

          <div className="flex-grow-1 d-flex justify-content-center">
            <input
              type="text"
              className="form-control text-center"
              placeholder="Search blogs..."
              style={{ maxWidth: "350px" }}
            />
          </div>

          <div className="dropdown">
            <button
              className="btn btn-outline-dark dropdown-toggle"
              data-bs-toggle="dropdown"
            >
              {username}
            </button>
            <ul className="dropdown-menu dropdown-menu-end">
              <li>
                <button
                  className="dropdown-item"
                  onClick={() => navigate("/profile")}
                >
                  Profile
                </button>
              </li>
              <li>
                <button
                  className="dropdown-item"
                  onClick={() => navigate("/settings")}
                >
                  Settings
                </button>
              </li>
              <li>
                <hr className="dropdown-divider" />
              </li>
              <li>
                <button
                  className="dropdown-item text-danger"
                  onClick={() => {
                    localStorage.removeItem("token");
                    window.location.href = "/login";
                  }}
                >
                  Logout
                </button>
              </li>
            </ul>
          </div>
        </nav>

        <div className="content-area p-4 mt-5">{children}</div>
      </div>

      {/* Right sidebar */}
      <aside className="rightbar bg-light p-3 shadow-sm">
        <h5 className="text-center mb-3">Suggestions</h5>

        {loadingSuggestions ? (
          <p className="text-center text-muted">Loading...</p>
        ) : suggestions.length === 0 ? (
          <p className="text-center text-muted">No suggestions</p>
        ) : (
          <div className="suggestions-list">
            {suggestions.map((user) => (
              <div
                key={user.id}
                className="suggestion-card d-flex align-items-center justify-content-between mb-3 p-2 rounded shadow-sm"
              >
                <div
                  className="d-flex align-items-center gap-2"
                  style={{ cursor: "pointer" }}
                  onClick={() => navigate(`/profile/${user.id}`)}
                >
                  <img
                    src={
                      user.avatar ||
                      `https://ui-avatars.com/api/?name=${user.username}`
                    }
                    alt="avatar"
                    className="rounded-circle"
                    width="40"
                    height="40"
                  />
                  <div>
                    <p className="mb-0 fw-semibold">{user.username}</p>
                  </div>
                </div>
                <button
                  className="btn btn-sm btn-outline-primary"
                  disabled={actionLoading.includes(user.id)}
                  onClick={() => handleFollow(user.id)}
                >
                  {actionLoading.includes(user.id) ? "Following..." : "Follow"}
                </button>
              </div>
            ))}
          </div>
        )}
      </aside>
    </div>
  );
}

export default Layout;
