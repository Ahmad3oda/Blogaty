import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom"; // ✅ added useNavigate
import { getBlogById } from "../api/blogApi";
import apiClient from "../api/apiClient";
import { getCommentsByBlog, addComment, getComment } from "../api/commentApi";
import {
  addBlogVote,
  updateBlogVote,
  addCommentVote,
  updateCommentVote,
  getBlogVote,
  getCommentVote,
} from "../api/voteApi";
import "../styles/BlogView.css";

function BlogView() {
  const { blogId } = useParams();
  const navigate = useNavigate(); // ✅ for navigating to user profiles
  const [blog, setBlog] = useState<any>(null);
  const [comments, setComments] = useState<any[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [content, setContent] = useState("");
  const [loading, setLoading] = useState(false);
  const userId = Number(sessionStorage.getItem("userId"));

  // ---------- LOAD BLOG + USER VOTE ----------
  useEffect(() => {
    if (blogId) loadBlogWithVote();
  }, [blogId]);

  // ---------- LOAD COMMENTS ----------
  useEffect(() => {
    if (blogId) loadCommentsWithVotes();
  }, [page]);

  const loadBlogWithVote = async () => {
    const blogData = await getBlogById(Number(blogId));
    if (userId) {
      try {
        const userVote = await getBlogVote(userId, Number(blogId));
        blogData.userVote = userVote?.vote || null;
      } catch {
        blogData.userVote = null;
      }
    }
    setBlog(blogData);
  };

  const loadCommentsWithVotes = async () => {
    try {
      const data = await getCommentsByBlog(Number(blogId), page, 10);
      const newComments = data.content || data;

      if (newComments.length === 0) {
        setHasMore(false);
        return;
      }

      const commentsTrusted = await Promise.all(
        newComments.map(async (c: any) => {
          try {
            const trusted = await getComment(c.id);
            let userVoteVal: string | null = null;
            if (userId) {
              try {
                const userVote = await getCommentVote(userId, c.id);
                userVoteVal = userVote?.vote || null;
              } catch {
                userVoteVal = null;
              }
            }
            return { ...c, ...trusted, userVote: userVoteVal };
          } catch (err) {
            console.warn("Failed to fetch single comment for id", c.id, err);
            return c;
          }
        })
      );

      setComments((prev) => {
        const existingIds = new Set(prev.map((c) => c.id));
        const unique = commentsTrusted.filter(
          (c: any) => !existingIds.has(c.id)
        );
        return page === 0 ? commentsTrusted : [...prev, ...unique];
      });
    } catch (err) {
      console.error("loadCommentsWithVotes failed:", err);
    }
  };

  const handleBlogVote = async (vote: "up" | "down") => {
    if (!userId) return alert("You must be logged in to vote!");

    try {
      await updateBlogVote(userId, Number(blogId), vote);
    } catch (err: any) {
      if (err.response?.status === 404) {
        await addBlogVote(userId, Number(blogId), vote);
      } else {
        console.error("Blog vote error:", err);
        return alert("Failed to vote on blog.");
      }
    }
    await loadBlogWithVote();
  };

  const handleCommentVote = async (commentId: number, vote: "up" | "down") => {
    if (!userId) {
      alert("You must be logged in to vote!");
      return;
    }

    try {
      await addCommentVote(userId, commentId, vote);
    } catch (err: any) {
      const status = err?.response?.status;
      if (status === 409 || status === 400 || status === 404) {
        try {
          await updateCommentVote(userId, commentId, vote);
        } catch (err2) {
          console.error("updateCommentVote failed:", err2);
          alert("Failed to vote on comment.");
          return;
        }
      } else {
        console.error("Unexpected addCommentVote error:", err);
        alert("Failed to vote on comment.");
        return;
      }
    }

    let userVoteVal: string | null = null;
    try {
      const voteData = await getCommentVote(userId, commentId);
      userVoteVal = voteData?.vote || null;
    } catch {
      userVoteVal = null;
    }

    let refreshed: any;
    try {
      refreshed = await getComment(commentId);
    } catch {
      alert("Vote recorded but failed to refresh comment.");
      return;
    }

    refreshed.userVote = userVoteVal;

    setComments((prev) =>
      prev.map((c) => (c.id === commentId ? { ...refreshed } : c))
    );
  };

  const submitComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!content.trim()) return;

    try {
      setLoading(true);
      await addComment(userId, Number(blogId), content);
      setContent("");
      setComments([]);
      setPage(0);
      setHasMore(true);
      await loadBlogWithVote();
      await loadCommentsWithVotes();
    } catch {
      alert("Failed to add comment.");
    } finally {
      setLoading(false);
    }
  };

  // ✅ Navigate to user profile
  const goToProfile = (id: number) => {
    navigate(`/profile/${id}`);
  };

  return (
    <div className="blog-view-container">
      {/* ---------- BLOG CONTENT ---------- */}
      {blog && (
        <div className="blog-view-card shadow-sm">
          <h4
            className="text-primary mb-2 username-link"
            style={{ cursor: "pointer" }}
            onClick={() => blog.user?.id && goToProfile(blog.user.id)} // ✅ clickable username
          >
            @{blog.user?.username}
          </h4>
          <p className="blog-view-content">{blog.content}</p>

          <div className="blog-meta">
            {new Date(blog.date).toLocaleDateString()} • {blog.comments} comments
          </div>

          <div className="vote-controls mt-2">
            <button
              className={`vote-btn up ${blog.userVote === "up" ? "active" : ""}`}
              onClick={() => handleBlogVote("up")}
            >
              ▲
            </button>
            <span className="vote-count">{blog.votes}</span>
            <button
              className={`vote-btn down ${blog.userVote === "down" ? "active" : ""}`}
              onClick={() => handleBlogVote("down")}
            >
              ▼
            </button>
          </div>
        </div>
      )}

      {/* ---------- COMMENTS ---------- */}
      <h5 className="section-title mt-4 mb-3">Comments</h5>
      {comments.length === 0 && <p className="text-muted">No comments yet.</p>}

      <div className="comment-list">
        {comments.map((c) => (
          <div key={c.id} className="comment-card shadow-sm">
            <strong
              className="comment-author username-link"
              style={{ cursor: "pointer" }}
              onClick={() => c.author?.id && goToProfile(c.author.id)} // ✅ clickable username
            >
              @{c.author?.username || "Unknown"}
            </strong>
            <p className="comment-content">{c.content}</p>
            <div className="comment-meta">
              {new Date(c.date).toLocaleString()}
            </div>

            <div className="vote-controls mt-1">
              <button
                className={`vote-btn up ${c.userVote === "up" ? "active" : ""}`}
                onClick={() => handleCommentVote(c.id, "up")}
              >
                ▲
              </button>
              <span className="vote-count">{c.votes}</span>
              <button
                className={`vote-btn down ${c.userVote === "down" ? "active" : ""}`}
                onClick={() => handleCommentVote(c.id, "down")}
              >
                ▼
              </button>
            </div>
          </div>
        ))}
      </div>

      {hasMore && (
        <div className="text-center">
          <button
            className="btn btn-outline-secondary mt-3 px-4"
            onClick={() => setPage((p) => p + 1)}
          >
            Load More
          </button>
        </div>
      )}

      {/* ---------- ADD COMMENT ---------- */}
      <div className="comment-box-card shadow-sm mt-4">
        <form onSubmit={submitComment}>
          <textarea
            className="form-control"
            rows={3}
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="Write a comment..."
          />
          <button
            type="submit"
            className="btn btn-primary w-100 mt-2"
            disabled={loading}
          >
            {loading ? "Posting..." : "Comment"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default BlogView;
