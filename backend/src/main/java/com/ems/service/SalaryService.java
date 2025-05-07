package com.ems.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.DeductionDto;
import com.ems.dto.SalaryDto;
import com.ems.exception.BadRequestException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Deduction;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.EmployeeInactivity;
import com.ems.model.Salary;
import com.ems.model.User;
import com.ems.model.Employee.Status;
import com.ems.model.EmployeeInactivity.InactivityType;
import com.ems.repository.DeductionRepository;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeInactivityRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.SalaryRepository;

/**
 * Service class for managing salary records and payroll processing
 */
@Service
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DeductionRepository deductionRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private EmployeeInactivityRepository inactivityRepository;
    
    @Autowired
    private AuthService authService;

    /**
     * Get all salaries for the current user's company
     */
    @Transactional(readOnly = true)
    public List<SalaryDto> getAllSalariesForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<Salary> salaries = salaryRepository.findByUser(currentUser);
        
        return salaries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all salaries for a specific month and year
     */
    @Transactional(readOnly = true)
    public List<SalaryDto> getSalariesByMonthAndYear(Integer month, Integer year) {
        User currentUser = authService.getCurrentUser();
        
        if (month < 1 || month > 12) {
            throw new BadRequestException("Invalid month. Must be between 1 and 12");
        }
        
        if (year < 2000 || year > 2100) {
            throw new BadRequestException("Invalid year. Must be between 2000 and 2100");
        }
        
        List<Salary> salaries = salaryRepository.findByUserAndYearAndMonth(currentUser, year, month);
        return salaries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get current month's salaries
     */
    @Transactional(readOnly = true)
    public List<SalaryDto> getCurrentMonthSalaries() {
        LocalDate now = LocalDate.now();
        return getSalariesByMonthAndYear(now.getMonthValue(), now.getYear());
    }
    
    /**
     * Get all unique salary periods (month/year combinations)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSalaryPeriods() {
        User currentUser = authService.getCurrentUser();
        List<Object[]> periods = salaryRepository.findDistinctSalaryMonthsAndYears(currentUser);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] period : periods) {
            Map<String, Object> periodMap = new HashMap<>();
            Integer year = (Integer) period[0];
            Integer month = (Integer) period[1];
            periodMap.put("year", year);
            periodMap.put("month", month);
            
            // Add formatted month name
            String monthName = Month.of(month).toString();
            periodMap.put("monthName", monthName.substring(0, 1) + monthName.substring(1).toLowerCase());
            
            // Count salaries in this period
            List<Salary> salaries = salaryRepository.findByUserAndYearAndMonth(currentUser, year, month);
            periodMap.put("count", salaries.size());
            
            result.add(periodMap);
        }
        
        return result;
    }

    /**
     * Get a specific salary by ID
     */
    @Transactional(readOnly = true)
    public SalaryDto getSalaryById(Long id) {
        User currentUser = authService.getCurrentUser();
        List<Salary> salaries = salaryRepository.findByIdAndUser(id, currentUser);
        
        if (salaries.isEmpty()) {
            throw new ResourceNotFoundException("Salary not found with id: " + id);
        }
        
        return convertToDto(salaries.get(0));
    }

    /**
     * Get a specific employee's current salary
     */
    @Transactional(readOnly = true)
    public SalaryDto getCurrentSalaryByEmployeeId(Long employeeId) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(employeeId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        // Get most recent salary for this employee
        List<Salary> salaries = salaryRepository.findByEmployeeIdAndUser(employeeId, currentUser);
        if (salaries.isEmpty()) {
            throw new ResourceNotFoundException("Salary not found for employee with id: " + employeeId);
        }
        
        // Return the most recent one
        return convertToDto(salaries.get(0));
    }
    
    /**
     * Get all salary history for an employee
     */
    @Transactional(readOnly = true)
    public List<SalaryDto> getSalaryHistoryByEmployeeId(Long employeeId) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(employeeId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        List<Salary> salaries = salaryRepository.findByEmployeeIdAndUser(employeeId, currentUser);
        return salaries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get top earners in the company
     */
    @Transactional(readOnly = true)
    public List<SalaryDto> getTopEarners(int limit) {
        User currentUser = authService.getCurrentUser();
        
        List<Salary> topEarners = salaryRepository.findTopEarners(
                currentUser, 
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "grossSalary"))
        );
        
        return topEarners.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get salary statistics for a department
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDepartmentSalaryStats(Long departmentId) {
        User currentUser = authService.getCurrentUser();
        Department department = departmentRepository.findByIdAndUser(departmentId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
        
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        
        // Get current salaries for this department
        List<Salary> salaries = salaryRepository.findCurrentSalariesByDepartment(department);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("departmentId", department.getId());
        stats.put("departmentName", department.getName());
        stats.put("employeeCount", salaries.size());
        
        // Calculate totals
        double totalGross = 0.0;
        double totalNet = 0.0;
        double minSalary = Double.MAX_VALUE;
        double maxSalary = 0.0;
        
        for (Salary salary : salaries) {
            totalGross += salary.getGrossSalary();
            totalNet += salary.getNetSalary();
            
            if (salary.getGrossSalary() < minSalary) {
                minSalary = salary.getGrossSalary();
            }
            if (salary.getGrossSalary() > maxSalary) {
                maxSalary = salary.getGrossSalary();
            }
        }
        
        stats.put("totalGrossSalary", totalGross);
        stats.put("totalNetSalary", totalNet);
        stats.put("averageGrossSalary", salaries.isEmpty() ? 0 : totalGross / salaries.size());
        stats.put("averageNetSalary", salaries.isEmpty() ? 0 : totalNet / salaries.size());
        stats.put("minSalary", salaries.isEmpty() ? 0 : minSalary);
        stats.put("maxSalary", salaries.isEmpty() ? 0 : maxSalary);
        
        // Budget information
        stats.put("budget", department.getBudget());
        stats.put("budgetType", department.getBudgetType());
        stats.put("salaryToBudgetRatio", department.getBudget() == 0 ? 0 : totalGross / department.getBudget());
        
        return stats;
    }

    /**
     * Create a new salary record for an employee
     */
    @Transactional
    public SalaryDto createSalary(SalaryDto salaryDto) {
        User currentUser = authService.getCurrentUser();
        
        // Validate employee
        Employee employee = employeeRepository.findByIdAndUser(salaryDto.getEmployeeId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + salaryDto.getEmployeeId()));
        
        // Validate month/year
        int salaryMonth = salaryDto.getSalaryMonth() != null ? salaryDto.getSalaryMonth() : LocalDate.now().getMonthValue();
        int salaryYear = salaryDto.getSalaryYear() != null ? salaryDto.getSalaryYear() : LocalDate.now().getYear();
        
        // Check if salary already exists for this employee in this month/year
        List<Salary> existingSalaries = salaryRepository.findByEmployeeAndSalaryMonthAndSalaryYear(
                employee, salaryMonth, salaryYear);
        
        if (!existingSalaries.isEmpty()) {
            throw new BadRequestException("Salary already exists for this employee in " + 
                Month.of(salaryMonth).toString() + " " + salaryYear);
        }
        
        // Validate budget
        Department department = employee.getDepartment();
        if (department == null) {
            throw new BadRequestException("Employee must be assigned to a department");
        }
        
        double currentExpenses = department.calculateCurrentExpenses();
        
        if (currentExpenses + salaryDto.getGrossSalary() > department.getBudget()) {
            if (department.getBudgetType() == Department.BudgetType.MONTHLY) {
                throw new BadRequestException("Adding this salary would exceed the monthly budget for department: " + department.getName());
            } else {
                throw new BadRequestException("Adding this salary would exceed the yearly budget for department: " + department.getName());
            }
        }
        
        // Check for employee inactivities that affect salary
        List<EmployeeInactivity> inactivities = inactivityRepository.findByEmployee(employee);
        boolean hasUnpaidInactivity = inactivities.stream()
                .anyMatch(i -> i.isActiveOnDate(LocalDate.of(salaryYear, salaryMonth, 1)) && 
                        (i.getType() == InactivityType.UNPAID_LEAVE || i.getType() == InactivityType.SUSPENSION));
        
        if (hasUnpaidInactivity && salaryDto.getGrossSalary() > 0) {
            throw new BadRequestException("Employee has unpaid leave or suspension in this period. Salary should be reduced or set to zero.");
        }
        
        // Create the salary record
        Salary salary = new Salary();
        salary.setEmployee(employee);
        salary.setGrossSalary(salaryDto.getGrossSalary());
        salary.setSalaryMonth(salaryMonth);
        salary.setSalaryYear(salaryYear);
        
        // Set deductions
        for (DeductionDto deductionDto : salaryDto.getDeductions()) {
            Deduction deduction = convertToDeductionEntity(deductionDto);
            deduction.setSalary(salary);
            salary.getDeductions().add(deduction);
        }
        
        // Calculate net salary
        salary.calculateNetSalary();
        
        Salary savedSalary = salaryRepository.save(salary);
        return convertToDto(savedSalary);
    }

    /**
     * Update an existing salary record
     */
    @Transactional
    public SalaryDto updateSalary(Long id, SalaryDto salaryDto) {
        User currentUser = authService.getCurrentUser();
        List<Salary> salaries = salaryRepository.findByIdAndUser(id, currentUser);
        
        if (salaries.isEmpty()) {
            throw new ResourceNotFoundException("Salary not found with id: " + id);
        }
        
        Salary salary = salaries.get(0);
        
        // Validate employee (should be the same employee)
        if (!salary.getEmployee().getId().equals(salaryDto.getEmployeeId())) {
            throw new BadRequestException("Cannot change the employee for an existing salary");
        }
        
        // Validate budget if gross salary increases
        if (salaryDto.getGrossSalary() > salary.getGrossSalary()) {
            Department department = salary.getEmployee().getDepartment();
            if (department == null) {
                throw new BadRequestException("Employee must be assigned to a department");
            }
            
            double currentExpenses = department.calculateCurrentExpenses() - salary.getGrossSalary();
            
            if (currentExpenses + salaryDto.getGrossSalary() > department.getBudget()) {
                if (department.getBudgetType() == Department.BudgetType.MONTHLY) {
                    throw new BadRequestException("Updating this salary would exceed the monthly budget for department: " + department.getName());
                } else {
                    throw new BadRequestException("Updating this salary would exceed the yearly budget for department: " + department.getName());
                }
            }
        }
        
        // Update salary details
        salary.setGrossSalary(salaryDto.getGrossSalary());
        
        // Update month/year if provided
        if (salaryDto.getSalaryMonth() != null) {
            salary.setSalaryMonth(salaryDto.getSalaryMonth());
        }
        
        if (salaryDto.getSalaryYear() != null) {
            salary.setSalaryYear(salaryDto.getSalaryYear());
        }
        
        // Update deductions
        salary.clearDeductions();
        for (DeductionDto deductionDto : salaryDto.getDeductions()) {
            Deduction deduction = convertToDeductionEntity(deductionDto);
            deduction.setSalary(salary);
            salary.getDeductions().add(deduction);
        }
        
        // Calculate net salary
        salary.calculateNetSalary();
        
        Salary updatedSalary = salaryRepository.save(salary);
        return convertToDto(updatedSalary);
    }
    
    /**
     * Delete a salary record
     */
    @Transactional
    public void deleteSalary(Long id) {
        User currentUser = authService.getCurrentUser();
        List<Salary> salaries = salaryRepository.findByIdAndUser(id, currentUser);
        
        if (salaries.isEmpty()) {
            throw new ResourceNotFoundException("Salary not found with id: " + id);
        }
        
        Salary salary = salaries.get(0);
        
        // Delete associated deductions first
        deductionRepository.deleteBySalary(salary);
        
        // Delete the salary record
        salaryRepository.delete(salary);
    }
    
    /**
     * Generate current month's salaries for all active employees who don't have one yet
     */
    @Transactional
    public List<SalaryDto> generateCurrentMonthSalaries() {
        User currentUser = authService.getCurrentUser();
        
        // Get current month/year
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        
        // Find active employees without salary this month
        List<Employee> employeesWithoutSalary = salaryRepository.findEmployeesWithoutCurrentMonthSalary(
                currentUser, Status.ACTIVE);
        
        List<SalaryDto> generatedSalaries = new ArrayList<>();
        
        for (Employee employee : employeesWithoutSalary) {
            // Look for previous month's salary to use as template
            List<Salary> previousSalaries = salaryRepository.findByEmployeeIdAndUser(employee.getId(), currentUser);
            
            if (!previousSalaries.isEmpty()) {
                // Use most recent salary as template
                Salary template = previousSalaries.get(0);
                
                Salary newSalary = new Salary();
                newSalary.setEmployee(employee);
                newSalary.setGrossSalary(template.getGrossSalary());
                newSalary.setSalaryMonth(currentMonth);
                newSalary.setSalaryYear(currentYear);
                
                // Copy deductions
                for (Deduction oldDeduction : template.getDeductions()) {
                    Deduction newDeduction = oldDeduction.copy();
                    newDeduction.setSalary(newSalary);
                    newSalary.getDeductions().add(newDeduction);
                }
                
                // Calculate net salary
                newSalary.calculateNetSalary();
                
                // Save
                Salary savedSalary = salaryRepository.save(newSalary);
                generatedSalaries.add(convertToDto(savedSalary));
            } else {
                // No template available, create minimal salary
                Salary newSalary = new Salary();
                newSalary.setEmployee(employee);
                newSalary.setGrossSalary(0.0); // Will need manual update
                newSalary.setSalaryMonth(currentMonth);
                newSalary.setSalaryYear(currentYear);
                
                // Calculate net salary
                newSalary.calculateNetSalary();
                
                // Save
                Salary savedSalary = salaryRepository.save(newSalary);
                generatedSalaries.add(convertToDto(savedSalary));
            }
        }
        
        return generatedSalaries;
    }
    
    /**
     * Get salary statistics grouped by department
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSalaryStatsByDepartment() {
        User currentUser = authService.getCurrentUser();
        
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        
        List<Object[]> departmentStats = salaryRepository.sumSalaryByDepartmentForCurrentMonth(
                currentUser, Status.ACTIVE);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] stat : departmentStats) {
            Map<String, Object> departmentStat = new HashMap<>();
            departmentStat.put("department", stat[0]);
            departmentStat.put("totalSalary", stat[1]);
            result.add(departmentStat);
        }
        
        return result;
    }
    
    /**
     * Get monthly salary trend data for charts
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSalaryTrends() {
        User currentUser = authService.getCurrentUser();
        
        List<Object[]> trends = salaryRepository.getSalaryTrendByMonth(currentUser);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] trend : trends) {
            Map<String, Object> trendPoint = new HashMap<>();
            int year = (int) trend[0];
            int month = (int) trend[1];
            double totalSalary = ((Number) trend[2]).doubleValue();
            
            trendPoint.put("year", year);
            trendPoint.put("month", month);
            trendPoint.put("totalSalary", totalSalary);
            
            // Add formatted month name
            String monthName = Month.of(month).toString();
            trendPoint.put("periodLabel", monthName.substring(0, 3) + " " + year);
            
            result.add(trendPoint);
        }
        
        return result;
    }

    /**
     * Helper method to convert Entity to DTO
     */
    private SalaryDto convertToDto(Salary salary) {
        SalaryDto dto = new SalaryDto();
        dto.setId(salary.getId());
        dto.setEmployeeId(salary.getEmployee().getId());
        dto.setEmployeeName(salary.getEmployee().getName());
        dto.setEmployeeEmail(salary.getEmployee().getEmail());
        dto.setEmployeeRole(salary.getEmployee().getRole());
        
        // Handle department info (which might be null)
        if (salary.getEmployee().getDepartment() != null) {
            dto.setDepartmentName(salary.getEmployee().getDepartment().getName());
        }
        
        dto.setGrossSalary(salary.getGrossSalary());
        dto.setNetSalary(salary.getNetSalary());
        dto.setTaxDeduction(salary.getTaxDeduction());
        dto.setInsuranceDeduction(salary.getInsuranceDeduction());
        dto.setOtherDeductions(salary.getOtherDeductions());
        
        dto.setSalaryMonth(salary.getSalaryMonth());
        dto.setSalaryYear(salary.getSalaryYear());
        dto.setCreatedAt(salary.getCreatedAt());
        
        // Check if this is the current month/year salary
        LocalDate now = LocalDate.now();
        dto.setIsCurrent(salary.getSalaryMonth() == now.getMonthValue() && 
                          salary.getSalaryYear() == now.getYear());
        
        // Convert deductions
        List<DeductionDto> deductionDtos = salary.getDeductions().stream()
                .map(this::convertToDeductionDto)
                .collect(Collectors.toList());
        dto.setDeductions(deductionDtos);
        
        return dto;
    }
    
    /**
     * Helper method to convert Deduction Entity to DTO with calculated amounts
     */
    private DeductionDto convertToDeductionDto(Deduction deduction) {
        DeductionDto dto = new DeductionDto();
        dto.setId(deduction.getId());
        dto.setType(deduction.getType());
        dto.setName(deduction.getName());
        dto.setValue(deduction.getValue());
        dto.setPercentage(deduction.isPercentage());
        
        // Calculate actual deduction amount if salary exists
        if (deduction.getSalary() != null) {
            double amount = deduction.calculateAmount(deduction.getSalary().getGrossSalary());
            dto.setCalculatedAmount(amount);
        }
        
        return dto;
    }
    
    /**
     * Helper method to convert Deduction DTO to Entity
     */
    private Deduction convertToDeductionEntity(DeductionDto dto) {
        Deduction deduction = new Deduction();
        if (dto.getId() != null) {
            deduction.setId(dto.getId());
        }
        deduction.setType(dto.getType());
        deduction.setName(dto.getName());
        deduction.setValue(dto.getValue());
        deduction.setPercentage(dto.isPercentage());
        return deduction;
    }
}
