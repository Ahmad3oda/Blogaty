import apiClient from "./apiClient";

export async function getNotifications(userId: number) {
  const res = await apiClient.get(`notifications/user/${userId}`);
  return res.data;
}
