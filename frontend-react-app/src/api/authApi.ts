import apiClient from "./apiClient";

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
}

// 🔗 Injected: POST /users/login
export const login = async (data: LoginRequest) => {
  const res = await apiClient.post("/users/login", data);
  localStorage.setItem("token", res.data.token);
  sessionStorage.setItem("userId", res.data.userId);
  sessionStorage.setItem("username", data.username);
  return res.data;
};

// 🔗 Injected: POST /users/register
export const register = async (data: RegisterRequest) => {
  const res = await apiClient.post("/users/register", data);
  return res.data;
};
