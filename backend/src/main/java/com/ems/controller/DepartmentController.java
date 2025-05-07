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
import com.ems.dto.DepartmentDto;
import com.ems.service.DepartmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST Controller for managing departments
 */
@RestController
@RequestMapping("/api/departments")
@Tag(name = "Department Management", description = "APIs for managing company departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * Get all departments
     */
    @Operation(summary = "Get all departments", description = "Retrieve a list of all departments in the company")
    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartmentsForCurrentUser();
        return ResponseEntity.ok(departments);
    }
    
    /**
     * Get paginated departments
     */
    @Operation(summary = "Get paginated departments", description = "Retrieve a paginated list of departments with sorting options")
    @GetMapping("/paginated")
    public ResponseEntity<Page<DepartmentDto>> getPaginatedDepartments(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "true") boolean ascending) {
        
        Page<DepartmentDto> departments = departmentService.getPaginatedDepartments(page, size, sortBy, ascending);
        return ResponseEntity.ok(departments);
    }
    
    /**
     * Get departments sorted by name
     */
    @Operation(summary = "Get departments sorted by name", description = "Retrieve all departments sorted alphabetically by name")
    @GetMapping("/sorted-by-name")
    public ResponseEntity<List<DepartmentDto>> getDepartmentsSortedByName() {
        List<DepartmentDto> departments = departmentService.getAllDepartmentsSortedByName();
        return ResponseEntity.ok(departments);
    }
    
    /**
     * Get departments sorted by budget
     */
    @Operation(summary = "Get departments sorted by budget", description = "Retrieve all departments sorted by budget (highest first)")
    @GetMapping("/sorted-by-budget")
    public ResponseEntity<List<DepartmentDto>> getDepartmentsSortedByBudget() {
        List<DepartmentDto> departments = departmentService.getAllDepartmentsSortedByBudget();
        return ResponseEntity.ok(departments);
    }

    /**
     * Get a department by ID
     */
    @Operation(summary = "Get department by ID", description = "Retrieve a specific department by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(
            @Parameter(description = "Department ID", required = true) @PathVariable Long id) {
        DepartmentDto department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }
    
    /**
     * Get a department with its employees
     */
    @Operation(summary = "Get department with employees", description = "Retrieve a specific department with its employee details")
    @GetMapping("/{id}/with-employees")
    public ResponseEntity<DepartmentDto> getDepartmentWithEmployees(
            @Parameter(description = "Department ID", required = true) @PathVariable Long id) {
        DepartmentDto department = departmentService.getDepartmentWithEmployees(id);
        return ResponseEntity.ok(department);
    }
    
    /**
     * Get departments that are over budget
     */
    @Operation(summary = "Get over-budget departments", description = "Retrieve departments that have exceeded their budget")
    @GetMapping("/over-budget")
    public ResponseEntity<List<DepartmentDto>> getOverBudgetDepartments() {
        List<DepartmentDto> departments = departmentService.getOverBudgetDepartments();
        return ResponseEntity.ok(departments);
    }
    
    /**
     * Get departments approaching budget limit
     */
    @Operation(summary = "Get near-limit departments", description = "Retrieve departments that are approaching their budget limit")
    @GetMapping("/near-limit")
    public ResponseEntity<List<DepartmentDto>> getNearLimitDepartments() {
        List<DepartmentDto> departments = departmentService.getNearLimitDepartments();
        return ResponseEntity.ok(departments);
    }
    
    /**
     * Get empty departments (with no employees)
     */
    @Operation(summary = "Get empty departments", description = "Retrieve departments that have no employees")
    @GetMapping("/empty")
    public ResponseEntity<List<DepartmentDto>> getEmptyDepartments() {
        List<DepartmentDto> departments = departmentService.getEmptyDepartments();
        return ResponseEntity.ok(departments);
    }
    
    /**
     * Get department statistics
     */
    @Operation(summary = "Get department statistics", description = "Retrieve aggregated statistics about all departments")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getDepartmentStatistics() {
        Map<String, Object> statistics = departmentService.getDepartmentStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Get budget analysis for a specific department
     */
    @Operation(summary = "Get department budget analysis", description = "Retrieve detailed budget analysis for a specific department")
    @GetMapping("/{id}/budget-analysis")
    public ResponseEntity<Map<String, Object>> getDepartmentBudgetAnalysis(
            @Parameter(description = "Department ID", required = true) @PathVariable Long id) {
        Map<String, Object> analysis = departmentService.getDepartmentBudgetAnalysis(id);
        return ResponseEntity.ok(analysis);
    }
    
    /**
     * Search departments by name
     */
    @Operation(summary = "Search departments by name", description = "Search for departments with names containing the specified keyword")
    @GetMapping("/search")
    public ResponseEntity<List<DepartmentDto>> searchDepartments(
            @Parameter(description = "Search keyword", required = true) @RequestParam String keyword) {
        List<DepartmentDto> departments = departmentService.searchDepartments(keyword);
        return ResponseEntity.ok(departments);
    }
    
    /**
     * Get fastest growing departments
     */
    @Operation(summary = "Get fastest growing departments", description = "Retrieve departments with the most new employees recently")
    @GetMapping("/fastest-growing")
    public ResponseEntity<List<Map<String, Object>>> getFastestGrowingDepartments() {
        List<Map<String, Object>> departments = departmentService.getFastestGrowingDepartments();
        return ResponseEntity.ok(departments);
    }

    /**
     * Create a new department
     */
    @Operation(summary = "Create new department", description = "Create a new department in the company")
    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(
            @Parameter(description = "Department details", required = true) @Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto createdDepartment = departmentService.createDepartment(departmentDto);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }

    /**
     * Update an existing department
     */
    @Operation(summary = "Update department", description = "Update an existing department's details")
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(
            @Parameter(description = "Department ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated department details", required = true) @Valid @RequestBody DepartmentDto departmentDto) {
        
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id, departmentDto);
        return ResponseEntity.ok(updatedDepartment);
    }

    /**
     * Delete a department
     */
    @Operation(summary = "Delete department", description = "Delete an existing department (only if it has no employees)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDepartment(
            @Parameter(description = "Department ID", required = true) @PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(new ApiResponse(true, "Department deleted successfully"));
    }
}
