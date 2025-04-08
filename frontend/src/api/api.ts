import axios, { InternalAxiosRequestConfig } from "axios";

interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

const api = axios.create({
  baseURL: "localhost:8080/api",
  withCredentials: true,
});

export type { CustomAxiosRequestConfig }; // eksport opcjonalny
export default api;
