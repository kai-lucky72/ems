# Employee Management System - Frontend

This is the frontend application for the Employee Management System built with Next.js and Tailwind CSS.

## Features

- User Registration & Authentication
- Department Management
- Employee Management
- Salary Management with deductions
- Leave Management
- Messaging System
- Analytics Dashboard

## Prerequisites

- Node.js (v16+)
- npm or yarn

## Setup and Installation

1. Clone the repository
2. Install dependencies:
   ```bash
   npm install
   # or
   yarn install
   ```
3. Create a `.env.local` file in the root directory with the following variables:
   ```
   NEXT_PUBLIC_API_URL=http://localhost:8000/api
   ```
4. Start the development server:
   ```bash
   npm run dev
   # or
   yarn dev
   ```
5. Open [http://localhost:5000](http://localhost:5000) in your browser

## API Integration

The frontend communicates with the backend through RESTful API endpoints. Below are the endpoints used by each feature:

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Authenticate a user and get JWT token

### Department Management
- `GET /api/departments` - Get all departments
- `GET /api/departments/{id}` - Get a specific department
- `POST /api/departments` - Create a new department
- `PUT /api/departments/{id}` - Update a department
- `DELETE /api/departments/{id}` - Delete a department

### Employee Management
- `GET /api/employees` - Get all employees
- `GET /api/employees/{id}` - Get a specific employee
- `POST /api/employees` - Create a new employee
- `PUT /api/employees/{id}` - Update an employee
- `DELETE /api/employees/{id}` - Delete an employee

### Salary Management
- `GET /api/salaries` - Get all salaries
- `GET /api/salaries/{id}` - Get a specific salary
- `POST /api/salaries` - Create a new salary record
- `PUT /api/salaries/{id}` - Update a salary record
- `GET /api/salaries/employee/{employeeId}` - Get salary for a specific employee

### Leave Management
- `GET /api/leaves` - Get all leave requests
- `GET /api/leaves/{id}` - Get a specific leave request
- `POST /api/leaves` - Create a new leave request
- `PUT /api/leaves/{id}` - Update a leave request status
- `GET /api/leaves/employee/{employeeId}` - Get leave records for a specific employee

### Messaging System
- `POST /api/messages` - Send a new message
- `GET /api/messages/employee/{employeeId}` - Get message history for a specific employee

### Analytics
- `GET /api/analytics/departments` - Get department budget analytics
- `GET /api/analytics/salaries` - Get salary analytics
- `GET /api/analytics/employees` - Get employee distribution analytics
- `GET /api/analytics/leaves` - Get leave status analytics

## Project Structure

- `pages/` - Contains all the Next.js pages
- `components/` - React components used across multiple pages
- `lib/` - Utility functions including API client
- `styles/` - Global CSS and Tailwind configuration
- `types/` - TypeScript type definitions

## Authentication Flow

1. User registers or logs in through the interface
2. Upon successful authentication, JWT token is stored in localStorage
3. Token is attached to all subsequent API requests
4. Protected routes check for valid token before rendering

## Integration with Backend

To integrate with the backend:

1. Ensure the backend is running on the configured URL
2. Make sure CORS is properly configured on the backend to accept requests from the frontend origin
3. The authentication token must be included in the Authorization header for all protected endpoints

## Development Guidelines

- Use TypeScript for type safety
- Follow the component structure for new features
- Use the API client in `lib/api.ts` for all backend requests
- Handle loading and error states appropriately
