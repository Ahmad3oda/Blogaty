import apiClient from "./apiClient";

// 🔗 Injected: GET /comments/blog/{blogId}?page=x&size=y
export const getCommentsByBlog = async (
  blogId: number,
  page: number = 0,
  size: number = 10
) => {
  const res = await apiClient.get(
    `/comments/blog/${blogId}?page=${page}&size=${size}`, {
      headers: { "Cache-Control": "no-cache" },
      params: { t: Date.now() }, // force fresh fetch
    }
  );
  return res.data;
};

// 🔗 Injected: Get /comments/{commentId}
export const getComment = async (commentId: number) => {
  const res = await apiClient.get(`/comments/${commentId}`, {
    headers: { "Cache-Control": "no-cache" },
    params: { t: Date.now() }, // force fresh fetch
  });
  return res.data;
};

// 🔗 Injected: POST /comments/{userId}/{blogId}
export const addComment = async (
  userId: number,
  blogId: number,
  content: string
) => {
  const res = await apiClient.post(`/comments/${userId}/${blogId}`, {
    content,
  });
  return res.data;
};

// 🔗 Injected: PATCH /comments/{commentId}
export const updateComment = async (commentId: number, content: string) => {
  const res = await apiClient.patch(`/comments/${commentId}`, { content });
  return res.data;
};

// 🔗 Injected: DELETE /comments/{commentId}
export const deleteComment = async (commentId: number) => {
  const res = await apiClient.delete(`/comments/${commentId}`);
  return res.data;
};
