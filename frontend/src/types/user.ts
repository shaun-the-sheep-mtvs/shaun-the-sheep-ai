export interface User {
  id: string;
  name: string;
  username: string;
  email: string;
  password: string;
  role: 'user' | 'admin';
  orders: number[];
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface SignupRequest {
  username: string;
  email: string;
  password: string;
} 