package com.ems.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.AnalyticsDto;
import com.ems.dto.DepartmentBudgetAnalytics;
import com.ems.dto.DistributionAnalytics;
import com.ems.dto.EmployeeAnalytics;
import com.ems.dto.EmployeeTimelineAnalytics;
import com.ems.dto.LeaveAnalytics;
import com.ems.dto.SalaryAnalytics;
import com.ems.dto.SalaryAnalytics.DepartmentSalary;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.Leave;
import com.ems.model.User;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.LeaveRepository;
import com.ems.repository.SalaryRepository;

@Service
public class AnalyticsService {

    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private LeaveRepository leaveRepository;
    
    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public AnalyticsDto getAnalyticsForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        
        AnalyticsDto analytics = new AnalyticsDto();
        
        // Set department budget analytics
        analytics.setDepartmentBudget(getDepartmentAnalyticsForCurrentUser());
        
        // Set salary analytics
        analytics.setSalaryData(getSalaryAnalyticsForCurrentUser());
        
        // Set employee distribution analytics
        analytics.setEmployeeDistribution(getEmployeeStatusDistribution(currentUser));
        
        // Set leave status analytics
        analytics.setLeaveStatus(getLeaveStatusDistribution(currentUser));
        
        // Set role distribution analytics
        analytics.setRoleDistribution(getRoleDistribution(currentUser));
        
        // Set contract type distribution analytics
        analytics.setContractTypeDistribution(getContractTypeDistribution(currentUser));
        
        // Set employee timeline analytics
        analytics.setEmployeeTimeline(getEmployeeTimeline(currentUser));
        
