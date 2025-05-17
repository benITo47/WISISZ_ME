import {
  createContext,
  ReactNode,
  useContext,
  useEffect,
  useLayoutEffect,
  useState,
} from "react";

import api, { CustomAxiosRequestConfig } from "../api/api";

interface User {
  fname: string;
  lname: string;
  id: number;
  emailAddr: string;
}

interface AuthContextType {
  isLoggedIn: boolean;
  user: User | null;
  token: string | null;
  logOut: () => void;
  setUser: (user: User | null) => void;
  setToken: (token: string | null) => void;
  setIsLoggedIn: (v: boolean) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const authContext = useContext(AuthContext);
  if (!authContext) {
    throw new Error("useAuth must be used within a AuthProvider");
  }
  return authContext;
};

const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);

  const logOut = async () => {
    console.log("[auth] 🚪 Logging out...");
    try {
      await api.post("/auth/logout");
    } catch {
      console.warn("[auth] ⚠️ Logout request failed");
    } finally {
      setUser(null);
      setToken(null);
      setIsLoggedIn(false);
    }
  };

  useEffect(() => {
    if (!token) return;

    const fetchMe = async () => {
      console.log("[auth] 🔄 Fetching /me/profile...");
      try {
        const response = await api.get("/me/profile");
        if (response.status === 200) {
          console.log("[auth] ✅ /me/profile success:", response.data);
          setUser(response.data);
          setIsLoggedIn(true);
        }
      } catch (err) {
        console.warn("[auth] ❌ /me/profile failed", err);
        setUser(null);
        setIsLoggedIn(false);
      }
    };

    fetchMe();
  }, [token]);

  useLayoutEffect(() => {
    const requestInterceptor = api.interceptors.request.use((config) => {
      const cfg = config as CustomAxiosRequestConfig;

      if (!cfg._retry && token) {
        cfg.headers.Authorization = `Bearer ${token}`;
        console.log(`[auth] 📨 Added Authorization header to ${cfg.url}`);
      }

      return cfg;
    });

    return () => {
      api.interceptors.request.eject(requestInterceptor);
    };
  }, [token]);

  useLayoutEffect(() => {
    const responseInterceptor = api.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config as CustomAxiosRequestConfig;

        const shouldTryRefresh =
          error.response &&
          [401, 403].includes(error.response.status) &&
          !originalRequest._retry &&
          originalRequest.url !== "/auth/refresh";

        if (shouldTryRefresh) {
          console.warn("[auth] 🔁 Trying token refresh...");

          try {
            const response = await api.post("/auth/refresh");
            const newAccessToken = response.data.accessToken;

            setToken(newAccessToken);
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            originalRequest._retry = true;

            return api(originalRequest);
          } catch (refreshError) {
            console.warn("[auth] ❌ Refresh failed – logging out");
            await logOut();
            return Promise.reject(refreshError);
          }
        }

        return Promise.reject(error);
      },
    );

    return () => {
      api.interceptors.response.eject(responseInterceptor);
    };
  }, [token]);

  return (
    <AuthContext.Provider
      value={{
        token,
        isLoggedIn,
        user,
        logOut,
        setUser,
        setToken,
        setIsLoggedIn,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export default AuthProvider;
