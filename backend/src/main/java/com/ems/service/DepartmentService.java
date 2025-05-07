package com.ems.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.DepartmentDto;
import com.ems.dto.DepartmentDto.SalaryTrendDto;
import com.ems.dto.EmployeeDto;
import com.ems.exception.BadRequestException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.Salary;
import com.ems.model.User;
import com.ems.model.Department.BudgetType;
import com.ems.model.Employee.Status;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.SalaryRepository;

/**
 * Service class for managing departments
 */
@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private EmployeeService employeeService;
    
    /**
     * Utility method to handle List<Boolean> return types from repository methods
     * @param booleanList The list returned from repository
     * @return true if the list contains at least one true value, false otherwise
     */
    private boolean getBooleanResult(List<Boolean> booleanList) {
        return booleanList != null && !booleanList.isEmpty() && booleanList.get(0);
    }

    /**
     * Get all departments for the current user's company
     */
    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartmentsForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<Department> departments = departmentRepository.findByUser(currentUser);
        
        return departments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get departments sorted by name (ascending)
     */
    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartmentsSortedByName() {
        User currentUser = authService.getCurrentUser();
        List<Department> departments = departmentRepository.findByUserOrderByNameAsc(currentUser);
        
        return departments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get departments sorted by budget (descending)
     */
    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartmentsSortedByBudget() {
        User currentUser = authService.getCurrentUser();
        List<Department> departments = departmentRepository.findByUserOrderByBudgetDesc(currentUser);
        
        return departments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get paginated departments
     */
    @Transactional(readOnly = true)
    public Page<DepartmentDto> getPaginatedDepartments(int page, int size, String sortBy, boolean ascending) {
        User currentUser = authService.getCurrentUser();
        Sort sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Department> departmentPage = departmentRepository.findByUser(currentUser, pageable);
        
        return departmentPage.map(this::convertToDto);
    }

    /**
     * Get a specific department by ID
     */
    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentById(Long id) {
        User currentUser = authService.getCurrentUser();
        Department department = departmentRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        
        return convertToDto(department);
    }
    
    /**
     * Get detailed department information with employee data
     */
    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentWithEmployees(Long id) {
        User currentUser = authService.getCurrentUser();
        Department department = departmentRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        
        DepartmentDto dto = convertToDto(department);
        
        // Fetch and add employees
        List<Employee> employees = new ArrayList<>(department.getEmployees());
        List<EmployeeDto> employeeDtos = employees.stream()
                .map(employeeService::convertToDto)
                .collect(Collectors.toList());
        
        dto.setEmployees(employeeDtos);
        
        // Set counts
        dto.setEmployeeCount(employees.size());
        dto.setActiveEmployeeCount((int) employees.stream()
                .filter(Employee::isActive)
                .count());
        
        return dto;
    }
    
    /**
     * Get departments that are over budget
     */
    @Transactional(readOnly = true)
    public List<DepartmentDto> getOverBudgetDepartments() {
        User currentUser = authService.getCurrentUser();
        List<Department> overBudgetDepartments = departmentRepository.findOverBudgetDepartments(currentUser);
        
        return overBudgetDepartments.stream()
                .map(department -> {
                    DepartmentDto dto = convertToDto(department);
                    dto.calculateBudgetUsage();
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get departments that are approaching their budget limit
     */
    @Transactional(readOnly = true)
    public List<DepartmentDto> getNearLimitDepartments() {
        User currentUser = authService.getCurrentUser();
        List<Department> nearLimitDepartments = departmentRepository.findNearBudgetLimitDepartments(currentUser);
        
        return nearLimitDepartments.stream()
                .map(department -> {
                    DepartmentDto dto = convertToDto(department);
                    dto.calculateBudgetUsage();
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get departments with no employees
     */
    @Transactional(readOnly = true)
    public List<DepartmentDto> getEmptyDepartments() {
        User currentUser = authService.getCurrentUser();
        List<Department> emptyDepartments = departmentRepository.findEmptyDepartmentsByUser(currentUser);
        
        return emptyDepartments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get department statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDepartmentStatistics() {
        User currentUser = authService.getCurrentUser();
        Map<String, Object> statistics = new HashMap<>();
        
        // Basic stats
        List<Long> departmentCountList = departmentRepository.countByUser(currentUser);
        long totalDepartments = departmentCountList.isEmpty() ? 0L : departmentCountList.get(0);
        List<Double> totalBudgetList = departmentRepository.sumBudgetByUser(currentUser);
        Double totalBudget = totalBudgetList.isEmpty() ? 0.0 : totalBudgetList.get(0);
        
        statistics.put("totalDepartments", totalDepartments);
        statistics.put("totalBudget", totalBudget);
        
        // Budget types breakdown
        List<Object[]> budgetTypeStats = departmentRepository.sumBudgetByTypeAndUser(currentUser);
        Map<String, Double> budgetByType = new HashMap<>();
        
        for (Object[] stat : budgetTypeStats) {
            BudgetType type = (BudgetType) stat[0];
            Double amount = (Double) stat[1];
            budgetByType.put(type.toString(), amount);
        }
        
        statistics.put("budgetByType", budgetByType);
        
        // Employee distribution
        List<Object[]> employeeStats = departmentRepository.countEmployeesByDepartment(currentUser);
        List<Map<String, Object>> employeeDistribution = new ArrayList<>();
        
        for (Object[] stat : employeeStats) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("departmentName", stat[0]);
            entry.put("employeeCount", stat[1]);
            employeeDistribution.add(entry);
        }
        
        statistics.put("employeeDistribution", employeeDistribution);
        
        // Budget analysis
        List<Object[]> budgetReport = departmentRepository.getDepartmentBudgetReport(currentUser);
        List<Map<String, Object>> budgetAnalysis = new ArrayList<>();
        
        for (Object[] report : budgetReport) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("departmentName", report[0]);
            entry.put("budgetType", report[1]);
            entry.put("budget", report[2]);
            entry.put("totalSalary", report[3] != null ? report[3] : 0.0);
            
            double budget = (Double) report[2];
            double totalSalary = report[3] != null ? (Double) report[3] : 0.0;
            double usagePercentage = budget > 0 ? (totalSalary / budget) * 100 : 0.0;
            
            entry.put("usagePercentage", usagePercentage);
            entry.put("overBudget", usagePercentage > 100);
            
            budgetAnalysis.add(entry);
        }
        
        statistics.put("budgetAnalysis", budgetAnalysis);
        
        return statistics;
    }
    
    /**
     * Get detailed budget and salary statistics for a specific department
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDepartmentBudgetAnalysis(Long departmentId) {
        User currentUser = authService.getCurrentUser();
        Department department = departmentRepository.findByIdAndUser(departmentId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
        
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("departmentId", department.getId());
        analysis.put("departmentName", department.getName());
        analysis.put("budget", department.getBudget());
        analysis.put("budgetType", department.getBudgetType());
        
        double currentExpenses = department.calculateCurrentExpenses();
        analysis.put("currentExpenses", currentExpenses);
        
        double budgetUsagePercentage = department.getBudget() > 0 ? 
                (currentExpenses / department.getBudget()) * 100 : 0.0;
        analysis.put("budgetUsagePercentage", budgetUsagePercentage);
        analysis.put("overBudget", budgetUsagePercentage > 100);
        
        // Get employees and their salaries
        List<Employee> employees = new ArrayList<>(department.getEmployees());
        analysis.put("employeeCount", employees.size());
        analysis.put("activeEmployeeCount", employees.stream().filter(Employee::isActive).count());
        
        // Collect salary data
        List<Salary> currentSalaries = salaryRepository.findCurrentSalariesByDepartment(department);
        
        Double totalGrossSalary = currentSalaries.stream()
                .mapToDouble(Salary::getGrossSalary)
                .sum();
        analysis.put("totalGrossSalary", totalGrossSalary);
        
        Double totalNetSalary = currentSalaries.stream()
                .mapToDouble(Salary::getNetSalary)
                .sum();
        analysis.put("totalNetSalary", totalNetSalary);
        
        Double averageGrossSalary = !currentSalaries.isEmpty() ? 
                totalGrossSalary / currentSalaries.size() : 0.0;
        analysis.put("averageGrossSalary", averageGrossSalary);
        
        // Historical salary trends
        List<SalaryTrendDto> trends = buildSalaryTrends(department);
        analysis.put("salaryTrends", trends);
        
        return analysis;
    }
    
    /**
     * Create a new department
     */
    @Transactional
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        User currentUser = authService.getCurrentUser();
        
        // Check if department with same name already exists for the user
        if (getBooleanResult(departmentRepository.existsByNameAndUser(departmentDto.getName(), currentUser))) {
            throw new BadRequestException("Department with this name already exists");
        }
        
        Department department = new Department();
        department.setName(departmentDto.getName());
        department.setBudget(departmentDto.getBudget());
        department.setBudgetType(departmentDto.getBudgetType());
        department.setUser(currentUser);
        
        Department savedDepartment = departmentRepository.save(department);
        return convertToDto(savedDepartment);
    }

    /**
     * Update an existing department
     */
    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentDto departmentDto) {
        User currentUser = authService.getCurrentUser();
        Department department = departmentRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        
        // Check if department with same name already exists (excluding this one)
        if (!department.getName().equals(departmentDto.getName()) && 
            getBooleanResult(departmentRepository.existsByNameAndUser(departmentDto.getName(), currentUser))) {
            throw new BadRequestException("Department with this name already exists");
        }
        
        // Check if new budget is too low for current expenses
        if (departmentDto.getBudget() < department.calculateCurrentExpenses()) {
            throw new BadRequestException("New budget is less than current salary expenses. " +
                    "Please review employee salaries before reducing the budget.");
        }
        
        department.setName(departmentDto.getName());
        department.setBudget(departmentDto.getBudget());
        department.setBudgetType(departmentDto.getBudgetType());
        
        Department updatedDepartment = departmentRepository.save(department);
        return convertToDto(updatedDepartment);
    }

    /**
     * Delete a department
     */
    @Transactional
    public void deleteDepartment(Long id) {
        User currentUser = authService.getCurrentUser();
        Department department = departmentRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        
        // Check if department has employees
        if (!department.getEmployees().isEmpty()) {
            throw new BadRequestException("Cannot delete department with employees. " +
                    "Please reassign or remove all employees first.");
        }
        
        departmentRepository.delete(department);
    }
    
    /**
     * Search departments by name
     */
    @Transactional(readOnly = true)
    public List<DepartmentDto> searchDepartments(String keyword) {
        User currentUser = authService.getCurrentUser();
        List<Department> departments = departmentRepository.searchByNameContaining(currentUser, keyword);
        
        return departments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get fastest growing departments (most new employees)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getFastestGrowingDepartments() {
        User currentUser = authService.getCurrentUser();
        List<Object[]> growingDepts = departmentRepository.findFastestGrowingDepartments(currentUser);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] deptData : growingDepts) {
            Department dept = (Department) deptData[0];
            Long newEmployeeCount = (Long) deptData[1];
            
            Map<String, Object> deptInfo = new HashMap<>();
            deptInfo.put("departmentId", dept.getId());
            deptInfo.put("departmentName", dept.getName());
            deptInfo.put("newEmployeeCount", newEmployeeCount);
            deptInfo.put("totalEmployeeCount", dept.getEmployees().size());
            deptInfo.put("growthPercentage", dept.getEmployees().size() > 0 ? 
                    (newEmployeeCount.doubleValue() / dept.getEmployees().size()) * 100 : 0);
            
            result.add(deptInfo);
        }
        
        return result;
    }

    /**
     * Helper method to convert Entity to DTO with enhanced information
     */
    public DepartmentDto convertToDto(Department department) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setBudget(department.getBudget());
        dto.setBudgetType(department.getBudgetType());
        dto.setCreatedAt(department.getCreatedAt());
        
        // Calculate current expenses and budget usage
        Double currentExpenses = department.calculateCurrentExpenses();
        dto.setCurrentExpenses(currentExpenses);
        
        // Set employee counts
        dto.setEmployeeCount(department.getEmployees().size());
        dto.setActiveEmployeeCount((int) department.getEmployees().stream()
                .filter(Employee::isActive)
                .count());
        
        // Calculate budget usage percentage
        if (department.getBudget() > 0) {
            double usagePercentage = (currentExpenses / department.getBudget()) * 100;
            dto.setBudgetUsagePercentage(usagePercentage);
            dto.setIsBudgetOverrun(usagePercentage > 100);
        } else {
            dto.setBudgetUsagePercentage(0.0);
            dto.setIsBudgetOverrun(false);
        }
        
        return dto;
    }
    
    /**
     * Build salary trends for a department
     */
    private List<SalaryTrendDto> buildSalaryTrends(Department department) {
        List<SalaryTrendDto> trends = new ArrayList<>();
        
        // Get current date info
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        
        // Look back 12 months
        for (int i = 0; i < 12; i++) {
            int month = currentMonth - i;
            int year = currentYear;
            
            if (month <= 0) {
                month += 12;
                year -= 1;
            }
            
            // Collect salaries for this month/year
            double totalSalary = 0.0;
            int employeeCount = 0;
            
            for (Employee employee : department.getEmployees()) {
                for (Salary salary : employee.getSalaries()) {
                    if (salary.getSalaryYear() == year && salary.getSalaryMonth() == month) {
                        totalSalary += salary.getGrossSalary();
                        employeeCount++;
                    }
                }
            }
            
            // Only add periods with salary data
            if (employeeCount > 0) {
                SalaryTrendDto trend = new SalaryTrendDto(year, month, totalSalary, employeeCount);
                trends.add(trend);
            }
        }
        
        return trends;
    }
}
