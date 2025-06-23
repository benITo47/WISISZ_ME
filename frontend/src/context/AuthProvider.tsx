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
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoggedIn, setIsLoggedIn] = useState(null);

  const logOut = async () => {
    try {
      await api.post("/auth/logout");
    } catch (err) {
    } finally {
      setUser(null);
      setToken(null);

      {/*
       //@ts-ignore */}
      setIsLoggedIn(false);
    }
  };

  useEffect(() => {
    const tryInitialRefresh = async () => {
      console.log("[auth] 🌅 Trying initial refresh...");
      try {
        const response = await api.post("/auth/refresh");
        const newAccessToken = response.data.accessToken;
        setToken(newAccessToken);

        {/*
       //@ts-ignore */}
        setIsLoggedIn(true);

        {/*
       //@ts-ignore */}
      } catch (e) {
        setToken(null);

        {/*
       //@ts-ignore */}
        setIsLoggedIn(false);
      }
    };

    tryInitialRefresh();
  }, []);

  // 👤 Fetch user profile after token is available
  useEffect(() => {
    if (!token) return;

    const fetchMe = async () => {
      try {
        const response = await api.get("/me/profile");
        if (response.status === 200) {
          setUser(response.data);
          {/*
       //@ts-ignore */}
          setIsLoggedIn(true);
        }
      } catch (err) {
        // console.warn("[auth] ❌ Failed to fetch profile", err);
        setUser(null);
        {/*
       //@ts-ignore */}
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
        // console.log(`[auth] 📨 Added Authorization to ${cfg.url}`);
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
          console.warn("[auth] 🔁 Token expired – trying refresh...");
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
    /*
    //@ts-ignore */
    < AuthContext.Provider
      value={{
        user,
        token,
        /*@ts-ignore */
        isLoggedIn,
        logOut,
        setUser,
        setToken,
        /*@ts-ignore */
        setIsLoggedIn,
      }
      }
    >
      {children}
    </AuthContext.Provider >
  );
};

export default AuthProvider;
