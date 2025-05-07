# Employee Management System - Frontend

This document outlines the frontend application for the Employee Management System, built using Next.js, TypeScript, and Tailwind CSS.

## Overview

The frontend provides a responsive user interface for the Employee Management System, with role-based access control for managers and employees. It includes features for authentication, employee management, department management, salary management, leave management, messaging, and analytics.

## Project Structure

```
frontend/
├── components/           # Reusable UI components
│   ├── auth/            # Authentication-related components
│   │   └── ProtectedRoute.tsx   # Route protection by role
│   ├── Layout.tsx        # Application layout with Navbar and Sidebar
│   ├── Navbar.tsx        # Top navigation bar
│   ├── Sidebar.tsx       # Side navigation menu
│   ├── AnalyticChart.tsx # Charts for analytics dashboard
│   ├── DepartmentForm.tsx # Department creation/edit form
│   ├── EmployeeForm.tsx  # Employee creation/edit form
│   ├── LeaveForm.tsx     # Leave request form
│   ├── MessageForm.tsx   # Messaging form
│   └── SalaryForm.tsx    # Salary management form
├── lib/                  # Utility functions and API calls
│   ├── api.ts            # API function wrappers
│   └── auth.ts           # Authentication utilities
├── pages/                # Application pages
│   ├── dashboard/        # Protected dashboard pages
│   │   ├── analytics.tsx # Analytics dashboard
│   │   ├── departments.tsx # Department management
│   │   ├── employees.tsx # Employee management
│   │   ├── index.tsx     # Main dashboard
│   │   ├── leave.tsx     # Leave management
│   │   ├── messages.tsx  # Messaging inbox
│   │   ├── messaging.tsx # Send messages
│   │   ├── my-leave.tsx  # Employee leave requests
│   │   ├── my-salary.tsx # Employee salary view
│   │   ├── profile.tsx   # User profile
│   │   └── salary.tsx    # Salary management
│   ├── _app.tsx          # App wrapper with global styles
│   ├── activate.tsx      # Account activation page
│   ├── index.tsx         # Landing page
│   ├── login.tsx         # Login page
│   ├── register.tsx      # Registration page
│   └── reset-password.tsx # Password reset page
└── types/                # TypeScript type definitions
    └── index.ts          # Common types used across the application
```

## Authentication

Authentication is handled via JWT (JSON Web Tokens). The frontend stores the token in localStorage and includes it in all authenticated API requests.

### Authentication Flow

1. **Registration**: Managers can register through the `/register` page
2. **Login**: Users can log in through the `/login` page
3. **Employee Activation**: Employees receive an invitation email with a link to the `/activate` page where they set their password
4. **Password Reset**: Users can request a password reset through the `/reset-password` page

The `ProtectedRoute` component is used to protect routes based on the user's role:

```tsx
<ProtectedRoute requiredRole="MANAGER">
  <DepartmentsPage />
</ProtectedRoute>
```

## API Integration

The `lib/api.ts` file contains wrapper functions for making API calls to the backend. Here are examples of how to use them:

### Authentication API Examples

```typescript
// Registration
import { register } from '@/lib/auth';

const handleRegister = async (userData) => {
  try {
    const result = await register(userData);
    // Handle successful registration
  } catch (error) {
    // Handle error
  }
};

// Login
import { login } from '@/lib/auth';

const handleLogin = async (email, password) => {
  try {
    const result = await login(email, password);
    // Handle successful login
  } catch (error) {
    // Handle error
  }
};
```

### Entity Management API Examples

```typescript
// Fetch departments
import { fetchDepartments } from '@/lib/api';

const loadDepartments = async () => {
  try {
    const departments = await fetchDepartments();
    // Use departments data
  } catch (error) {
    // Handle error
  }
};

// Create employee
import { createEmployee } from '@/lib/api';

const handleCreateEmployee = async (employeeData) => {
  try {
    const newEmployee = await createEmployee(employeeData);
    // Handle successful creation
  } catch (error) {
    // Handle error
  }
};
```

### API Endpoints Used

