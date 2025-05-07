package com.ems.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.dto.AnalyticsDto;
import com.ems.dto.DepartmentBudgetAnalytics;
import com.ems.dto.SalaryAnalytics;
import com.ems.service.AnalyticsServiceImpl;

/**
 * Controller for analytics-related endpoints
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsServiceImpl analyticsService;

    /**
     * Get comprehensive analytics for dashboard
     */
    @GetMapping
    public ResponseEntity<AnalyticsDto> getAnalytics() {
        AnalyticsDto analytics = analyticsService.getAnalyticsForCurrentUser();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get department budget analytics
     */
    @GetMapping("/departments")
    public ResponseEntity<DepartmentBudgetAnalytics> getDepartmentAnalytics() {
        DepartmentBudgetAnalytics departmentAnalytics = 
                analyticsService.getDepartmentBudgetAnalyticsForCurrentUser();
        return ResponseEntity.ok(departmentAnalytics);
    }

    /**
     * Get salary analytics
     */
    @GetMapping("/salaries")
    public ResponseEntity<SalaryAnalytics> getSalaryAnalytics() {
        SalaryAnalytics salaryAnalytics = 
                analyticsService.getSalaryAnalyticsForCurrentUser();
        return ResponseEntity.ok(salaryAnalytics);
    }
}
