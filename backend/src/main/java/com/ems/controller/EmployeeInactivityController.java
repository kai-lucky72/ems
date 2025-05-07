package com.ems.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ems.dto.EmployeeInactivityDto;
import com.ems.model.EmployeeInactivity.InactivityType;
import com.ems.model.User;
import com.ems.service.EmployeeInactivityService;

/**
 * Controller for handling employee inactivity periods like leaves, medical absences, etc.
 */
@RestController
@RequestMapping("/api/employee-inactivity")
public class EmployeeInactivityController {

    @Autowired
    private EmployeeInactivityService employeeInactivityService;

    /**
     * Get all inactivity records for the current user's employees
     */
    @GetMapping
    public ResponseEntity<List<EmployeeInactivityDto>> getAllInactivities(@AuthenticationPrincipal User user) {
        List<EmployeeInactivityDto> inactivities = employeeInactivityService.getAllInactivitiesByUser(user);
        return ResponseEntity.ok(inactivities);
    }

    /**
     * Get a specific inactivity record by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeInactivityDto> getInactivityById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        EmployeeInactivityDto inactivity = employeeInactivityService.getInactivityById(id, user);
        return ResponseEntity.ok(inactivity);
    }

    /**
     * Get all inactivity records for a specific employee
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeInactivityDto>> getInactivitiesByEmployee(
            @PathVariable Long employeeId,
            @AuthenticationPrincipal User user) {
        List<EmployeeInactivityDto> inactivities = employeeInactivityService.getInactivitiesByEmployee(employeeId, user);
        return ResponseEntity.ok(inactivities);
    }

    /**
     * Get the current inactivity record for a specific employee
     */
    @GetMapping("/employee/{employeeId}/current")
    public ResponseEntity<EmployeeInactivityDto> getCurrentInactivityByEmployee(
            @PathVariable Long employeeId,
            @AuthenticationPrincipal User user) {
        Optional<EmployeeInactivityDto> inactivity = employeeInactivityService.getCurrentInactivityByEmployee(employeeId, user);
        return inactivity.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all current inactivity records
     */
    @GetMapping("/current")
    public ResponseEntity<List<EmployeeInactivityDto>> getCurrentInactivities(@AuthenticationPrincipal User user) {
        List<EmployeeInactivityDto> inactivities = employeeInactivityService.getCurrentInactivities(user);
        return ResponseEntity.ok(inactivities);
    }

    /**
     * Get inactivities by type (e.g., MEDICAL, PERSONAL, etc.)
     */
    @GetMapping("/by-type/{type}")
    public ResponseEntity<List<EmployeeInactivityDto>> getInactivitiesByType(
            @PathVariable InactivityType type,
            @AuthenticationPrincipal User user) {
        List<EmployeeInactivityDto> inactivities = employeeInactivityService.getInactivitiesByType(type, user);
        return ResponseEntity.ok(inactivities);
    }

    /**
     * Get inactivities by date range
     */
    @GetMapping("/by-date-range")
    public ResponseEntity<List<EmployeeInactivityDto>> getInactivitiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal User user) {
        List<EmployeeInactivityDto> inactivities = employeeInactivityService.getInactivitiesByDateRange(startDate, endDate, user);
        return ResponseEntity.ok(inactivities);
    }

    /**
     * Get statistics about inactivities
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getInactivityStatistics(@AuthenticationPrincipal User user) {
        Map<String, Object> statistics = employeeInactivityService.getInactivityStatistics(user);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get inactivities grouped by month
     */
    @GetMapping("/stats/monthly")
    public ResponseEntity<List<Object[]>> getInactivitiesByMonth(@AuthenticationPrincipal User user) {
        List<Object[]> stats = employeeInactivityService.getInactivitiesByMonth(user);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get inactivities grouped by department
     */
    @GetMapping("/stats/by-department")
    public ResponseEntity<List<Object[]>> getInactivitiesByDepartment(@AuthenticationPrincipal User user) {
        List<Object[]> stats = employeeInactivityService.getInactivitiesByDepartment(user);
        return ResponseEntity.ok(stats);
    }

    /**
     * Create a new inactivity record
     */
    @PostMapping
    public ResponseEntity<EmployeeInactivityDto> createInactivity(
            @Valid @RequestBody EmployeeInactivityDto inactivityDto,
            @AuthenticationPrincipal User user) {
        EmployeeInactivityDto createdInactivity = employeeInactivityService.createInactivity(inactivityDto, user);
        return new ResponseEntity<>(createdInactivity, HttpStatus.CREATED);
    }

    /**
     * Update an existing inactivity record
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeInactivityDto> updateInactivity(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeInactivityDto inactivityDto,
            @AuthenticationPrincipal User user) {
        EmployeeInactivityDto updatedInactivity = employeeInactivityService.updateInactivity(id, inactivityDto, user);
        return ResponseEntity.ok(updatedInactivity);
    }

    /**
     * Delete an inactivity record
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInactivity(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        employeeInactivityService.deleteInactivity(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * End an ongoing inactivity period by setting an end date
     */
    @PatchMapping("/{id}/end")
    public ResponseEntity<EmployeeInactivityDto> endInactivity(
            @PathVariable Long id,
            @RequestBody Map<String, String> endDateMap,
            @AuthenticationPrincipal User user) {
        
        LocalDate endDate;
        try {
            endDate = LocalDate.parse(endDateMap.get("endDate"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        
        EmployeeInactivityDto updatedInactivity = employeeInactivityService.endInactivity(id, endDate, user);
        return ResponseEntity.ok(updatedInactivity);
    }
    
    /**
     * Find employees returning from inactivity in the next X days
     */
    @GetMapping("/returning")
    public ResponseEntity<List<EmployeeInactivityDto>> getUpcomingReturns(
            @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal User user) {
        LocalDate futureDate = LocalDate.now().plusDays(days);
        List<EmployeeInactivityDto> inactivities = employeeInactivityService.getInactivitiesByDateRange(
                LocalDate.now(), futureDate, user);
        return ResponseEntity.ok(inactivities);
    }
    
    /**
     * Find employees who have been inactive for more than X days
     */
    @GetMapping("/long-term")
    public ResponseEntity<List<EmployeeInactivityDto>> getLongTermInactivities(
            @RequestParam(defaultValue = "30") int days,
            @AuthenticationPrincipal User user) {
        LocalDate pastDate = LocalDate.now().minusDays(days);
        List<EmployeeInactivityDto> inactivities = employeeInactivityService.getInactivitiesByDateRange(
                pastDate, LocalDate.now(), user);
        return ResponseEntity.ok(inactivities);
    }
}