package com.ems.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.dto.EmployeeDto;
import com.ems.service.EmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        List<EmployeeDto> employees = employeeService.getAllEmployeesForCurrentUser();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        EmployeeDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
        return ResponseEntity.ok(createdEmployee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDto employeeDto) {
        
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EmployeeDto> updateEmployeeStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> status) {
        
        boolean isActive = (boolean) status.get("isActive");
        String inactiveFrom = status.get("inactiveFrom") != null ? status.get("inactiveFrom").toString() : null;
        String inactiveTo = status.get("inactiveTo") != null ? status.get("inactiveTo").toString() : null;
        
        EmployeeDto updatedEmployee = employeeService.updateEmployeeStatus(id, isActive, inactiveFrom, inactiveTo);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
