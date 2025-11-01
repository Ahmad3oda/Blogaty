import { useState } from "react";
import { login, register } from "../api/authApi";

export function useAuth() {
  const [loading, setLoading] = useState(false);

  const handleLogin = async (username: string, password: string) => {
    setLoading(true);
    try {
      const data = await login({ username, password });
      setLoading(false);
      return data;
    } catch (err) {
      setLoading(false);
      throw err;
    }
  };

  const handleRegister = async (
    username: string,
    password: string
  ) => {
    setLoading(true);
    try {
      const data = await register({ username, password });
      setLoading(false);
      return data;
    } catch (err) {
      setLoading(false);
      throw err;
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
  };

  return { handleLogin, handleRegister, logout, loading };
}
