import axios from 'axios';
import { User } from '@/types';

const API_URL = '/api';

export interface RegisterData {
  fullName: string;
  email: string;
  phoneNumber: string;
  companyName: string;
  password: string;
}

// Register a new user
export const register = async (userData: RegisterData) => {
  const response = await axios.post(`${API_URL}/auth/register`, userData);
  return response.data;
};

// Login user
export const login = async (email: string, password: string) => {
  const response = await axios.post(`${API_URL}/auth/login`, { email, password });
  const { token, user } = response.data;
  
  // Store token in localStorage
  localStorage.setItem('token', token);
  localStorage.setItem('user', JSON.stringify(user));
  
  return { token, user };
};

// Logout user
export const logout = async () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  
  // Optional: Call logout endpoint to invalidate token on server
  try {
    await axios.post(`${API_URL}/auth/logout`, {}, {
      headers: { Authorization: `Bearer ${getToken()}` }
    });
  } catch (error) {
    console.error('Error during logout:', error);
  }
};

// Get JWT token from localStorage
export const getToken = (): string | null => {
  if (typeof window === 'undefined') {
    return null; // Return null on server-side
  }
  return localStorage.getItem('token');
};

// Get current user from localStorage
export const getUser = () => {
  if (typeof window === 'undefined') {
    return null; // Return null on server-side
  }
  
  const userStr = localStorage.getItem('user');
  if (!userStr) return null;
  
  try {
    return JSON.parse(userStr) as User;
  } catch (error) {
    console.error('Error parsing user data:', error);
    return null;
  }
};

// Check if user is authenticated
export const checkUserAuthentication = async (): Promise<boolean> => {
  const token = getToken();
  
  if (!token) {
    return false;
  }
  
  try {
    // Verify token with backend
    await axios.get(`${API_URL}/auth/verify`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    return true;
  } catch (error) {
    console.error('Authentication error:', error);
    logout(); // Clear invalid token
    return false;
  }
};