import React, { useEffect, useState } from "react";
import {
  getFollowers,
  getFollowings,
  getFollowersCount,
  unfollowUser,
} from "../api/followApi";
import "../styles/FollowPage.css";

interface User {
  id: number;
  username: string;
  avatar?: string;
}

function FollowersSwitch() {
  const [followers, setFollowers] = useState<User[]>([]);
  const [followings, setFollowings] = useState<User[]>([]);
  const [view, setView] = useState<"followers" | "followings">("followers");
  const [loading, setLoading] = useState<boolean>(true);
  const [actionLoading, setActionLoading] = useState<number[]>([]);

  const userId = Number(sessionStorage.getItem("userId")); // read userId from sessionStorage

  // Fetch followers or followings
  const fetchData = async () => {
    setLoading(true);
    try {
      if (view === "followers") {
        const data = await getFollowers(userId);
        setFollowers(data.followers || []); // <-- extract followers array
      } else {
        const data = await getFollowings(userId);
        setFollowings(data.followers || []); // <-- extract followers array
      }
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleUnfollow = async (followingId: number) => {
    try {
      setActionLoading((prev) => [...prev, followingId]);
      await unfollowUser(userId, followingId);
      setFollowings((prev) => prev.filter((f) => f.id !== followingId));
    } catch (err) {
      console.error(err);
    } finally {
      setActionLoading((prev) => prev.filter((id) => id !== followingId));
    }
  };

  useEffect(() => {
    fetchData();
  }, [view]);

  const dataToShow = view === "followers" ? followers : followings;

  return (
    <div className="container mt-3 mb-4">
      {/* Toggle buttons */}
      <div className="mb-3 d-flex gap-2">
        <button
          className={`btn btn-${view === "followers" ? "primary" : "outline-primary"}`}
          onClick={() => setView("followers")}
        >
          Followers
        </button>
        <button
          className={`btn btn-${view === "followings" ? "primary" : "outline-primary"}`}
          onClick={() => setView("followings")}
        >
          Followings
        </button>
      </div>

      {loading ? (
        <div className="text-center py-5">Loading...</div>
      ) : dataToShow.length === 0 ? (
        <div className="text-center py-5">No {view} yet</div>
      ) : (
        <div className="col-lg-9 mt-4 mt-lg-0">
          <div className="row">
            <div className="col-md-12">
              <div className="user-dashboard-info-box table-responsive mb-0 bg-white p-4 shadow-sm">
                <table className="table manage-candidates-top mb-0">
                  <thead>
                    <tr>
                      <th>{view === "followers" ? "Follower" : "Following"}</th>
                      <th className="text-center">Followers</th>
                      <th className="action text-right">Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {dataToShow.map((user) => (
                      <tr key={user.id} className="followers-list">
                        <td className="title d-flex align-items-center gap-3">
                          <a
                            href={`/profile/${user.id}`}
                            className="d-flex align-items-center gap-3 text-decoration-none text-dark"
                          >
                            <div className="thumb">
                              <img
                                className="img-fluid rounded-circle"
                                src={
                                  user.avatar ||
                                  `https://ui-avatars.com/api/?name=${user.username}`
                                }
                                alt={user.username}
                                width={50}
                                height={50}
                              />
                            </div>
                            <div className="follower-details">
                              <h5 className="mb-0">@{user.username}</h5>
                            </div>
                          </a>
                        </td>
                        <td className="text-center">
                          <FollowerCount userId={user.id} />
                        </td>
                        <td className="text-right">
                          {view === "followings" ? (
                            <button
                              className="btn btn-outline-danger btn-sm"
                              disabled={actionLoading.includes(user.id)}
                              onClick={() => handleUnfollow(user.id)}
                            >
                              {actionLoading.includes(user.id)
                                ? "Unfollowing..."
                                : "Unfollow"}
                            </button>
                          ) : (
                            <a
                              href={`/profile/${user.id}`}
                              className="btn btn-outline-primary btn-sm"
                            >
                              View Profile
                            </a>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

// Separate component for fetching and displaying followers count per user
function FollowerCount({ userId }: { userId: number }) {
  const [count, setCount] = useState<number>(0);

  useEffect(() => {
    const fetchCount = async () => {
      try {
        const res = await getFollowersCount(userId);
        setCount(res.followersCount || 0);
      } catch (err) {
        console.error(err);
      }
    };
    fetchCount();
  }, [userId]);

  return <span>{count}</span>;
}

export default FollowersSwitch;
