// User interfaces
export interface User {
  id: number;
  fullName: string;
  email: string;
  phoneNumber: string;
  companyName: string;
}

// Department interfaces
export interface Department {
  id: number;
  name: string;
  budget: number;
  budgetType: 'MONTHLY' | 'YEARLY';
  currentExpenses: number;
}

export interface DepartmentFormData {
  name: string;
  budget: number;
  budgetType: 'MONTHLY' | 'YEARLY';
}

// Employee interfaces
export interface Employee {
  id: number;
  name: string;
  email: string;
  phone: string;
  role: string;
  departmentId: number;
  departmentName: string;
  contractType: 'FULL_TIME' | 'PART_TIME' | 'REMOTE';
  startDate: string;
  endDate: string | null;
  isActive: boolean;
  inactiveFrom: string | null;
  inactiveTo: string | null;
}

export interface EmployeeFormData {
  name: string;
  email: string;
  phone: string;
  role: string;
  departmentId: number;
  contractType: 'FULL_TIME' | 'PART_TIME' | 'REMOTE';
  startDate: string;
  endDate: string | null;
  isActive: boolean;
}

export interface EmployeeStatusFormData {
  isActive: boolean;
  inactiveFrom: string | null;
  inactiveTo: string | null;
}

// Salary interfaces
export interface Deduction {
  id?: number;
  type: 'TAX' | 'INSURANCE' | 'CUSTOM';
  name: string;
  value: number;
  isPercentage: boolean;
}

export interface Salary {
  id: number;
  employeeId: number;
  employeeName: string;
  departmentName: string;
  grossSalary: number;
  netSalary: number;
  deductions: Deduction[];
}

export interface SalaryFormData {
  employeeId: number;
  grossSalary: number;
  deductions: Deduction[];
}

// Leave interfaces
export interface Leave {
  id: number;
  employeeId: number;
  employeeName: string;
  startDate: string;
  endDate: string;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'DENIED';
  createdAt: string;
}

export interface LeaveFormData {
  employeeId: number;
  startDate: string;
  endDate: string;
  reason: string;
}

export interface LeaveStatusFormData {
  status: 'APPROVED' | 'DENIED';
}

// Message interfaces
export interface Message {
  id: number;
  employeeId: number;
  employeeName: string;
  subject: string;
  content: string;
  sentAt: string;
  status: 'SENT' | 'FAILED';
}

export interface MessageFormData {
  employeeId: number;
  subject: string;
  content: string;
}

// Analytics interfaces
export interface DepartmentBudgetAnalytics {
  labels: string[];
  actual: number[];
  budget: number[];
}

export interface SalaryAnalytics {
  totalGross: number;
  totalNet: number;
  averageSalary: number;
  departmentSalaries: {
    department: string;
    totalSalary: number;
  }[];
}

export interface DistributionAnalytics {
  labels: string[];
  counts: number[];
}

export interface EmployeeTimelineAnalytics {
  months: string[];
  active: number[];
  inactive: number[];
}

export interface AnalyticsData {
  departmentBudget: DepartmentBudgetAnalytics;
  salaryData: SalaryAnalytics;
  employeeDistribution: DistributionAnalytics;
  leaveStatus: DistributionAnalytics;
  roleDistribution: DistributionAnalytics;
  contractTypeDistribution: DistributionAnalytics;
  employeeTimeline: EmployeeTimelineAnalytics;
}