        return analytics;
    }

    @Transactional(readOnly = true)
    public DepartmentBudgetAnalytics getDepartmentAnalyticsForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<Department> departments = departmentRepository.findByUser(currentUser);
        
        DepartmentBudgetAnalytics analytics = new DepartmentBudgetAnalytics();
        
        List<String> labels = new ArrayList<>();
        List<Double> actual = new ArrayList<>();
        List<Double> budget = new ArrayList<>();
        
        for (Department department : departments) {
            labels.add(department.getName());
            budget.add(department.getBudget());
            actual.add(department.calculateCurrentExpenses());
        }
        
        analytics.setLabels(labels);
        analytics.setActual(actual);
        analytics.setBudget(budget);
        
        return analytics;
    }

    @Transactional(readOnly = true)
    public SalaryAnalytics getSalaryAnalyticsForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        
        SalaryAnalytics analytics = new SalaryAnalytics();
        
        // TODO: Implement these repository methods
        Double totalGross = 0.0; // Mock data until repository methods are implemented
        Double totalNet = 0.0; // Mock data until repository methods are implemented
        Double averageSalary = 0.0; // Mock data until repository methods are implemented
        
        analytics.setTotalGross(totalGross != null ? totalGross : 0.0);
        analytics.setTotalNet(totalNet != null ? totalNet : 0.0);
        analytics.setAverageSalary(averageSalary != null ? averageSalary : 0.0);
        
        // TODO: Implement repository method
        // For now, create sample data
        List<DepartmentSalary> deptSalaries = new ArrayList<>();
        
        // Sample department salary data
        DepartmentSalary ds1 = new DepartmentSalary();
        ds1.setDepartment("Development");
        ds1.setTotalSalary(75000.0);
        deptSalaries.add(ds1);
        
        DepartmentSalary ds2 = new DepartmentSalary();
        ds2.setDepartment("Marketing");
        ds2.setTotalSalary(45000.0);
        deptSalaries.add(ds2);
        
        DepartmentSalary ds3 = new DepartmentSalary();
        ds3.setDepartment("HR");
        ds3.setTotalSalary(30000.0);
        deptSalaries.add(ds3);
        
        analytics.setDepartmentSalaries(deptSalaries);
        
        return analytics;
    }

    @Transactional(readOnly = true)
    public EmployeeAnalytics getEmployeeAnalyticsForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        
        EmployeeAnalytics analytics = new EmployeeAnalytics();
        
        // Set employee status distribution
        analytics.setStatusDistribution(getEmployeeStatusDistribution(currentUser));
        
        // Set role distribution
        analytics.setRoleDistribution(getRoleDistribution(currentUser));
        
        // Set contract type distribution
        analytics.setContractTypeDistribution(getContractTypeDistribution(currentUser));
        
        return analytics;
    }

    @Transactional(readOnly = true)
    public LeaveAnalytics getLeaveAnalyticsForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        
        LeaveAnalytics analytics = new LeaveAnalytics();
        
        // Set leave status distribution
        analytics.setStatusDistribution(getLeaveStatusDistribution(currentUser));
        
        return analytics;
    }

    // Helper methods to get various distributions
    private DistributionAnalytics getEmployeeStatusDistribution(User user) {
        DistributionAnalytics distribution = new DistributionAnalytics();
        
        List<String> labels = List.of("Active", "Inactive");
        List<Integer> counts = new ArrayList<>();
        
        // TODO: Implement repository method
        // For now, use sample data
        long activeCount = 30;
        long inactiveCount = 5;
        
        counts.add((int) activeCount);
        counts.add((int) inactiveCount);
        
        distribution.setLabels(labels);
        distribution.setCounts(counts);
        
        return distribution;
    }

    private DistributionAnalytics getLeaveStatusDistribution(User user) {
        DistributionAnalytics distribution = new DistributionAnalytics();
        
        List<String> labels = List.of("Pending", "Approved", "Denied");
        List<Integer> counts = new ArrayList<>();
        
        // TODO: Implement repository method
        // For now, use sample data
        long pendingCount = 12;
        long approvedCount = 5;
        long deniedCount = 2;
        
        counts.add((int) pendingCount);
        counts.add((int) approvedCount);
        counts.add((int) deniedCount);
        
        distribution.setLabels(labels);
        distribution.setCounts(counts);
        
        return distribution;
    }

    private DistributionAnalytics getRoleDistribution(User user) {
        DistributionAnalytics distribution = new DistributionAnalytics();
        
        // TODO: Implement repository method
        // For now, create sample data
        Map<String, Integer> roleMap = new HashMap<>();
        roleMap.put("Manager", 3);
        roleMap.put("Developer", 20);
        roleMap.put("HR", 2);
        roleMap.put("Finance", 5);
        
        List<String> labels = new ArrayList<>(roleMap.keySet());
        List<Integer> counts = labels.stream().map(roleMap::get).collect(Collectors.toList());
        
        distribution.setLabels(labels);
        distribution.setCounts(counts);
        
        return distribution;
    }

    private DistributionAnalytics getContractTypeDistribution(User user) {
        DistributionAnalytics distribution = new DistributionAnalytics();
        
        // TODO: Implement repository method
        // For now, create sample data
        List<String> labels = Arrays.asList("Full Time", "Part Time", "Remote");
        List<Integer> counts = Arrays.asList(25, 8, 12);
        
        distribution.setLabels(labels);
        distribution.setCounts(counts);
        
        return distribution;
    }

    private EmployeeTimelineAnalytics getEmployeeTimeline(User user) {
        EmployeeTimelineAnalytics timeline = new EmployeeTimelineAnalytics();
        
        // Get data for the last 6 months
        List<String> months = new ArrayList<>();
        List<Integer> active = new ArrayList<>();
        List<Integer> inactive = new ArrayList<>();
        
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        
        for (int i = 5; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            months.add(date.format(formatter));
            
            // TODO: Implement repository method
            // For now, use sample data with a slight increase each month
            long activeCount = 30 + i * 2; // Starts at 30, adds 2 each month
            long inactiveCount = Math.max(0, 2 - i / 2); // Starts at 2, gradually decreases
            
            active.add((int) activeCount);
            inactive.add((int) inactiveCount);
        }
        
        timeline.setMonths(months);
        timeline.setActive(active);
        timeline.setInactive(inactive);
        
        return timeline;
    }
}
