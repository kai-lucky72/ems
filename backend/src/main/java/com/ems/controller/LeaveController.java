package com.ems.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.ems.dto.LeaveDto;
import com.ems.model.Leave.Status;
import com.ems.service.LeaveService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST Controller for managing employee leave requests
 */
@RestController
@RequestMapping("/api/leaves")
@Tag(name = "Leave Management", description = "APIs for managing employee leave requests")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    /**
     * Get all leave requests
     */
    @Operation(summary = "Get all leave requests", description = "Retrieve a list of all leave requests in the company")
    @GetMapping
    public ResponseEntity<List<LeaveDto>> getAllLeaves() {
        List<LeaveDto> leaves = leaveService.getAllLeaves();
        return ResponseEntity.ok(leaves);
    }
    
    /**
     * Get paginated leave requests
     */
    @Operation(summary = "Get paginated leave requests", description = "Retrieve a paginated list of leave requests with sorting options")
    @GetMapping("/paginated")
    public ResponseEntity<Page<LeaveDto>> getPaginatedLeaves(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "requestDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "false") boolean ascending) {
        
        Page<LeaveDto> leaves = leaveService.getPaginatedLeaves(page, size, sortBy, ascending);
        return ResponseEntity.ok(leaves);
    }

    /**
     * Get leave by ID
     */
    @Operation(summary = "Get leave by ID", description = "Retrieve a specific leave request by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<LeaveDto> getLeaveById(
            @Parameter(description = "Leave ID", required = true) @PathVariable Long id) {
        LeaveDto leave = leaveService.getLeaveById(id);
        return ResponseEntity.ok(leave);
    }
    
    /**
     * Get leaves by employee
     */
    @Operation(summary = "Get leaves by employee", description = "Retrieve all leave requests for a specific employee")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveDto>> getLeavesByEmployee(
            @Parameter(description = "Employee ID", required = true) @PathVariable Long employeeId) {
        List<LeaveDto> leaves = leaveService.getLeavesByEmployee(employeeId);
        return ResponseEntity.ok(leaves);
    }
    
    /**
     * Get leaves by department
     */
    @Operation(summary = "Get leaves by department", description = "Retrieve all leave requests for employees in a specific department")
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<LeaveDto>> getLeavesByDepartment(
            @Parameter(description = "Department ID", required = true) @PathVariable Long departmentId) {
        List<LeaveDto> leaves = leaveService.getLeavesByDepartment(departmentId);
        return ResponseEntity.ok(leaves);
    }
    
    /**
     * Get leaves by status
     */
    @Operation(summary = "Get leaves by status", description = "Retrieve all leave requests with a specific status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeaveDto>> getLeavesByStatus(
            @Parameter(description = "Leave status (PENDING, APPROVED, DENIED)", required = true) @PathVariable Status status) {
        List<LeaveDto> leaves = leaveService.getLeavesByStatus(status);
        return ResponseEntity.ok(leaves);
    }
    
    /**
     * Get pending leaves
     */
    @Operation(summary = "Get pending leaves", description = "Retrieve all pending leave requests that need approval")
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveDto>> getPendingLeaves() {
        List<LeaveDto> leaves = leaveService.getPendingLeaves();
        return ResponseEntity.ok(leaves);
    }
    
    /**
     * Get current leaves (employees currently on leave)
     */
    @Operation(summary = "Get current leaves", description = "Retrieve all currently active approved leave requests")
    @GetMapping("/current")
    public ResponseEntity<List<LeaveDto>> getCurrentLeaves() {
        List<LeaveDto> leaves = leaveService.getCurrentLeaves();
        return ResponseEntity.ok(leaves);
    }
    
    /**
     * Get upcoming leaves
     */
    @Operation(summary = "Get upcoming leaves", description = "Retrieve all approved leave requests that haven't started yet")
    @GetMapping("/upcoming")
    public ResponseEntity<List<LeaveDto>> getUpcomingLeaves() {
        List<LeaveDto> leaves = leaveService.getUpcomingLeaves();
        return ResponseEntity.ok(leaves);
    }
    
    /**
     * Get leaves starting soon
     */
    @Operation(summary = "Get leaves starting soon", description = "Retrieve approved leave requests starting within the specified number of days")
    @GetMapping("/starting-soon")
    public ResponseEntity<List<LeaveDto>> getLeavesStartingSoon(
            @Parameter(description = "Days threshold") @RequestParam(defaultValue = "7") int days) {
        List<LeaveDto> leaves = leaveService.getLeavesStartingSoon(days);
        return ResponseEntity.ok(leaves);
    }
    
    /**
     * Get leaves ending soon
     */
    @Operation(summary = "Get leaves ending soon", description = "Retrieve approved leave requests ending within the specified number of days")
    @GetMapping("/ending-soon")
    public ResponseEntity<List<LeaveDto>> getLeavesEndingSoon(
            @Parameter(description = "Days threshold") @RequestParam(defaultValue = "7") int days) {
        List<LeaveDto> leaves = leaveService.getLeavesEndingSoon(days);
        return ResponseEntity.ok(leaves);
    }
    
    /**
     * Search leaves by employee name
     */
    @Operation(summary = "Search leaves by employee name", description = "Search for leave requests by employee name")
    @GetMapping("/search")
    public ResponseEntity<List<LeaveDto>> searchLeavesByEmployeeName(
            @Parameter(description = "Search term", required = true) @RequestParam String term) {
        List<LeaveDto> leaves = leaveService.searchLeavesByEmployeeName(term);
        return ResponseEntity.ok(leaves);
    }
    
    /**
     * Get leave statistics
     */
    @Operation(summary = "Get leave statistics", description = "Retrieve aggregated statistics about leave requests")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getLeaveStatistics() {
        Map<String, Object> statistics = leaveService.getLeaveStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Check for leave overlap
     */
    @Operation(summary = "Check leave overlap", description = "Check if a proposed leave period overlaps with existing approved leaves for an employee")
    @GetMapping("/check-overlap")
    public ResponseEntity<List<LeaveDto>> checkLeaveOverlap(
            @Parameter(description = "Employee ID", required = true) @RequestParam Long employeeId,
            @Parameter(description = "Start date", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<LeaveDto> overlappingLeaves = leaveService.checkLeaveOverlap(employeeId, startDate, endDate);
        return ResponseEntity.ok(overlappingLeaves);
    }

    /**
     * Create a new leave request
     */
    @Operation(summary = "Create leave request", description = "Submit a new leave request for approval")
    @PostMapping
    public ResponseEntity<LeaveDto> createLeave(
            @Parameter(description = "Leave request details", required = true) @Valid @RequestBody LeaveDto leaveDto) {
        LeaveDto createdLeave = leaveService.createLeave(leaveDto);
        return new ResponseEntity<>(createdLeave, HttpStatus.CREATED);
    }

    /**
     * Update a leave request
     */
    @Operation(summary = "Update leave request", description = "Update an existing pending leave request")
    @PutMapping("/{id}")
    public ResponseEntity<LeaveDto> updateLeave(
            @Parameter(description = "Leave ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated leave request details", required = true) @Valid @RequestBody LeaveDto leaveDto) {
        
        LeaveDto updatedLeave = leaveService.updateLeave(id, leaveDto);
        return ResponseEntity.ok(updatedLeave);
    }
    
    /**
     * Approve a leave request
     */
    @Operation(summary = "Approve leave request", description = "Approve a pending leave request")
    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveDto> approveLeave(
            @Parameter(description = "Leave ID", required = true) @PathVariable Long id) {
        
        LeaveDto approvedLeave = leaveService.approveLeave(id);
        return ResponseEntity.ok(approvedLeave);
    }
    
    /**
     * Deny a leave request
     */
    @Operation(summary = "Deny leave request", description = "Deny a pending leave request")
    @PutMapping("/{id}/deny")
    public ResponseEntity<LeaveDto> denyLeave(
            @Parameter(description = "Leave ID", required = true) @PathVariable Long id) {
        
        LeaveDto deniedLeave = leaveService.denyLeave(id);
        return ResponseEntity.ok(deniedLeave);
    }

    /**
     * Cancel a leave request
     */
    @Operation(summary = "Cancel leave request", description = "Cancel a pending leave request")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> cancelLeave(
            @Parameter(description = "Leave ID", required = true) @PathVariable Long id) {
        leaveService.cancelLeave(id);
        return ResponseEntity.ok(new ApiResponse(true, "Leave request cancelled successfully"));
    }
}