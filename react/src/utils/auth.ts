import axios from 'axios';
import { LoginRequest, LoginResponse, RegisterRequest } from '@/models/Auth';

export const login = async (payload: LoginRequest): Promise<LoginResponse> => {
  try {
    const response = await axios.post<LoginResponse>("/api/auth/login", payload);
    return response.data;
  } catch (error) {
    console.error("Login failed:", error);
    throw new Error("Login failed. Please try again!");
  }
};

export const register = async (payload: RegisterRequest): Promise<void> => {
  try {
    await axios.post("/api/auth/register", payload);
  } catch (error) {
    console.error("Register failed:", error);
    throw new Error("Register failed. Please try again!");
  }
};

export const getUserRole = (): "USER" | "ADMIN" | null => {
  const user = localStorage.getItem("user");
  if (user) {
    const parsedUser = JSON.parse(user);
    return parsedUser.role; // Trả về role từ localStorage
  }
  return null; // Nếu không tìm thấy người dùng trong localStorage
};
