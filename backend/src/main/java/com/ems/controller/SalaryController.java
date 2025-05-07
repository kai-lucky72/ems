package com.ems.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.ems.dto.SalaryDto;
import com.ems.service.SalaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST Controller for managing salary and payroll operations
 */
@RestController
@RequestMapping("/api/salaries")
@Tag(name = "Salary Management", description = "APIs for managing employee salaries and payroll")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    /**
     * Get all salaries
     */
    @Operation(summary = "Get all salaries", description = "Retrieve a list of all salary records for the company")
    @GetMapping
    public ResponseEntity<List<SalaryDto>> getAllSalaries() {
        List<SalaryDto> salaries = salaryService.getAllSalariesForCurrentUser();
        return ResponseEntity.ok(salaries);
    }

    /**
     * Get salaries for a specific month and year
     */
    @Operation(summary = "Get salaries by month/year", description = "Retrieve all salary records for a specific month and year")
    @GetMapping("/period")
    public ResponseEntity<List<SalaryDto>> getSalariesByPeriod(
            @Parameter(description = "Month (1-12)", required = true) @RequestParam Integer month,
            @Parameter(description = "Year (e.g., 2023)", required = true) @RequestParam Integer year) {
        
        List<SalaryDto> salaries = salaryService.getSalariesByMonthAndYear(month, year);
        return ResponseEntity.ok(salaries);
    }
    
    /**
     * Get current month's salaries
     */
    @Operation(summary = "Get current month salaries", description = "Retrieve all salary records for the current month")
    @GetMapping("/current")
    public ResponseEntity<List<SalaryDto>> getCurrentMonthSalaries() {
        List<SalaryDto> salaries = salaryService.getCurrentMonthSalaries();
        return ResponseEntity.ok(salaries);
    }
    
    /**
     * Get all salary periods (month/year combinations)
     */
    @Operation(summary = "Get salary periods", description = "Retrieve all month/year combinations that have salary records")
    @GetMapping("/periods")
    public ResponseEntity<List<Map<String, Object>>> getSalaryPeriods() {
        List<Map<String, Object>> periods = salaryService.getSalaryPeriods();
        return ResponseEntity.ok(periods);
    }

    /**
     * Get a salary by ID
     */
    @Operation(summary = "Get salary by ID", description = "Retrieve a specific salary record by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<SalaryDto> getSalaryById(
            @Parameter(description = "Salary ID", required = true) @PathVariable Long id) {
        SalaryDto salary = salaryService.getSalaryById(id);
        return ResponseEntity.ok(salary);
    }

    /**
     * Get a current salary for an employee
     */
    @Operation(summary = "Get current salary by employee ID", description = "Retrieve the most recent salary record for a specific employee")
    @GetMapping("/employee/{employeeId}/current")
    public ResponseEntity<SalaryDto> getCurrentSalaryByEmployeeId(
            @Parameter(description = "Employee ID", required = true) @PathVariable Long employeeId) {
        SalaryDto salary = salaryService.getCurrentSalaryByEmployeeId(employeeId);
        return ResponseEntity.ok(salary);
    }
    
    /**
     * Get salary history for an employee
     */
    @Operation(summary = "Get salary history by employee ID", description = "Retrieve the complete salary history for a specific employee")
    @GetMapping("/employee/{employeeId}/history")
    public ResponseEntity<List<SalaryDto>> getSalaryHistoryByEmployeeId(
            @Parameter(description = "Employee ID", required = true) @PathVariable Long employeeId) {
        List<SalaryDto> salaries = salaryService.getSalaryHistoryByEmployeeId(employeeId);
        return ResponseEntity.ok(salaries);
    }
    
    /**
     * Get top earners
     */
    @Operation(summary = "Get top earners", description = "Retrieve the highest paid employees")
    @GetMapping("/top-earners")
    public ResponseEntity<List<SalaryDto>> getTopEarners(
            @Parameter(description = "Number of top earners to retrieve") @RequestParam(defaultValue = "5") int limit) {
        List<SalaryDto> topEarners = salaryService.getTopEarners(limit);
        return ResponseEntity.ok(topEarners);
    }
    
    /**
     * Get salary statistics for a department
     */
    @Operation(summary = "Get department salary stats", description = "Retrieve salary statistics for a specific department")
    @GetMapping("/department/{departmentId}/stats")
    public ResponseEntity<Map<String, Object>> getDepartmentSalaryStats(
            @Parameter(description = "Department ID", required = true) @PathVariable Long departmentId) {
        Map<String, Object> stats = salaryService.getDepartmentSalaryStats(departmentId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Create a new salary
     */
    @Operation(summary = "Create new salary", description = "Create a new salary record for an employee")
    @PostMapping
    public ResponseEntity<SalaryDto> createSalary(
            @Parameter(description = "Salary details", required = true) @Valid @RequestBody SalaryDto salaryDto) {
        SalaryDto createdSalary = salaryService.createSalary(salaryDto);
        return new ResponseEntity<>(createdSalary, HttpStatus.CREATED);
    }

    /**
     * Update an existing salary
     */
    @Operation(summary = "Update salary", description = "Update an existing salary record")
    @PutMapping("/{id}")
    public ResponseEntity<SalaryDto> updateSalary(
            @Parameter(description = "Salary ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated salary details", required = true) @Valid @RequestBody SalaryDto salaryDto) {
        
        SalaryDto updatedSalary = salaryService.updateSalary(id, salaryDto);
        return ResponseEntity.ok(updatedSalary);
    }
    
    /**
     * Delete a salary
     */
    @Operation(summary = "Delete salary", description = "Delete an existing salary record")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSalary(
            @Parameter(description = "Salary ID", required = true) @PathVariable Long id) {
        
        salaryService.deleteSalary(id);
        return ResponseEntity.ok(new ApiResponse(true, "Salary deleted successfully"));
    }
    
    /**
     * Generate current month's salaries
     */
    @Operation(summary = "Generate current month salaries", 
               description = "Automatically generate salary records for the current month for all active employees who don't have one yet")
    @PostMapping("/generate-current")
    public ResponseEntity<Map<String, Object>> generateCurrentMonthSalaries() {
        List<SalaryDto> generatedSalaries = salaryService.generateCurrentMonthSalaries();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Generated " + generatedSalaries.size() + " salary records for current month");
        response.put("count", generatedSalaries.size());
        response.put("salaries", generatedSalaries);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get salary statistics by department
     */
    @Operation(summary = "Get salary stats by department", description = "Retrieve salary statistics grouped by department")
    @GetMapping("/stats/by-department")
    public ResponseEntity<List<Map<String, Object>>> getSalaryStatsByDepartment() {
        List<Map<String, Object>> stats = salaryService.getSalaryStatsByDepartment();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get monthly salary trends
     */
    @Operation(summary = "Get salary trends", description = "Retrieve historical salary trends by month")
    @GetMapping("/trends")
    public ResponseEntity<List<Map<String, Object>>> getSalaryTrends() {
        List<Map<String, Object>> trends = salaryService.getSalaryTrends();
        return ResponseEntity.ok(trends);
    }
}
