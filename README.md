# Employee Management System

A comprehensive Employee Management System built with Next.js (frontend) and Java Spring Boot (backend) for efficient workforce administration and HR management.

## Project Overview

The Employee Management System (EMS) is a modern, scalable web application designed to streamline workforce administration through intelligent, modular solutions with enhanced communication capabilities.

### Key Features

- **Authentication & Authorization**: Role-based access control with two user types (Managers and Employees)
- **Department Management**: Create, update, and track departments with budget management
- **Employee Management**: Manage employee records, contracts, and status
- **Salary & Payroll**: Process employee salaries with customizable deductions
- **Leave Management**: Handle employee leave requests with approval workflows
- **Messaging System**: Internal communication platform for announcements and direct messaging
- **Analytics Dashboard**: Comprehensive HR analytics for informed decision-making

## Architecture

The application is built with a clear separation of concerns:

### Frontend
- **Technology**: Next.js, TypeScript, Tailwind CSS
- **Features**: Responsive UI, role-based views, chart visualizations
- **Structure**: Component-based architecture with reusable UI elements

### Backend
- **Technology**: Java Spring Boot 21
- **Features**: RESTful API, JWT authentication, business logic
- **Structure**: Controller-Service-Repository pattern

## Project Structure

```
/
├── frontend/               # Next.js frontend application
│   ├── components/         # Reusable UI components
│   ├── lib/                # Utility functions and API calls
│   ├── pages/              # Application pages
│   ├── types/              # TypeScript type definitions
│   └── README.md           # Frontend documentation and API guide
│
├── backend/                # Java Spring Boot backend application
│   ├── src/                # Source code
│   │   ├── main/
│   │   │   ├── java/com/ems/
│   │   │   │   ├── controller/ # API endpoints
│   │   │   │   ├── model/      # Data models
│   │   │   │   ├── repository/ # Data access layer
│   │   │   │   ├── service/    # Business logic
│   │   │   │   ├── security/   # Authentication and authorization
│   │   │   │   └── exception/  # Exception handling
│   │   │   └── resources/
│   │   └── test/
│   └── README.md           # Backend documentation and API reference
│
└── README.md               # Main project documentation
```

## Integration Points

While the frontend and backend are designed to be loosely coupled, they integrate through the following API endpoints:

- **Authentication API**: User login, registration, and authorization
- **Department API**: Department CRUD operations
- **Employee API**: Employee management operations
- **Salary API**: Salary and payroll management
- **Leave API**: Leave request and approval workflows
- **Messaging API**: Internal communication
- **Analytics API**: Data aggregation for dashboards

See the individual README files in the frontend and backend directories for detailed API documentation.

## User Roles

The system supports two primary user roles:

1. **Managers**
   - Full system access
   - Can register through self-registration
   - Manage departments, employees, salaries, and leave requests
   - Access to analytics dashboards

2. **Employees**
   - Limited access to personal information
   - Added to the system by managers
   - View personal salary details and submit leave requests
   - Receive messages and notifications

## Setup and Development

### Prerequisites

- Node.js (v14+) and npm for frontend
- Java 21 and Maven for backend
- PostgreSQL database

### Running the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at `http://localhost:5000`

### Running the Backend

```bash
cd backend
./mvnw spring-boot:run
```

The backend API will be available at `http://localhost:8080`

## API Documentation

Detailed API documentation is available in:
- **Frontend API Integration**: `/frontend/README.md`
- **Backend API Reference**: `/backend/README.md`

## Contributing

This project follows a modular approach to encourage extensions and customizations:

1. **Frontend Extensions**: Add new components and pages following the existing patterns
2. **Backend Extensions**: Add new controllers, services, and repositories following the established architecture
3. **Testing**: Write unit and integration tests for new features

## License

This project is licensed under the MIT License - see the LICENSE file for details.