The frontend connects to the following backend API endpoints:

#### Authentication
- `POST /api/auth/register` - Register a new manager
- `POST /api/auth/login` - Login to an existing account
- `POST /api/auth/activate` - Activate employee account
- `POST /api/auth/request-reset` - Request password reset
- `POST /api/auth/reset-password` - Reset password with token

#### Department Management
- `GET /api/departments` - Get all departments
- `GET /api/departments/{id}` - Get department by ID
- `POST /api/departments` - Create department
- `PUT /api/departments/{id}` - Update department
- `DELETE /api/departments/{id}` - Delete department

#### Employee Management
- `GET /api/employees` - Get all employees
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees` - Create employee
- `PUT /api/employees/{id}` - Update employee
- `PATCH /api/employees/{id}/status` - Update employee status
- `DELETE /api/employees/{id}` - Delete employee

#### Salary Management
- `GET /api/salaries` - Get all salaries
- `GET /api/salaries/employee/{employeeId}` - Get salary by employee ID
- `POST /api/salaries` - Create salary
- `PUT /api/salaries/{id}` - Update salary

#### Leave Management
- `GET /api/leaves` - Get all leaves
- `GET /api/leaves/employee/{employeeId}` - Get employee leaves
- `POST /api/leaves` - Create leave request
- `PATCH /api/leaves/{id}/status` - Update leave status

#### Messaging
- `GET /api/messages` - Get all messages
- `GET /api/messages/employee/{employeeId}` - Get employee messages
- `POST /api/messages` - Send message

#### Analytics
- `GET /api/analytics` - Get system analytics

## Role-Based Access

The application implements role-based access control with two roles:

1. **Manager**: Has full access to the system. Can manage departments, employees, salaries, leave requests, and view analytics.
2. **Employee**: Has limited access to personal information, salary details, leave requests, and messages.

The sidebar navigation dynamically adjusts based on the user's role:

### Manager Navigation
- Dashboard
- Departments
- Employees
- Salary Management
- Leave Management
- Messaging
- Analytics

### Employee Navigation
- Dashboard
- Profile
- My Salary
- My Leave
- Messages

## Connecting to the Backend

To connect the frontend to the backend:

1. Ensure the backend API is running at the expected URL (default: `http://localhost:8080`)
2. Configure the API base URL in the `next.config.js` file:

```js
module.exports = {
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'http://localhost:8080/api/:path*',
      },
    ];
  },
}
```

This setup proxies API requests through Next.js to avoid CORS issues during development.

## Error Handling

The frontend implements comprehensive error handling:

1. API errors are caught and displayed to users with friendly messages
2. Form validation errors are displayed inline
3. Authentication errors redirect to the login page
4. Network errors show appropriate feedback

Example error handling pattern:

```typescript
try {
  const data = await apiCall();
  // Handle success
} catch (err) {
  if (err.response?.status === 401) {
    // Handle unauthorized
  } else if (err.response?.data?.message) {
    // Display error message from API
    setError(err.response.data.message);
  } else {
    // Generic error
    setError('An unexpected error occurred. Please try again.');
  }
}
```

## Development Setup

To set up the frontend for development:

1. Clone the repository
2. Navigate to the frontend directory
3. Install dependencies:
   ```
   npm install
   ```
4. Start the development server:
   ```
   npm run dev
   ```

The application will be available at `http://localhost:5000`

## Building for Production

To build the frontend for production:

```
npm run build
```

To start the production build:

```
npm start
```

## Tests

To run tests:

```
npm test
```

## Best Practices

When further developing this frontend:

1. **Type Safety**: Maintain strict TypeScript typing for all components and API calls
2. **Component Reuse**: Create reusable components for common UI patterns
3. **Error Handling**: Implement consistent error handling across the application
4. **API Integration**: Use the provided API wrapper functions in `lib/api.ts`
5. **Responsive Design**: Ensure all pages work well on both desktop and mobile devices
6. **Accessibility**: Follow accessibility best practices
7. **State Management**: For complex state, consider using React Context or a state management library