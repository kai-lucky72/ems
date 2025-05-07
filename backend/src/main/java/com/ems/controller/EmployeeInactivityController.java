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
import com.ems.model.User;
import com.ems.service.EmployeeInactivityService;

@RestController
@RequestMapping("/api/employee-inactivity")
public class EmployeeInactivityController {

    @Autowired
    private EmployeeInactivityService employeeInactivityService;

    @GetMapping
    public ResponseEntity<List<EmployeeInactivityDto>> getAllInactivities(@AuthenticationPrincipal User user) {
        List<EmployeeInactivityDto> inactivities = employeeInactivityService.getAllInactivitiesByUser(user);
        return ResponseEntity.ok(inactivities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeInactivityDto> getInactivityById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        EmployeeInactivityDto inactivity = employeeInactivityService.getInactivityById(id, user);
        return ResponseEntity.ok(inactivity);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeInactivityDto>> getInactivitiesByEmployee(
            @PathVariable Long employeeId,
            @AuthenticationPrincipal User user) {
        List<EmployeeInactivityDto> inactivities = employeeInactivityService.getInactivitiesByEmployee(employeeId, user);
        return ResponseEntity.ok(inactivities);
    }

    @GetMapping("/employee/{employeeId}/current")
    public ResponseEntity<EmployeeInactivityDto> getCurrentInactivityByEmployee(
            @PathVariable Long employeeId,
            @AuthenticationPrincipal User user) {
        Optional<EmployeeInactivityDto> inactivity = employeeInactivityService.getCurrentInactivityByEmployee(employeeId, user);
        return inactivity.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/current")
    public ResponseEntity<List<EmployeeInactivityDto>> getCurrentInactivities(@AuthenticationPrincipal User user) {
        List<EmployeeInactivityDto> inactivities = employeeInactivityService.getCurrentInactivities(user);
        return ResponseEntity.ok(inactivities);
    }

    @GetMapping("/stats/monthly")
    public ResponseEntity<List<Object[]>> getInactivitiesByMonth(@AuthenticationPrincipal User user) {
        List<Object[]> stats = employeeInactivityService.getInactivitiesByMonth(user);
        return ResponseEntity.ok(stats);
    }

    @PostMapping
    public ResponseEntity<EmployeeInactivityDto> createInactivity(
            @Valid @RequestBody EmployeeInactivityDto inactivityDto,
            @AuthenticationPrincipal User user) {
        EmployeeInactivityDto createdInactivity = employeeInactivityService.createInactivity(inactivityDto, user);
        return new ResponseEntity<>(createdInactivity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeInactivityDto> updateInactivity(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeInactivityDto inactivityDto,
            @AuthenticationPrincipal User user) {
        EmployeeInactivityDto updatedInactivity = employeeInactivityService.updateInactivity(id, inactivityDto, user);
        return ResponseEntity.ok(updatedInactivity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInactivity(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        employeeInactivityService.deleteInactivity(id, user);
        return ResponseEntity.noContent().build();
    }

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
}