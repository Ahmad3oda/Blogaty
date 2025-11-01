import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getBlogs, addBlog, getBlogById, getBlogByUser } from "../api/blogApi";
import { addBlogVote, updateBlogVote, getBlogVote } from "../api/voteApi";
import "../styles/BlogList.css";

interface BlogListProps {
  showCreatePost?: boolean; // optional, default true
  userId?: number; // optional, filter blogs by user
}

function BlogList({ showCreatePost = true, userId }: BlogListProps) {
  const [blogs, setBlogs] = useState<any[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [content, setContent] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const loggedInUserId = Number(sessionStorage.getItem("userId"));

  useEffect(() => {
    loadBlogs();
  }, [page, userId]);

  const loadBlogs = async () => {
    try {
      const data = await getBlogs(page, 10, userId); // filter by userId if provided
      const newBlogs = data.content || data;
      if (newBlogs.length === 0) {
        setHasMore(false);
        return;
      }

      if (loggedInUserId) {
        const blogsWithVote = await Promise.all(
          newBlogs.map(async (b: any) => {
            try {
              const voteData = await getBlogVote(loggedInUserId, b.blogId);
              return { ...b, userVote: voteData.vote || null };
            } catch {
              return { ...b, userVote: null };
            }
          })
        );
        setBlogs((prev) =>
          page === 0 ? blogsWithVote : [...prev, ...blogsWithVote]
        );
      } else {
        setBlogs((prev) => (page === 0 ? newBlogs : [...prev, ...newBlogs]));
      }
    } catch (err) {
      console.error("Error loading blogs:", err);
    }
  };

  const handlePost = async (e: React.FormEvent) => {
    if (!showCreatePost) return;
    e.preventDefault();
    if (!content.trim()) return;

    try {
      setLoading(true);
      const username = sessionStorage.getItem("username");
      const newBlog = await addBlog(loggedInUserId, content);
      const blogWithUser = {
        ...newBlog,
        user: { username: username || "You" },
        userVote: null,
      };
      setBlogs((prev) => [blogWithUser, ...prev]);
      setContent("");
    } catch (err) {
      alert("Failed to post blog.");
    } finally {
      setLoading(false);
    }
  };

  const handleVote = async (blogId: number, isUpvote: boolean) => {
    if (!loggedInUserId) return alert("Please login first.");
    const vote: "up" | "down" = isUpvote ? "up" : "down";
    const blog = blogs.find((b) => b.blogId === blogId);
    if (!blog) return;

    try {
      if (blog.userVote === vote) {
        await updateBlogVote(loggedInUserId, blogId, "none");
      } else {
        try {
          await addBlogVote(loggedInUserId, blogId, vote);
        } catch (err: any) {
          if ([409, 400, 404].includes(err?.response?.status)) {
            await updateBlogVote(loggedInUserId, blogId, vote);
          } else throw err;
        }
      }

      const updatedBlog = await getBlogById(blogId);
      const voteData = await getBlogVote(loggedInUserId, blogId);

      setBlogs((prev) =>
        prev.map((b) =>
          b.blogId === blogId
            ? { ...updatedBlog, userVote: voteData.vote || null }
            : b
        )
      );
    } catch (err) {
      console.error("Vote error:", err);
      alert("Failed to update blog vote.");
    }
  };

  const loadMore = () => {
    if (hasMore) setPage((prev) => prev + 1);
  };

  // ✅ navigate to user profile
  const goToProfile = (id: number) => {
    navigate(`/profile/${id}`);
  };

  return (
    <div className="blog-list-container">
      {showCreatePost && (
        <div className="create-post-card shadow-sm">
          <form onSubmit={handlePost}>
            <textarea
              className="form-control"
              placeholder="What's on your mind?"
              value={content}
              onChange={(e) => setContent(e.target.value)}
              rows={3}
            />
            <button
              type="submit"
              className="btn btn-primary mt-2 w-100"
              disabled={loading}
            >
              {loading ? "Posting..." : "Post"}
            </button>
          </form>
        </div>
      )}

      {/* Blog Cards */}
      <div className="blog-grid">
        {blogs.map((b) => (
          <div key={b.blogId} className="blog-card">
            <div className="blog-card-body">
              {/* ✅ clickable username */}
              <h5
                className="blog-author username-link"
                style={{ cursor: "pointer" }}
                onClick={() => b.user?.id && goToProfile(b.user.id)}
              >
                @{b.user?.username || "Unknown"}
              </h5>

              <p
                className="blog-content"
                onClick={() => navigate(`/blogs/${b.blogId}`)}
                style={{ cursor: "pointer" }}
              >
                {b.content}
              </p>
            </div>

            <div className="blog-meta">
              {new Date(b.date).toLocaleDateString()} • {b.comments} comments
              <div className="vote-controls">
                <button
                  className={`vote-btn up ${
                    b.userVote === "up" ? "active" : ""
                  }`}
                  onClick={(e) => {
                    e.stopPropagation();
                    handleVote(b.blogId, true);
                  }}
                >
                  ▲
                </button>
                <span className="vote-count">{b.votes}</span>
                <button
                  className={`vote-btn down ${
                    b.userVote === "down" ? "active" : ""
                  }`}
                  onClick={(e) => {
                    e.stopPropagation();
                    handleVote(b.blogId, false);
                  }}
                >
                  ▼
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {hasMore && (
        <button className="btn btn-load-more" onClick={loadMore}>
          Load More
        </button>
      )}
      {!hasMore && <p className="no-more">No more blogs to load.</p>}
    </div>
  );
}

export default BlogList;
