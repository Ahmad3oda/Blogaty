import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  getFollowersCount,
  followUser,
  unfollowUser,
  getFollowers,
} from "../api/followApi";
import { getUser } from "../api/userApi";
import { getBlogByUser } from "../api/blogApi";
import "../styles/Profile.css";

function Profile() {
  const { userId: paramUserId } = useParams<{ userId: string }>();
  const loggedInUserId = Number(sessionStorage.getItem("userId"));
  const userId = paramUserId ? Number(paramUserId) : loggedInUserId;

  const [username, setUsername] = useState("");
  const [followersCount, setFollowersCount] = useState(0);
  const [followingsCount, setFollowingsCount] = useState(0);
  const [blogsCount, setBlogsCount] = useState(0);
  const [isFollowing, setIsFollowing] = useState(false);
  const [loading, setLoading] = useState(false);

  const isOwnProfile = userId === loggedInUserId;

  // 🟦 Fetch user data
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        setLoading(true);

        // 1️⃣ Fetch username
        const userData = await getUser(userId);
        setUsername(userData.username);

        // 2️⃣ Fetch counts
        const counts = await getFollowersCount(userId);
        setFollowersCount(counts.followers || 0);
        setFollowingsCount(counts.following || 0);

        // 3️⃣ Fetch blog count
        const blogs = await getBlogByUser(userId);
        setBlogsCount(blogs.length);

        // 4️⃣ Check if logged-in user follows this user
        if (!isOwnProfile) {
          const followersData = await getFollowers(userId);
          const followersList = followersData.followers || [];
          const followed = followersList.some(
            (f: any) => f.id === loggedInUserId
          );
          setIsFollowing(followed);
        }
      } catch (err) {
        console.error("Profile fetch error:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [userId]);

  // 🟧 Handle Follow / Unfollow Toggle
  const handleFollowToggle = async () => {
    if (isOwnProfile) return;

    setLoading(true);
    try {
      if (isFollowing) {
        await unfollowUser(userId, loggedInUserId);
        setFollowersCount((prev) => prev - 1);
      } else {
        await followUser(userId, loggedInUserId);
        setFollowersCount((prev) => prev + 1);
      }
      setIsFollowing(!isFollowing);
    } catch (err) {
      console.error("Follow/Unfollow failed:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="profile-page">
      <div className="profile-card">
        <div className="profile-image">
          <img
            src={`https://ui-avatars.com/api/?name=${username || "User"}&background=cccccc&color=ffffff`}
            alt="Profile"
          />
        </div>

        <div className="profile-info">
          <h2 className="profile-name">@{username || "Loading..."}</h2>

          <div className="profile-stats">
            <div className="stat">
              <span className="stat-number">
                {loading ? "..." : blogsCount}
              </span>
              <span className="stat-label">Blogs</span>
            </div>
            <div className="stat">
              <span className="stat-number">
                {loading ? "..." : followersCount}
              </span>
              <span className="stat-label">Followers</span>
            </div>
            <div className="stat">
              <span className="stat-number">
                {loading ? "..." : followingsCount}
              </span>
              <span className="stat-label">Following</span>
            </div>
          </div>

          <div className="profile-actions">
            {isOwnProfile ? (
              <button className="btn disabled-btn" disabled>
                This is you
              </button>
            ) : (
              <button
                className={`btn ${isFollowing ? "unfollow" : "follow"}`}
                onClick={handleFollowToggle}
                disabled={loading}
              >
                {loading
                  ? "..."
                  : isFollowing
                  ? "Unfollow"
                  : "Follow"}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Profile;
