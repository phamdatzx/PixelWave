export interface User {
	id: number;
	fullName: string;
	avatar: string;
	role: "USER" | "ADMIN";
}

export interface LoginResponse {
	accessToken: string;
	refreshToken: string;
	user: User;
}

export interface LoginRequest {
	email: string;
	password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  age: number;
}
