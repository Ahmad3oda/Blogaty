import apiClient from "./apiClient";

// Get Folowers and Following Count
export const getFollowersCount = async (userId: number) => {
  const res = await apiClient.get(`/followers/numbers/${userId}`);
  return res.data;
};

// Get the list of followers for a user
export const getFollowers = async (userId: number) => {
  const res = await apiClient.get(`/followers/${userId}`);
  return res.data;
};

// Get the list of followings for a user
export const getFollowings = async (userId: number) => {
  const res = await apiClient.get(`/followers/by/${userId}`);
  return res.data;
};

// Get the list of suggestions for a user
export const getSuggestions = async (userId: number) => {
  const res = await apiClient.get(`/followers/suggest/${userId}`);
  return res.data;
};

// Follow a user
export const followUser = async (followingId: number, followerId: number) => {
  const res = await apiClient.post(`/followers/${followingId}/${followerId}`);
  return res.data;
};

// Unfollow a user
export const unfollowUser = async (followingId: number, followerId: number) => {
  const res = await apiClient.delete(`/followers/${followingId}/${followerId}`);
  return res.data;
};

