import apiClient from "./apiClient";

export const getUser = async (userId: number) => {
  const res = await apiClient.get(`users/${userId}`);
  return res.data;
};
