// src/contexts/AuthContext.tsx
import { createContext, useState } from "react";

type User = {
  id: number;
  fullName: string;
  avatar?: string;
  role: "ADMIN" | "USER" | "GUEST";
};

type AuthContextType = {
  user: User | null;
  accessToken: string | null;
  setUserLogin: (data: { user: User; accessToken: string }) => void;
  setUserLogout: () => void;
};

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(null);

  const setUserLogin = ({ user, accessToken }: { user: User; accessToken: string }) => {
    setUser(user);
    setAccessToken(accessToken);
  };

  const setUserLogout = () => {
    setUser(null);
    setAccessToken(null);
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
  };

  return (
    <AuthContext.Provider value={{ user, accessToken, setUserLogin, setUserLogout }}>
      {children}
    </AuthContext.Provider>
  );
};