import axios from 'axios';
import { getToken } from './auth';

const API_URL = '/api';

// Create axios instance with authorization header
const api = axios.create({
  baseURL: API_URL
});

// Add request interceptor to add token to every request
api.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Department API calls
export const fetchDepartments = async () => {
  const response = await api.get('/departments');
  return response.data;
};

export const createDepartment = async (department: { name: string; budget: number; budgetType: string }) => {
  const response = await api.post('/departments', department);
  return response.data;
};

export const updateDepartment = async (id: number, department: { name: string; budget: number; budgetType: string }) => {
  const response = await api.put(`/departments/${id}`, department);
  return response.data;
};

export const deleteDepartment = async (id: number) => {
  const response = await api.delete(`/departments/${id}`);
  return response.data;
};

// Employee API calls
export const fetchEmployees = async () => {
  const response = await api.get('/employees');
  return response.data;
};

export const createEmployee = async (employee: {
  name: string;
  email: string;
  phone: string;
  role: string;
  departmentId: number;
  contractType: string;
  startDate: string;
  endDate: string | null;
  isActive: boolean;
}) => {
  const response = await api.post('/employees', employee);
  return response.data;
};

export const updateEmployee = async (id: number, employee: {
  name: string;
  email: string;
  phone: string;
  role: string;
  departmentId: number;
  contractType: string;
  startDate: string;
  endDate: string | null;
  isActive: boolean;
}) => {
  const response = await api.put(`/employees/${id}`, employee);
  return response.data;
};

export const deleteEmployee = async (id: number) => {
  const response = await api.delete(`/employees/${id}`);
  return response.data;
};

export const updateEmployeeStatus = async (id: number, status: {
  isActive: boolean;
  inactiveFrom: string | null;
  inactiveTo: string | null;
}) => {
  const response = await api.patch(`/employees/${id}/status`, status);
  return response.data;
};

// Salary API calls
export const fetchSalaries = async () => {
  const response = await api.get('/salaries');
  return response.data;
};

export const fetchSalaryByEmployee = async (employeeId: number) => {
  const response = await api.get(`/salaries/employee/${employeeId}`);
  return response.data;
};

export const createSalary = async (salary: {
  employeeId: number;
  grossSalary: number;
  deductions: Array<{
    type: string;
    name: string;
    value: number;
    isPercentage: boolean;
  }>;
}) => {
  const response = await api.post('/salaries', salary);
  return response.data;
};

export const updateSalary = async (id: number, salary: {
  employeeId: number;
  grossSalary: number;
  deductions: Array<{
    id?: number;
    type: string;
    name: string;
    value: number;
    isPercentage: boolean;
  }>;
}) => {
  const response = await api.put(`/salaries/${id}`, salary);
  return response.data;
};

// Leave API calls
export const fetchLeaves = async () => {
  const response = await api.get('/leaves');
  return response.data;
};

export const createLeave = async (leave: {
  employeeId: number;
  startDate: string;
  endDate: string;
  reason: string;
}) => {
  const response = await api.post('/leaves', leave);
  return response.data;
};

export const updateLeaveStatus = async (id: number, status: { status: 'APPROVED' | 'DENIED' }) => {
  const response = await api.patch(`/leaves/${id}/status`, status);
  return response.data;
};

// Message API calls
export const fetchMessages = async () => {
  const response = await api.get('/messages');
  return response.data;
};

export const sendMessage = async (message: {
  employeeId: number;
  subject: string;
  content: string;
}) => {
  const response = await api.post('/messages', message);
  return response.data;
};

// Analytics API calls
export const fetchAnalytics = async () => {
  const response = await api.get('/analytics');
  return response.data;
};