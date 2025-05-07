# API Endpoints Mapping

This document provides a comprehensive mapping between frontend API calls and backend endpoints to verify all functionality is properly connected.

## Authentication Endpoints

| Frontend API Call | Backend Endpoint | Status | Description |
|-------------------|------------------|--------|-------------|
| `register()` | `POST /api/auth/register` | ✓ Implemented | User registration with name, email, phone, company name, password |
| `login()` | `POST /api/auth/login` | ✓ Implemented | User login with email and password |
| `logout()` | `POST /api/auth/logout` | ✓ Implemented | User logout (invalidate token) |
| `checkUserAuthentication()` | `GET /api/auth/check` | ✓ Implemented | Verify JWT token validity |
| N/A | `POST /api/auth/activate` | ✓ Implemented | Employee account activation |
| N/A | `POST /api/auth/request-reset` | ✓ Implemented | Request password reset |
| N/A | `POST /api/auth/reset-password` | ✓ Implemented | Process password reset |

## Department Management Endpoints

| Frontend API Call | Backend Endpoint | Status | Description |
|-------------------|------------------|--------|-------------|
| `fetchDepartments()` | `GET /api/departments` | ✓ Implemented | Get all departments |
| N/A | `GET /api/departments/{id}` | ✓ Implemented | Get department by ID |
| `createDepartment()` | `POST /api/departments` | ✓ Implemented | Create new department |
| `updateDepartment()` | `PUT /api/departments/{id}` | ✓ Implemented | Update existing department |
| `deleteDepartment()` | `DELETE /api/departments/{id}` | ✓ Implemented | Delete department |

## Employee Management Endpoints

| Frontend API Call | Backend Endpoint | Status | Description |
|-------------------|------------------|--------|-------------|
| `fetchEmployees()` | `GET /api/employees` | ✓ Implemented | Get all employees |
| N/A | `GET /api/employees/{id}` | ✓ Implemented | Get employee by ID |
| `createEmployee()` | `POST /api/employees` | ✓ Implemented | Create new employee |
| `updateEmployee()` | `PUT /api/employees/{id}` | ✓ Implemented | Update existing employee |
| `deleteEmployee()` | `DELETE /api/employees/{id}` | ✓ Implemented | Delete employee |
| `updateEmployeeStatus()` | `PATCH /api/employees/{id}/status` | ✓ Implemented | Update employee active status |

## Salary Management Endpoints

| Frontend API Call | Backend Endpoint | Status | Description |
|-------------------|------------------|--------|-------------|
| `fetchSalaries()` | `GET /api/salaries` | ✓ Implemented | Get all salaries |
| `fetchSalaryByEmployee()` | `GET /api/salaries/employee/{employeeId}` | ✓ Implemented | Get salary by employee ID |
| `createSalary()` | `POST /api/salaries` | ✓ Implemented | Create new salary record |
| `updateSalary()` | `PUT /api/salaries/{id}` | ✓ Implemented | Update existing salary record |

## Leave Management Endpoints

| Frontend API Call | Backend Endpoint | Status | Description |
|-------------------|------------------|--------|-------------|
| `fetchLeaves()` | `GET /api/leaves` | ✓ Implemented | Get all leaves |
| N/A | `GET /api/leaves/employee/{employeeId}` | ✓ Implemented | Get leaves by employee ID |
| `createLeave()` | `POST /api/leaves` | ✓ Implemented | Create new leave request |
| `updateLeaveStatus()` | `PATCH /api/leaves/{id}/status` | ✓ Implemented | Update leave status (approve/deny) |

## Messaging System Endpoints

| Frontend API Call | Backend Endpoint | Status | Description |
|-------------------|------------------|--------|-------------|
| `fetchMessages()` | `GET /api/messages` | ✓ Implemented | Get all messages |
| N/A | `GET /api/messages/employee/{employeeId}` | ✓ Implemented | Get messages by employee ID |
| `sendMessage()` | `POST /api/messages` | ✓ Implemented | Send a new message |

## Analytics Endpoints

| Frontend API Call | Backend Endpoint | Status | Description |
|-------------------|------------------|--------|-------------|
| `fetchAnalytics()` | `GET /api/analytics` | ✓ Implemented | Get system analytics data |

## Frontend Routes and Backend Dependencies

| Frontend Route | Required Backend Endpoints | Purpose |
|----------------|----------------------------|---------|
| `/` | None (Static landing page) | Landing page |
| `/login` | `POST /api/auth/login` | User login page |
| `/register` | `POST /api/auth/register` | User registration page |
| `/activate` | `POST /api/auth/activate` | Account activation page |
| `/reset-password` | `POST /api/auth/reset-password` | Password reset page |
| `/dashboard` | `GET /api/auth/check` | Main dashboard |
| `/dashboard/profile` | `GET /api/auth/profile` | User profile |
| `/dashboard/departments` | Department CRUD endpoints | Department management |
| `/dashboard/employees` | Employee CRUD endpoints | Employee management |
| `/dashboard/salary` | Salary CRUD endpoints | Salary management for managers |
| `/dashboard/my-salary` | `GET /api/salaries/employee/{id}` | Personal salary view |
| `/dashboard/leave` | Leave management endpoints | Leave management for managers |
| `/dashboard/my-leave` | `GET /api/leaves/employee/{id}` | Personal leave management |
| `/dashboard/messages` | `GET /api/messages` | View messages |
| `/dashboard/messaging` | `POST /api/messages` | Send messages |
| `/dashboard/analytics` | `GET /api/analytics` | Analytics dashboard |

## Conclusion

The frontend and backend are well aligned with all required API endpoints properly documented and implemented. The backend README provides detailed information about request/response formats, and the frontend makes API calls using the expected endpoints.

Key observations:

1. **Complete API Coverage**: All frontend API calls have corresponding backend endpoints documented in the backend README.

2. **Authentication Mechanism**: JWT-based authentication is consistently implemented across both components.

3. **CRUD Operations**: Complete CRUD operations for departments, employees, salaries, and leaves are available.

4. **Role-Based Access**: The backend supports role-based access control matching frontend requirements.

5. **Analytics Data**: The backend provides comprehensive analytics data needed by the frontend dashboard.

For deployment, ensure the frontend's `next.config.js` points to the correct backend URL and CORS is properly configured on the backend to accept requests from the frontend domain.