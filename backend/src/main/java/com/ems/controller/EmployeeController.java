package com.ems.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ems.dto.ApiResponse;
import com.ems.dto.EmployeeDto;
import com.ems.dto.EmployeeInactivityDto;
import com.ems.model.Employee.ContractType;
import com.ems.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST Controller for managing employees
 */
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee Management", description = "APIs for managing company employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * Get all employees
     */
    @Operation(summary = "Get all employees", description = "Retrieve a list of all employees in the company")
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        List<EmployeeDto> employees = employeeService.getAllEmployeesForCurrentUser();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get active employees
     */
    @Operation(summary = "Get active employees", description = "Retrieve a list of all active employees in the company")
    @GetMapping("/active")
    public ResponseEntity<List<EmployeeDto>> getActiveEmployees() {
        List<EmployeeDto> employees = employeeService.getActiveEmployeesForCurrentUser();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get inactive employees
     */
    @Operation(summary = "Get inactive employees", description = "Retrieve a list of all inactive employees in the company")
    @GetMapping("/inactive")
    public ResponseEntity<List<EmployeeDto>> getInactiveEmployees() {
        List<EmployeeDto> employees = employeeService.getInactiveEmployeesForCurrentUser();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get paginated employees
     */
    @Operation(summary = "Get paginated employees", description = "Retrieve a paginated list of employees with sorting options")
    @GetMapping("/paginated")
    public ResponseEntity<Page<EmployeeDto>> getPaginatedEmployees(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "true") boolean ascending) {
        
        Page<EmployeeDto> employees = employeeService.getPaginatedEmployees(page, size, sortBy, ascending);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get employee by ID
     */
    @Operation(summary = "Get employee by ID", description = "Retrieve a specific employee by their ID")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(
            @Parameter(description = "Employee ID", required = true) @PathVariable Long id) {
        EmployeeDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }
    
    /**
     * Get detailed employee information
     */
    @Operation(summary = "Get detailed employee information", description = "Retrieve detailed information about an employee including salary, leave and inactivity history")
    @GetMapping("/{id}/details")
    public ResponseEntity<EmployeeDto> getEmployeeDetailById(
            @Parameter(description = "Employee ID", required = true) @PathVariable Long id) {
        EmployeeDto employee = employeeService.getEmployeeDetailById(id);
        return ResponseEntity.ok(employee);
    }
    
    /**
     * Search employees by keyword
     */
    @Operation(summary = "Search employees", description = "Search for employees by name, email, role or department")
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDto>> searchEmployees(
            @Parameter(description = "Search term", required = true) @RequestParam String term) {
        List<EmployeeDto> employees = employeeService.searchEmployees(term);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees by department
     */
    @Operation(summary = "Get employees by department", description = "Retrieve all employees in a specific department")
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartment(
            @Parameter(description = "Department ID", required = true) @PathVariable Long departmentId) {
        List<EmployeeDto> employees = employeeService.getEmployeesByDepartment(departmentId);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees by contract type
     */
    @Operation(summary = "Get employees by contract type", description = "Retrieve all employees with a specific contract type")
    @GetMapping("/contract-type/{type}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByContractType(
            @Parameter(description = "Contract type", required = true) @PathVariable ContractType type) {
        List<EmployeeDto> employees = employeeService.getEmployeesByContractType(type);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees on leave
     */
    @Operation(summary = "Get employees on leave", description = "Retrieve all employees currently on approved leave")
    @GetMapping("/on-leave")
    public ResponseEntity<List<EmployeeDto>> getEmployeesOnLeave() {
        List<EmployeeDto> employees = employeeService.getEmployeesOnLeave();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees with contracts ending soon
     */
    @Operation(summary = "Get employees with contracts ending soon", description = "Retrieve employees whose contracts will end within the specified number of days")
    @GetMapping("/contracts-ending-soon")
    public ResponseEntity<List<EmployeeDto>> getEmployeesWithContractsEndingSoon(
            @Parameter(description = "Days threshold") @RequestParam(defaultValue = "30") int days) {
        List<EmployeeDto> employees = employeeService.getEmployeesWithContractsEndingSoon(days);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get recently joined employees
     */
    @Operation(summary = "Get recent employees", description = "Retrieve employees who joined within the specified number of days")
    @GetMapping("/recent")
    public ResponseEntity<List<EmployeeDto>> getRecentEmployees(
            @Parameter(description = "Days threshold") @RequestParam(defaultValue = "30") int days) {
        List<EmployeeDto> employees = employeeService.getRecentEmployees(days);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees with upcoming work anniversaries
     */
    @Operation(summary = "Get employees with upcoming anniversaries", description = "Retrieve employees with work anniversaries coming up in the specified number of days")
    @GetMapping("/upcoming-anniversaries")
    public ResponseEntity<List<EmployeeDto>> getEmployeesWithUpcomingAnniversaries(
            @Parameter(description = "Days threshold") @RequestParam(defaultValue = "30") int days) {
        List<EmployeeDto> employees = employeeService.getEmployeesWithUpcomingAnniversaries(days);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees with no salary assigned
     */
    @Operation(summary = "Get employees with no salary", description = "Retrieve active employees who don't have any salary record assigned")
    @GetMapping("/no-salary")
    public ResponseEntity<List<EmployeeDto>> getEmployeesWithNoSalary() {
        List<EmployeeDto> employees = employeeService.getEmployeesWithNoSalary();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees with pending leave requests
     */
    @Operation(summary = "Get employees with pending leaves", description = "Retrieve employees who have pending leave requests")
    @GetMapping("/pending-leaves")
    public ResponseEntity<List<EmployeeDto>> getEmployeesWithPendingLeaves() {
        List<EmployeeDto> employees = employeeService.getEmployeesWithPendingLeaves();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees by role
     */
    @Operation(summary = "Get employees by role", description = "Retrieve all employees with a specific job role")
    @GetMapping("/role")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByRole(
            @Parameter(description = "Role name", required = true) @RequestParam String role) {
        List<EmployeeDto> employees = employeeService.getEmployeesByRole(role);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get currently inactive employees
     */
    @Operation(summary = "Get currently inactive employees", description = "Retrieve employees who are currently marked as inactive with valid inactivity periods")
    @GetMapping("/currently-inactive")
    public ResponseEntity<List<EmployeeDto>> getCurrentlyInactiveEmployees() {
        List<EmployeeDto> employees = employeeService.getCurrentlyInactiveEmployees();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employees returning from inactivity soon
     */
    @Operation(summary = "Get employees returning from inactivity", description = "Retrieve employees who will return from inactivity within the specified number of days")
    @GetMapping("/returning-soon")
    public ResponseEntity<List<EmployeeDto>> getEmployeesReturningFromInactivity(
            @Parameter(description = "Days threshold") @RequestParam(defaultValue = "14") int days) {
        List<EmployeeDto> employees = employeeService.getEmployeesReturningFromInactivity(days);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * Get employee counts by role
     */
    @Operation(summary = "Get employee counts by role", description = "Retrieve counts of employees grouped by their roles")
    @GetMapping("/counts/by-role")
    public ResponseEntity<List<Map<String, Object>>> getEmployeeCountsByRole() {
        List<Map<String, Object>> countsData = employeeService.getEmployeeCountsByRole();
        return ResponseEntity.ok(countsData);
    }
    
    /**
     * Get employee counts by contract type
     */
    @Operation(summary = "Get employee counts by contract type", description = "Retrieve counts of employees grouped by their contract types")
    @GetMapping("/counts/by-contract-type")
    public ResponseEntity<List<Map<String, Object>>> getEmployeeCountsByContractType() {
        List<Map<String, Object>> countsData = employeeService.getEmployeeCountsByContractType();
        return ResponseEntity.ok(countsData);
    }
    
    /**
     * Get employee counts by department
     */
    @Operation(summary = "Get employee counts by department", description = "Retrieve counts of employees grouped by their departments")
    @GetMapping("/counts/by-department")
    public ResponseEntity<List<Map<String, Object>>> getEmployeeCountsByDepartment() {
        List<Map<String, Object>> countsData = employeeService.getEmployeeCountsByDepartment();
        return ResponseEntity.ok(countsData);
    }
    
    /**
     * Get employee hiring trends
     */
    @Operation(summary = "Get employee hiring trends", description = "Retrieve employee counts grouped by their hire date (year and month)")
    @GetMapping("/hiring-trends")
    public ResponseEntity<List<Map<String, Object>>> getEmployeeHiringTrends() {
        List<Map<String, Object>> trendsData = employeeService.getEmployeeHiringTrends();
        return ResponseEntity.ok(trendsData);
    }

    /**
     * Create a new employee
     */
    @Operation(summary = "Create new employee", description = "Create a new employee in the company")
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(
            @Parameter(description = "Employee details", required = true) @Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    /**
     * Update an existing employee
     */
    @Operation(summary = "Update employee", description = "Update an existing employee's details")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @Parameter(description = "Employee ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated employee details", required = true) @Valid @RequestBody EmployeeDto employeeDto) {
        
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    /**
     * Update employee status (active/inactive) and inactivity record
     */
    @Operation(summary = "Update employee status", description = "Update an employee's active status and create or update inactivity records")
    @PutMapping("/{id}/status")
    public ResponseEntity<EmployeeDto> updateEmployeeStatus(
            @Parameter(description = "Employee ID", required = true) @PathVariable Long id,
            @Parameter(description = "Inactivity details", required = true) @Valid @RequestBody EmployeeInactivityDto inactivityDto) {
        
        EmployeeDto updatedEmployee = employeeService.updateEmployeeStatus(id, inactivityDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    /**
     * Delete an employee
     */
    @Operation(summary = "Delete employee", description = "Delete an existing employee (only if they have no dependent records)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(
            @Parameter(description = "Employee ID", required = true) @PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new ApiResponse(true, "Employee deleted successfully"));
    }
}