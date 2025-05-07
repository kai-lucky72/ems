package com.ems.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.dto.AnalyticsDto;
import com.ems.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<AnalyticsDto> getAnalytics() {
        AnalyticsDto analytics = analyticsService.getAnalyticsForCurrentUser();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/departments")
    public ResponseEntity<AnalyticsDto.DepartmentBudgetAnalytics> getDepartmentAnalytics() {
        AnalyticsDto.DepartmentBudgetAnalytics departmentAnalytics = 
                analyticsService.getDepartmentAnalyticsForCurrentUser();
        return ResponseEntity.ok(departmentAnalytics);
    }

    @GetMapping("/salaries")
    public ResponseEntity<AnalyticsDto.SalaryAnalytics> getSalaryAnalytics() {
        AnalyticsDto.SalaryAnalytics salaryAnalytics = 
                analyticsService.getSalaryAnalyticsForCurrentUser();
        return ResponseEntity.ok(salaryAnalytics);
    }

    @GetMapping("/employees")
    public ResponseEntity<AnalyticsDto.EmployeeAnalytics> getEmployeeAnalytics() {
        AnalyticsDto.EmployeeAnalytics employeeAnalytics = 
                analyticsService.getEmployeeAnalyticsForCurrentUser();
        return ResponseEntity.ok(employeeAnalytics);
    }

    @GetMapping("/leaves")
    public ResponseEntity<AnalyticsDto.LeaveAnalytics> getLeaveAnalytics() {
        AnalyticsDto.LeaveAnalytics leaveAnalytics = 
                analyticsService.getLeaveAnalyticsForCurrentUser();
        return ResponseEntity.ok(leaveAnalytics);
    }
}
