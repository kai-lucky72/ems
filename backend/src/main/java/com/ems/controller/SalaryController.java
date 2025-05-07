package com.ems.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.dto.SalaryDto;
import com.ems.service.SalaryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @GetMapping
    public ResponseEntity<List<SalaryDto>> getAllSalaries() {
        List<SalaryDto> salaries = salaryService.getAllSalariesForCurrentUser();
        return ResponseEntity.ok(salaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaryDto> getSalaryById(@PathVariable Long id) {
        SalaryDto salary = salaryService.getSalaryById(id);
        return ResponseEntity.ok(salary);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<SalaryDto> getSalaryByEmployeeId(@PathVariable Long employeeId) {
        SalaryDto salary = salaryService.getSalaryByEmployeeId(employeeId);
        return ResponseEntity.ok(salary);
    }

    @PostMapping
    public ResponseEntity<SalaryDto> createSalary(@Valid @RequestBody SalaryDto salaryDto) {
        SalaryDto createdSalary = salaryService.createSalary(salaryDto);
        return ResponseEntity.ok(createdSalary);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalaryDto> updateSalary(
            @PathVariable Long id,
            @Valid @RequestBody SalaryDto salaryDto) {
        
        SalaryDto updatedSalary = salaryService.updateSalary(id, salaryDto);
        return ResponseEntity.ok(updatedSalary);
    }
}
