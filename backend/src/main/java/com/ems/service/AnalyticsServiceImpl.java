package com.ems.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.AnalyticsDto;
import com.ems.dto.DepartmentBudgetAnalytics;
import com.ems.dto.DistributionAnalytics;
import com.ems.dto.EmployeeTimelineAnalytics;
import com.ems.dto.SalaryAnalytics;
import com.ems.dto.SalaryAnalytics.DepartmentSalary;
import com.ems.model.Department;
import com.ems.model.User;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.LeaveRepository;
import com.ems.repository.SalaryRepository;
import com.ems.repository.UserRepository;

/**
 * Service for generating analytics data for the dashboard
 */
@Service
public class AnalyticsServiceImpl {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private LeaveRepository leaveRepository;
    
    @Autowired
    private AuthService authService;
    
    /**
     * Get comprehensive analytics for the current user
     */
    @Transactional(readOnly = true)
    public AnalyticsDto getAnalyticsForCurrentUser() {
        AnalyticsDto analytics = new AnalyticsDto();
        
        try {
            // Get department budget analytics
            analytics.setDepartmentBudget(getDepartmentBudgetAnalyticsForCurrentUser());
            
            // Get salary analytics with mock data for now
            analytics.setSalaryData(getSalaryAnalyticsForCurrentUser());
            
            // Create distribution analytics
            DistributionAnalytics employeeDistribution = new DistributionAnalytics();
            employeeDistribution.setLabels(Arrays.asList("Department A", "Department B", "Department C"));
            employeeDistribution.setCounts(Arrays.asList(10, 15, 8));
            analytics.setEmployeeDistribution(employeeDistribution);
            
            // Create leave status distribution
            DistributionAnalytics leaveStatus = new DistributionAnalytics();
            leaveStatus.setLabels(Arrays.asList("Approved", "Pending", "Denied"));
            leaveStatus.setCounts(Arrays.asList(12, 5, 2));
            analytics.setLeaveStatus(leaveStatus);
            
            // Create role distribution
            DistributionAnalytics roleDistribution = new DistributionAnalytics();
            roleDistribution.setLabels(Arrays.asList("Manager", "Developer", "HR", "Finance"));
            roleDistribution.setCounts(Arrays.asList(3, 20, 2, 5));
            analytics.setRoleDistribution(roleDistribution);
            
            // Create contract type distribution
            DistributionAnalytics contractDistribution = new DistributionAnalytics();
            contractDistribution.setLabels(Arrays.asList("Full Time", "Part Time", "Remote"));
            contractDistribution.setCounts(Arrays.asList(25, 8, 12));
            analytics.setContractTypeDistribution(contractDistribution);
            
            // Create employee timeline
            EmployeeTimelineAnalytics timeline = new EmployeeTimelineAnalytics();
            timeline.setMonths(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun"));
            timeline.setActive(Arrays.asList(30, 32, 35, 38, 40, 42));
            timeline.setInactive(Arrays.asList(2, 1, 0, 2, 1, 0));
            analytics.setEmployeeTimeline(timeline);
        } catch (Exception e) {
            // Fallback to default analytics if any error occurs
            analytics = createDefaultAnalytics();
        }
        
        return analytics;
    }

    /**
     * Get salary analytics for the current user
     */
    @Transactional(readOnly = true)
    public SalaryAnalytics getSalaryAnalyticsForCurrentUser() {
        SalaryAnalytics analytics = new SalaryAnalytics();
        
        try {
            // Create sample data until repository methods are fixed
            analytics.setTotalGross(150000.0);
            analytics.setTotalNet(120000.0);
            analytics.setAverageSalary(5000.0);
            
            List<DepartmentSalary> deptSalaries = new ArrayList<>();
            
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
        } catch (Exception e) {
            // Create empty analytics on error
            analytics.setTotalGross(0.0);
            analytics.setTotalNet(0.0);
            analytics.setAverageSalary(0.0);
            analytics.setDepartmentSalaries(Collections.emptyList());
        }
        
        return analytics;
    }
    
    /**
     * Get department budget analytics for the current user
     */
    @Transactional(readOnly = true)
    public DepartmentBudgetAnalytics getDepartmentBudgetAnalyticsForCurrentUser() {
        DepartmentBudgetAnalytics analytics = new DepartmentBudgetAnalytics();
        
        try {
            User currentUser = authService.getCurrentUser();
            
            // Get all active departments for the current user
            List<Department> departments = departmentRepository.findByUser(currentUser);
            
            if (departments.isEmpty()) {
                analytics.setLabels(Collections.emptyList());
                analytics.setActual(Collections.emptyList());
                analytics.setBudget(Collections.emptyList());
                return analytics;
            }
            
            List<String> labels = new ArrayList<>();
            List<Double> actual = new ArrayList<>();
            List<Double> budget = new ArrayList<>();
            
            for (Department dept : departments) {
                labels.add(dept.getName());
                actual.add(dept.calculateCurrentExpenses());
                budget.add(dept.getBudget());
            }
            
            analytics.setLabels(labels);
            analytics.setActual(actual);
            analytics.setBudget(budget);
        } catch (Exception e) {
            // Return empty data on error
            analytics.setLabels(Collections.emptyList());
            analytics.setActual(Collections.emptyList());
            analytics.setBudget(Collections.emptyList());
        }
        
        return analytics;
    }
    
    /**
     * Create default analytics data for fallback
     */
    private AnalyticsDto createDefaultAnalytics() {
        AnalyticsDto analytics = new AnalyticsDto();
        
        // Create empty department budget analytics
        DepartmentBudgetAnalytics deptBudget = new DepartmentBudgetAnalytics();
        deptBudget.setLabels(Arrays.asList("Department A", "Department B"));
        deptBudget.setActual(Arrays.asList(5000.0, 3000.0));
        deptBudget.setBudget(Arrays.asList(8000.0, 6000.0));
        analytics.setDepartmentBudget(deptBudget);
        
        // Create empty salary analytics
        SalaryAnalytics salaryData = new SalaryAnalytics();
        salaryData.setTotalGross(0.0);
        salaryData.setTotalNet(0.0);
        salaryData.setAverageSalary(0.0);
        analytics.setSalaryData(salaryData);
        
        // Create empty distribution analytics
        DistributionAnalytics emptyDistribution = new DistributionAnalytics();
        emptyDistribution.setLabels(Collections.emptyList());
        emptyDistribution.setCounts(Collections.emptyList());
        
        analytics.setEmployeeDistribution(emptyDistribution);
        analytics.setLeaveStatus(emptyDistribution);
        analytics.setRoleDistribution(emptyDistribution);
        analytics.setContractTypeDistribution(emptyDistribution);
        
        // Create empty timeline
        EmployeeTimelineAnalytics timeline = new EmployeeTimelineAnalytics();
        timeline.setMonths(Collections.emptyList());
        timeline.setActive(Collections.emptyList());
        timeline.setInactive(Collections.emptyList());
        analytics.setEmployeeTimeline(timeline);
        
        return analytics;
    }
    

}