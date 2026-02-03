import axios from "axios";

const apiBase = process.env.NEXT_PUBLIC_API_BASE || "http://localhost:8080";

export const apiClient = axios.create({
  baseURL: apiBase
});

apiClient.interceptors.request.use((config) => {
  if (typeof window !== "undefined") {
    const token = window.localStorage.getItem("taronote_token");
    if (token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

export default apiClient;
