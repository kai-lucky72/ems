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

// Login user (manager or employee)
export const login = async (email: string, password: string) => {
  const response = await axios.post(`${API_URL}/auth/login`, { email, password });
  const { token, role } = response.data;
  
  // Store token and role in localStorage
  localStorage.setItem('token', token);
  localStorage.setItem('userRole', role);
  
  // Fetch user profile based on role
  try {
    const profileResponse = await axios.get(
      `${API_URL}/auth/profile`, 
      { headers: { Authorization: `Bearer ${token}` } }
    );
    
    const user = profileResponse.data;
    localStorage.setItem('user', JSON.stringify(user));
    
    return { token, role, user };
  } catch (error) {
    console.error('Error fetching user profile:', error);
    // Still return token and role even if profile fetch fails
    return { token, role, user: null };
  }
};

// Logout user
export const logout = async () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  localStorage.removeItem('userRole');
  
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

// Get user role from localStorage
export const getUserRole = (): string | null => {
  if (typeof window === 'undefined') {
    return null; // Return null on server-side
  }
  return localStorage.getItem('userRole');
};

// Check if user is a manager
export const isManager = (): boolean => {
  const role = getUserRole();
  return role === 'ROLE_MANAGER';
};

// Check if user is an employee
export const isEmployee = (): boolean => {
  const role = getUserRole();
  return role === 'ROLE_EMPLOYEE';
};

// Check if user is authenticated
export const checkUserAuthentication = async (): Promise<boolean> => {
  const token = getToken();
  
  if (!token) {
    return false;
  }
  
  try {
    // Verify token with backend using the check endpoint
    const response = await axios.get(`${API_URL}/auth/check`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    
    // Update role in case it changed
    if (response.data) {
      localStorage.setItem('userRole', response.data);
    }
    
    return true;
  } catch (error) {
    console.error('Authentication error:', error);
    logout(); // Clear invalid token
    return false;
  }
};