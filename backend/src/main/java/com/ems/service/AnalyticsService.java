package com.ems.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.AnalyticsDto;
import com.ems.dto.AnalyticsDto.DepartmentBudgetAnalytics;
import com.ems.dto.AnalyticsDto.DistributionAnalytics;
import com.ems.dto.AnalyticsDto.EmployeeAnalytics;
import com.ems.dto.AnalyticsDto.EmployeeTimelineAnalytics;
import com.ems.dto.AnalyticsDto.LeaveAnalytics;
import com.ems.dto.AnalyticsDto.SalaryAnalytics;
import com.ems.dto.AnalyticsDto.SalaryAnalytics.DepartmentSalary;
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
        
        Double totalGross = salaryRepository.sumGrossSalaryByUser(currentUser);
        Double totalNet = salaryRepository.sumNetSalaryByUser(currentUser);
        Double averageSalary = salaryRepository.averageGrossSalaryByUser(currentUser);
        
        analytics.setTotalGross(totalGross != null ? totalGross : 0.0);
        analytics.setTotalNet(totalNet != null ? totalNet : 0.0);
        analytics.setAverageSalary(averageSalary != null ? averageSalary : 0.0);
        
        // Get department-wise salary distribution
        List<Object[]> departmentSalaries = salaryRepository.sumSalaryByDepartment(currentUser);
        List<DepartmentSalary> deptSalaries = new ArrayList<>();
        
        for (Object[] result : departmentSalaries) {
            DepartmentSalary ds = new DepartmentSalary();
            ds.setDepartment((String) result[0]);
            ds.setTotalSalary(((Number) result[1]).doubleValue());
            deptSalaries.add(ds);
        }
        
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
        
        long activeCount = employeeRepository.countByUserAndActiveStatusAndDate(user, true, null);
        long inactiveCount = employeeRepository.countByUserAndActiveStatusAndDate(user, false, null);
        
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
        
        long pendingCount = leaveRepository.countByUserAndStatus(user, Leave.Status.PENDING);
        long approvedCount = leaveRepository.countByUserAndStatus(user, Leave.Status.APPROVED);
        long deniedCount = leaveRepository.countByUserAndStatus(user, Leave.Status.DENIED);
        
        counts.add((int) pendingCount);
        counts.add((int) approvedCount);
        counts.add((int) deniedCount);
        
        distribution.setLabels(labels);
        distribution.setCounts(counts);
        
        return distribution;
    }

    private DistributionAnalytics getRoleDistribution(User user) {
        DistributionAnalytics distribution = new DistributionAnalytics();
        
        List<Object[]> roleData = employeeRepository.countEmployeesByRole(user);
        Map<String, Integer> roleMap = new HashMap<>();
        
        for (Object[] data : roleData) {
            String role = (String) data[0];
            long count = (long) data[1];
            roleMap.put(role, (int) count);
        }
        
        List<String> labels = new ArrayList<>(roleMap.keySet());
        List<Integer> counts = labels.stream().map(roleMap::get).collect(Collectors.toList());
        
        distribution.setLabels(labels);
        distribution.setCounts(counts);
        
        return distribution;
    }

    private DistributionAnalytics getContractTypeDistribution(User user) {
        DistributionAnalytics distribution = new DistributionAnalytics();
        
        List<Object[]> contractData = employeeRepository.countEmployeesByContractType(user);
        List<String> labels = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        
        for (Object[] data : contractData) {
            Employee.ContractType type = (Employee.ContractType) data[0];
            long count = (long) data[1];
            
            switch (type) {
                case FULL_TIME:
                    labels.add("Full-time");
                    break;
                case PART_TIME:
                    labels.add("Part-time");
                    break;
                case REMOTE:
                    labels.add("Remote");
                    break;
            }
            
            counts.add((int) count);
        }
        
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
            
            long activeCount = employeeRepository.countByUserAndActiveStatusAndDate(user, true, date);
            long inactiveCount = employeeRepository.countByUserAndActiveStatusAndDate(user, false, date);
            
            active.add((int) activeCount);
            inactive.add((int) inactiveCount);
        }
        
        timeline.setMonths(months);
        timeline.setActive(active);
        timeline.setInactive(inactive);
        
        return timeline;
    }
}
