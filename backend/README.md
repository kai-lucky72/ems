# Employee Management System - Backend API

This document outlines the API endpoints provided by the EMS backend service, built using Java Spring Boot 21.

## Overview

The backend provides a RESTful API for the Employee Management System, with endpoints for authentication, employee management, department management, salary management, leave management, messaging, and analytics.

## Authentication

Authentication is handled via JWT (JSON Web Tokens). All authenticated endpoints require a valid JWT token in the `Authorization` header using the Bearer scheme.

### Authentication Endpoints

#### Register a New Manager Account

```
POST /api/auth/register
```

**Request Body:**
```json
{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "companyName": "Acme Inc",
  "password": "SecurePassword123"
}
```

**Response:**
```json
{
  "id": 1,
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "token": "jwt_token_string",
  "role": "MANAGER"
}
```

#### Login to an Existing Account

```
POST /api/auth/login
```

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```

**Response:**
```json
{
  "id": 1,
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "token": "jwt_token_string",
  "role": "MANAGER"
}
```

#### Activate Employee Account

```
POST /api/auth/activate
```

**Request Body:**
```json
{
  "token": "activation_token",
  "password": "SecurePassword123"
}
```

**Response:**
```json
{
  "id": 2,
  "fullName": "Jane Smith",
  "email": "jane.smith@example.com",
  "token": "jwt_token_string",
  "role": "EMPLOYEE"
}
```

#### Reset Password

```
POST /api/auth/reset-password
```

**Request Body:**
```json
{
  "token": "reset_token",
  "newPassword": "NewSecurePassword123"
}
```

**Response:**
```json
{
  "message": "Password reset successfully"
}
```

#### Request Password Reset

```
POST /api/auth/request-reset
```

**Request Body:**
```json
{
  "email": "jane.smith@example.com"
}
```

**Response:**
```json
{
  "message": "Password reset request sent"
}
```

## Department Management

### Department Endpoints

#### Get All Departments

```
GET /api/departments
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Engineering",
    "budget": 100000,
    "budgetType": "MONTHLY",
    "currentExpenses": 85000
  },
  {
    "id": 2,
    "name": "Marketing",
    "budget": 75000,
    "budgetType": "MONTHLY",
    "currentExpenses": 45000
  }
]
```

#### Get Department by ID

```
GET /api/departments/{id}
```

**Response:**
```json
{
  "id": 1,
  "name": "Engineering",
  "budget": 100000,
  "budgetType": "MONTHLY",
  "currentExpenses": 85000
}
```

#### Create Department

```
POST /api/departments
```

**Request Body:**
```json
{
  "name": "Sales",
  "budget": 80000,
  "budgetType": "MONTHLY"
}
```

**Response:**
```json
{
  "id": 3,
  "name": "Sales",
  "budget": 80000,
  "budgetType": "MONTHLY",
  "currentExpenses": 0
}
```

#### Update Department

```
PUT /api/departments/{id}
```

**Request Body:**
```json
{
  "name": "Sales & Marketing",
  "budget": 90000,
  "budgetType": "MONTHLY"
}
```

**Response:**
```json
{
  "id": 3,
  "name": "Sales & Marketing",
  "budget": 90000,
  "budgetType": "MONTHLY",
  "currentExpenses": 0
}
```

#### Delete Department

```
DELETE /api/departments/{id}
```

**Response:**
```json
{
  "message": "Department deleted successfully"
}
```

## Employee Management

### Employee Endpoints

#### Get All Employees

```
GET /api/employees
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "phone": "+1234567890",
    "role": "Software Engineer",
    "departmentId": 1,
    "departmentName": "Engineering",
    "contractType": "FULL_TIME",
    "startDate": "2023-01-15",
    "endDate": null,
    "isActive": true,
    "inactiveFrom": null,
    "inactiveTo": null
  }
]
```

#### Get Employee by ID

```
GET /api/employees/{id}
```

**Response:**
```json
{
  "id": 1,
  "name": "Jane Smith",
  "email": "jane.smith@example.com",
  "phone": "+1234567890",
  "role": "Software Engineer",
  "departmentId": 1,
  "departmentName": "Engineering",
  "contractType": "FULL_TIME",
  "startDate": "2023-01-15",
  "endDate": null,
  "isActive": true,
  "inactiveFrom": null,
  "inactiveTo": null
}
```

#### Create Employee

```
POST /api/employees
```

**Request Body:**
```json
{
  "name": "Alice Johnson",
  "email": "alice.johnson@example.com",
  "phone": "+1987654321",
  "role": "Product Manager",
  "departmentId": 1,
  "contractType": "FULL_TIME",
  "startDate": "2023-05-10",
  "endDate": null,
  "isActive": true
}
```

**Response:**
```json
{
  "id": 2,
  "name": "Alice Johnson",
  "email": "alice.johnson@example.com",
  "phone": "+1987654321",
  "role": "Product Manager",
  "departmentId": 1,
  "departmentName": "Engineering",
  "contractType": "FULL_TIME",
  "startDate": "2023-05-10",
  "endDate": null,
  "isActive": true,
  "inactiveFrom": null,
  "inactiveTo": null
}
```

#### Update Employee

```
PUT /api/employees/{id}
```

**Request Body:**
```json
{
  "name": "Alice J. Smith",
  "email": "alice.johnson@example.com",
  "phone": "+1987654321",
  "role": "Senior Product Manager",
  "departmentId": 1,
  "contractType": "FULL_TIME",
  "startDate": "2023-05-10",
  "endDate": null,
  "isActive": true
}
```

**Response:**
```json
{
  "id": 2,
  "name": "Alice J. Smith",
  "email": "alice.johnson@example.com",
  "phone": "+1987654321",
  "role": "Senior Product Manager",
  "departmentId": 1,
  "departmentName": "Engineering",
  "contractType": "FULL_TIME",
  "startDate": "2023-05-10",
  "endDate": null,
  "isActive": true,
  "inactiveFrom": null,
  "inactiveTo": null
}
```

#### Update Employee Status

```
PATCH /api/employees/{id}/status
```

**Request Body:**
```json
{
  "isActive": false,
  "inactiveFrom": "2023-07-01",
  "inactiveTo": "2023-07-15"
}
```

**Response:**
```json
{
  "id": 2,
  "name": "Alice J. Smith",
  "email": "alice.johnson@example.com",
  "phone": "+1987654321",
  "role": "Senior Product Manager",
  "departmentId": 1,
  "departmentName": "Engineering",
  "contractType": "FULL_TIME",
  "startDate": "2023-05-10",
  "endDate": null,
  "isActive": false,
  "inactiveFrom": "2023-07-01",
  "inactiveTo": "2023-07-15"
}
```

#### Delete Employee

```
DELETE /api/employees/{id}
```

**Response:**
```json
{
  "message": "Employee deleted successfully"
}
```

## Salary Management

### Salary Endpoints

#### Get All Salaries

```
GET /api/salaries
```

**Response:**
```json
[
  {
    "id": 1,
    "employeeId": 1,
    "employeeName": "Jane Smith",
    "departmentName": "Engineering",
    "grossSalary": 5000,
    "netSalary": 3850,
    "deductions": [
      {
        "id": 1,
        "type": "TAX",
        "name": "Income Tax",
        "value": 20,
        "isPercentage": true
      },
      {
        "id": 2,
        "type": "INSURANCE",
        "name": "Health Insurance",
        "value": 150,
        "isPercentage": false
      }
    ]
  }
]
```

#### Get Salary by Employee ID

```
GET /api/salaries/employee/{employeeId}
```

**Response:**
```json
{
  "id": 1,
  "employeeId": 1,
  "employeeName": "Jane Smith",
  "departmentName": "Engineering",
  "grossSalary": 5000,
  "netSalary": 3850,
  "deductions": [
    {
      "id": 1,
      "type": "TAX",
      "name": "Income Tax",
      "value": 20,
      "isPercentage": true
    },
    {
      "id": 2,
      "type": "INSURANCE",
      "name": "Health Insurance",
      "value": 150,
      "isPercentage": false
    }
  ]
}
```

#### Create Salary

```
POST /api/salaries
```

**Request Body:**
```json
{
  "employeeId": 2,
  "grossSalary": 6000,
  "deductions": [
    {
      "type": "TAX",
      "name": "Income Tax",
      "value": 20,
      "isPercentage": true
    },
    {
      "type": "INSURANCE",
      "name": "Health Insurance",
      "value": 200,
      "isPercentage": false
    }
  ]
}
```

**Response:**
```json
{
  "id": 2,
  "employeeId": 2,
  "employeeName": "Alice J. Smith",
  "departmentName": "Engineering",
  "grossSalary": 6000,
  "netSalary": 4600,
  "deductions": [
    {
      "id": 3,
      "type": "TAX",
      "name": "Income Tax",
      "value": 20,
      "isPercentage": true
    },
    {
      "id": 4,
      "type": "INSURANCE",
      "name": "Health Insurance",
      "value": 200,
      "isPercentage": false
    }
  ]
}
```

#### Update Salary

```
PUT /api/salaries/{id}
```

**Request Body:**
```json
{
  "employeeId": 2,
  "grossSalary": 6500,
  "deductions": [
    {
      "type": "TAX",
      "name": "Income Tax",
      "value": 20,
      "isPercentage": true
    },
    {
      "type": "INSURANCE",
      "name": "Health Insurance",
      "value": 200,
      "isPercentage": false
    },
    {
      "type": "CUSTOM",
      "name": "Retirement Plan",
      "value": 5,
      "isPercentage": true
    }
  ]
}
```

**Response:**
```json
{
  "id": 2,
  "employeeId": 2,
  "employeeName": "Alice J. Smith",
  "departmentName": "Engineering",
  "grossSalary": 6500,
  "netSalary": 4775,
  "deductions": [
    {
      "id": 3,
      "type": "TAX",
      "name": "Income Tax",
      "value": 20,
      "isPercentage": true
    },
    {
      "id": 4,
      "type": "INSURANCE",
      "name": "Health Insurance",
      "value": 200,
      "isPercentage": false
    },
    {
      "id": 5,
      "type": "CUSTOM",
      "name": "Retirement Plan",
      "value": 5,
      "isPercentage": true
    }
  ]
}
```

## Leave Management

### Leave Endpoints

#### Get All Leaves

```
GET /api/leaves
```

**Response:**
```json
[
  {
    "id": 1,
    "employeeId": 1,
    "employeeName": "Jane Smith",
    "startDate": "2023-05-15",
    "endDate": "2023-05-20",
    "reason": "Family vacation",
    "status": "APPROVED",
    "createdAt": "2023-04-20T10:30:00Z"
  }
]
```

#### Get Employee Leaves

```
GET /api/leaves/employee/{employeeId}
```

**Response:**
```json
[
  {
    "id": 1,
    "employeeId": 1,
    "employeeName": "Jane Smith",
    "startDate": "2023-05-15",
    "endDate": "2023-05-20",
    "reason": "Family vacation",
    "status": "APPROVED",
    "createdAt": "2023-04-20T10:30:00Z"
  }
]
```

#### Create Leave Request

```
POST /api/leaves
```

**Request Body:**
```json
{
  "employeeId": 1,
  "startDate": "2023-07-10",
  "endDate": "2023-07-12",
  "reason": "Medical appointment"
}
```

**Response:**
```json
{
  "id": 2,
  "employeeId": 1,
  "employeeName": "Jane Smith",
  "startDate": "2023-07-10",
  "endDate": "2023-07-12",
  "reason": "Medical appointment",
  "status": "PENDING",
  "createdAt": "2023-06-25T14:15:00Z"
}
```

#### Update Leave Status

```
PATCH /api/leaves/{id}/status
```

**Request Body:**
```json
{
  "status": "APPROVED"
}
```

**Response:**
```json
{
  "id": 2,
  "employeeId": 1,
  "employeeName": "Jane Smith",
  "startDate": "2023-07-10",
  "endDate": "2023-07-12",
  "reason": "Medical appointment",
  "status": "APPROVED",
  "createdAt": "2023-06-25T14:15:00Z"
}
```

## Messaging System

### Message Endpoints

#### Get All Messages

```
GET /api/messages
```

**Response:**
```json
[
  {
    "id": 1,
    "employeeId": 1,
    "employeeName": "Jane Smith",
    "subject": "Welcome to the Engineering Team",
    "content": "Hello Jane, we are excited to welcome you to our Engineering team!",
    "sentAt": "2023-05-15T09:30:00Z",
    "status": "SENT"
  }
]
```

#### Get Employee Messages

```
GET /api/messages/employee/{employeeId}
```

**Response:**
```json
[
  {
    "id": 1,
    "employeeId": 1,
    "employeeName": "Jane Smith",
    "subject": "Welcome to the Engineering Team",
    "content": "Hello Jane, we are excited to welcome you to our Engineering team!",
    "sentAt": "2023-05-15T09:30:00Z",
    "status": "SENT"
  }
]
```

#### Send Message

```
POST /api/messages
```

**Request Body:**
```json
{
  "employeeId": 1,
  "subject": "Annual Performance Review",
  "content": "Dear Jane, this is a reminder that your annual performance review is scheduled for July 15th."
}
```

**Response:**
```json
{
  "id": 2,
  "employeeId": 1,
  "employeeName": "Jane Smith",
  "subject": "Annual Performance Review",
  "content": "Dear Jane, this is a reminder that your annual performance review is scheduled for July 15th.",
  "sentAt": "2023-06-20T14:15:00Z",
  "status": "SENT"
}
```

## Analytics

### Analytics Endpoints

#### Get System Analytics

```
GET /api/analytics
```

**Response:**
```json
{
  "departmentBudget": {
    "labels": ["Engineering", "Marketing", "Sales & Marketing"],
    "actual": [85000, 45000, 30000],
    "budget": [100000, 75000, 90000]
  },
  "salaryData": {
    "totalGross": 11500,
    "totalNet": 8625,
    "averageSalary": 5750,
    "departmentSalaries": [
      {
        "department": "Engineering",
        "totalSalary": 11500
      }
    ]
  },
  "employeeDistribution": {
    "labels": ["Engineering", "Marketing", "Sales & Marketing"],
    "counts": [2, 0, 0]
  },
  "leaveStatus": {
    "labels": ["PENDING", "APPROVED", "DENIED"],
    "counts": [1, 1, 0]
  },
  "roleDistribution": {
    "labels": ["Software Engineer", "Senior Product Manager"],
    "counts": [1, 1]
  },
  "contractTypeDistribution": {
    "labels": ["FULL_TIME", "PART_TIME", "REMOTE"],
    "counts": [2, 0, 0]
  },
  "employeeTimeline": {
    "months": ["Jan 2023", "Feb 2023", "Mar 2023", "Apr 2023", "May 2023", "Jun 2023"],
    "active": [1, 1, 1, 1, 2, 2],
    "inactive": [0, 0, 0, 0, 0, 0]
  }
}
```

## Error Handling

All API endpoints follow a consistent error response format:

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": [
    "Email is required",
    "Password must be at least 8 characters long"
  ],
  "timestamp": "2023-06-25T14:15:00Z"
}
```

## Integration with Frontend

To integrate with the frontend, all API calls should:

1. Include the appropriate authentication headers:
   ```
   Authorization: Bearer <jwt_token>
   Content-Type: application/json
   ```

2. Handle response status codes appropriately:
   - 200/201: Success
   - 400: Bad request
   - 401: Unauthorized (invalid/expired token)
   - 403: Forbidden (insufficient permissions)
   - 404: Resource not found
   - 500: Server error

3. Implement proper error handling and display user-friendly error messages.

4. Implement token refresh mechanisms to handle token expiration.

## Development Setup

To set up the backend for development:

1. Clone the repository
2. Navigate to the backend directory
3. Configure the application.properties file with database connections
4. Run `./mvnw spring-boot:run` to start the development server

The API will be available at `http://localhost:8080`