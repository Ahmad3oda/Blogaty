import apiClient from "./apiClient";

// ---------- BLOG VOTES ----------
export const getBlogVotes = async (blogId: number) => {
  const res = await apiClient.get(`votes/blog/${blogId}`);
  return res.data;
};

export const getBlogVote = async (userId: number, blogId: number) => {
  const res = await apiClient.get(`votes/blog/${userId}/${blogId}`);
  return res.data;
};

export const addBlogVote = async (userId: number, blogId: number, vote: string) => {
  const res = await apiClient.post(`votes/blog/${userId}/${blogId}`, { vote });
  return res.data;
};

export const updateBlogVote = async (userId: number, blogId: number, vote: string) => {
  const res = await apiClient.patch(`votes/blog/${userId}/${blogId}`, { vote });
  return res.data;
};

// ---------- COMMENT VOTES ----------
export const getCommentVotes = async (commentId: number) => {
  const res = await apiClient.get(`votes/comment/${commentId}`);
  return res.data;
};

export const getCommentVote = async (userId: number, commentId: number) => {
  const res = await apiClient.get(`votes/comment/${userId}/${commentId}`);
  return res.data;
};

// ✅ Add a new vote for a comment
export const addCommentVote = async (
  userId: number,
  commentId: number,
  vote: "up" | "down"
) => {
  const res = await apiClient.post(
    `/votes/comment/${userId}/${commentId}`,
    { vote } 
  );
  return res.data;
};

export const updateCommentVote = async (userId: number, commentId: number, vote: string) => {
  const res = await apiClient.patch(`votes/comment/${userId}/${commentId}`, { vote });
  return res.data;
};
