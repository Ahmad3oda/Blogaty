import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getUserBookmarks } from "../api/bookmarkApi";

interface Blog {
  blogId: number;
  content: string;
  date: string;
  votes: number;
  comments: number;
  user: {
    id: number;
    username: string;
  };
}

function BookmarksPage() {
  const [bookmarks, setBookmarks] = useState<Blog[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const userId = Number(localStorage.getItem("userId") || sessionStorage.getItem("userId"));

  useEffect(() => {
    const fetchBookmarks = async () => {
      if (!userId) return;
      try {
        setLoading(true);
        const data = await getUserBookmarks(userId);
        setBookmarks(data.bookmarkedBlogs || []);
      } catch (err) {
        console.error("Failed to load bookmarks", err);
      } finally {
        setLoading(false);
      }
    };
    fetchBookmarks();
  }, [userId]);

  return (
    <div className="container mt-4">
      <h3 className="mb-4">🔖 Your Bookmarks</h3>
      {loading ? (
        <p className="text-muted">Loading bookmarks...</p>
      ) : bookmarks.length === 0 ? (
        <div className="card p-4 text-center text-muted">
          <p className="mb-0">No bookmarks saved yet.</p>
        </div>
      ) : (
        <div className="d-flex flex-column gap-3">
          {bookmarks.map((b) => (
            <div
              key={b.blogId}
              className="card shadow-sm p-3"
              style={{ cursor: "pointer" }}
              onClick={() => navigate(`/blogs/${b.blogId}`)}
            >
              <h5 className="mb-1 text-primary">@{b.user?.username || "Unknown"}</h5>
              <p className="mb-2">{b.content}</p>
              <div className="text-muted small">
                {new Date(b.date).toLocaleDateString()} • {b.votes} votes • {b.comments} comments
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default BookmarksPage;
