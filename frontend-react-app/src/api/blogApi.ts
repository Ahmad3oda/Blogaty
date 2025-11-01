import apiClient from "./apiClient";

// 🔗 Injected: GET /blogs?page=x&size=y
export const getBlogs = async (page: number = 0, size: number = 5) => {
  const res = await apiClient.get(`/blogs?page=${page}&size=${size}`);
  return res.data;
};

// 🔗 Injected: GET /blogs/{blogId}
export const getBlogById = async (blogId: number) => {
  const res = await apiClient.get(`/blogs/${blogId}`);
  return res.data;
};


// 🔗 Injected: GET /blogs/{blogId}
export const getBlogByUser = async (userId: number) => {
  const res = await apiClient.get(`/blogs/user/${userId}`);
  return res.data;
};


export const addBlog = async (userId: number, content: string) => {
  const res = await apiClient.post(`/blogs/user/${userId}`, { content });
  return res.data;
};
