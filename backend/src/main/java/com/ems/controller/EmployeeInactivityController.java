package com.ems.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ems.dto.EmployeeInactivityDto;
import com.ems.service.EmployeeInactivityService;

@RestController
@RequestMapping("/employee-inactivity")
public class EmployeeInactivityController {

    @Autowired
    private EmployeeInactivityService employeeInactivityService;
    
    @GetMapping
    public ResponseEntity<List<EmployeeInactivityDto>> getAllInactivities() {
        List<EmployeeInactivityDto> inactivities = employeeInactivityService.getAllInactivitiesForCurrentUser();
        return ResponseEntity.ok(inactivities);
    }
    
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeInactivityDto>> getInactivitiesForEmployee(@PathVariable Long employeeId) {
        List<EmployeeInactivityDto> inactivities = employeeInactivityService.getInactivitiesForEmployee(employeeId);
        return ResponseEntity.ok(inactivities);
    }
    
    @PostMapping
    public ResponseEntity<EmployeeInactivityDto> createInactivity(@RequestBody EmployeeInactivityDto inactivityDto) {
        EmployeeInactivityDto createdInactivity = employeeInactivityService.createInactivity(inactivityDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInactivity);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeInactivityDto> updateInactivity(
            @PathVariable Long id,
            @RequestBody EmployeeInactivityDto inactivityDto) {
        EmployeeInactivityDto updatedInactivity = employeeInactivityService.updateInactivity(id, inactivityDto);
        return ResponseEntity.ok(updatedInactivity);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInactivity(@PathVariable Long id) {
        employeeInactivityService.deleteInactivity(id);
        return ResponseEntity.noContent().build();
    }
}