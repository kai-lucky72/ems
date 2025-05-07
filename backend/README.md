# Employee Management System - Backend

This is the backend application for the Employee Management System built with Spring Boot and MySQL.

## Features

- User Registration & Authentication with JWT
- Department Management with budget tracking
- Employee Management
- Salary Management with deductions
- Leave Management
- Messaging System with email integration
- Analytics endpoints

## Prerequisites

- Java 21
- Maven
- MySQL

## Setup and Installation

1. Clone the repository
2. Configure the database connection in `src/main/resources/application.properties`
3. Build the application:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   java -jar target/ems-backend.jar
   ```
   or
   ```bash
   mvn spring-boot:run
   ```
5. The API will be available at [http://localhost:8000/api](http://localhost:8000/api)

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
  - Request: `{"fullName": "string", "email": "string", "phoneNumber": "string", "companyName": "string", "password": "string"}`
  - Response: `{"id": "long", "fullName": "string", "email": "string", "token": "string"}`

- `POST /api/auth/login` - Authenticate a user
  - Request: `{"email": "string", "password": "string"}`
  - Response: `{"id": "long", "fullName": "string", "email": "string", "token": "string"}`

### Department Management
- `GET /api/departments` - Get all departments for current user
- `GET /api/departments/{id}` - Get a specific department
- `POST /api/departments` - Create a new department
  - Request: `{"name": "string", "budget": "double", "budgetType": "MONTHLY|YEARLY"}`
- `PUT /api/departments/{id}` - Update a department
- `DELETE /api/departments/{id}` - Delete a department

### Employee Management
- `GET /api/employees` - Get all employees for current user
- `GET /api/employees/{id}` - Get a specific employee
- `POST /api/employees` - Create a new employee
  - Request: `{"name": "string", "email": "string", "phone": "string", "role": "string", "departmentId": "long", "contractType": "FULL_TIME|PART_TIME|REMOTE", "startDate": "date", "endDate": "date", "isActive": "boolean"}`
- `PUT /api/employees/{id}` - Update an employee
- `DELETE /api/employees/{id}` - Delete an employee
- `PATCH /api/employees/{id}/status` - Update employee status (active/inactive)
  - Request: `{"isActive": "boolean", "inactiveFrom": "date", "inactiveTo": "date"}`

### Salary Management
- `GET /api/salaries` - Get all salary records
- `GET /api/salaries/{id}` - Get a specific salary record
- `GET /api/salaries/employee/{employeeId}` - Get salary for a specific employee
- `POST /api/salaries` - Create a new salary record
  - Request: `{"employeeId": "long", "grossSalary": "double", "deductions": [{"type": "TAX|INSURANCE|CUSTOM", "name": "string", "value": "double", "isPercentage": "boolean"}]}`
- `PUT /api/salaries/{id}` - Update a salary record

### Leave Management
- `GET /api/leaves` - Get all leave requests
- `GET /api/leaves/{id}` - Get a specific leave request
- `GET /api/leaves/employee/{employeeId}` - Get leave records for a specific employee
- `POST /api/leaves` - Create a new leave request
  - Request: `{"employeeId": "long", "startDate": "date", "endDate": "date", "reason": "string"}`
- `PUT /api/leaves/{id}/status` - Update leave request status
  - Request: `{"status": "APPROVED|DENIED"}`

### Messaging System
- `POST /api/messages` - Send a new message
  - Request: `{"employeeId": "long", "subject": "string", "content": "string"}`
- `GET /api/messages/employee/{employeeId}` - Get message history for a specific employee

### Analytics
- `GET /api/analytics/departments` - Get department budget analytics
- `GET /api/analytics/salaries` - Get salary analytics
- `GET /api/analytics/employees` - Get employee distribution analytics
- `GET /api/analytics/leaves` - Get leave status analytics

## Authentication and Security

The system uses JWT (JSON Web Token) for authentication:

1. Upon successful login, a JWT token is generated and returned to the client
2. For protected endpoints, the token must be included in the HTTP Authorization header as a Bearer token
3. The token contains user identification and roles, and is validated on each request

Example:
