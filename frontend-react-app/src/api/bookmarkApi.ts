import apiClient from "./apiClient";

export const getUserBookmarks = async (userId: number) => {
  const res = await apiClient.get(`/bookmarks/user/${userId}`);
  return res.data;
};

export const addBookmark = async (userId: number, blogId: number) => {
  const res = await apiClient.post(`/bookmarks/${userId}/${blogId}`);
  return res.data;
};

export const deleteBookmark = async (userId: number, blogId: number) => {
  const res = await apiClient.delete(`/bookmarks/${userId}/${blogId}`);
  return res.data;
};
