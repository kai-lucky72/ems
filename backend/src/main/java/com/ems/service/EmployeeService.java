package com.ems.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

import com.ems.dto.EmployeeDto;
import com.ems.dto.EmployeeInactivityDto;
import com.ems.dto.LeaveDto;
import com.ems.dto.SalaryDto;
import com.ems.exception.BadRequestException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.EmployeeInactivity;
import com.ems.model.Leave;
import com.ems.model.Salary;
import com.ems.model.User;
import com.ems.model.Employee.ContractType;
import com.ems.model.Employee.Status;
import com.ems.model.EmployeeInactivity.InactivityType;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeInactivityRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.LeaveRepository;
import com.ems.repository.SalaryRepository;

/**
 * Service for managing employees
 */
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private EmployeeInactivityRepository inactivityRepository;
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private LeaveRepository leaveRepository;
    
    /**
     * Utility method to handle List<Boolean> return types from repository methods
     * @param booleanList The list returned from repository
     * @return true if the list contains at least one true value, false otherwise
     */
    private boolean getBooleanResult(List<Boolean> booleanList) {
        return booleanList != null && !booleanList.isEmpty() && booleanList.get(0);
    }
    
    @Autowired
    private AuthService authService;

    /**
     * Get all employees for the current user's company
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployeesForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<Employee> employees = employeeRepository.findByUserOrderByNameAsc(currentUser);
        
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get active employees for the current user's company
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getActiveEmployeesForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<Employee> employees = employeeRepository.findByUserAndStatusOrderByNameAsc(currentUser, Status.ACTIVE);
        
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get inactive employees for the current user's company
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getInactiveEmployeesForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<Employee> employees = employeeRepository.findByUserAndStatusOrderByNameAsc(currentUser, Status.INACTIVE);
        
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get paginated employees
     */
    @Transactional(readOnly = true)
    public Page<EmployeeDto> getPaginatedEmployees(int page, int size, String sortBy, boolean ascending) {
        User currentUser = authService.getCurrentUser();
        Sort sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Employee> employeePage = employeeRepository.findByUserPageable(currentUser, pageable);
        
        return employeePage.map(this::convertToDto);
    }

    /**
     * Get employee by ID with detailed information
     */
    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeById(Long id) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        return convertToDto(employee);
    }
    
    /**
     * Get detailed employee information including salary, leave and inactivity history
     */
    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeDetailById(Long id) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        EmployeeDto dto = convertToDto(employee);
        
        // Add salary history
        List<Salary> salaryHistory = salaryRepository.findByEmployeeIdOrderByYearDescMonthDesc(id);
        if (!salaryHistory.isEmpty()) {
            dto.setCurrentSalary(salaryHistory.get(0).getGrossSalary());
            dto.setNetSalary(salaryHistory.get(0).getNetSalary());
            
            List<SalaryDto> salaryDtos = new ArrayList<>();
            for (Salary salary : salaryHistory) {
                SalaryDto salaryDto = new SalaryDto();
                salaryDto.setId(salary.getId());
                salaryDto.setEmployeeId(salary.getEmployee().getId());
                salaryDto.setEmployeeName(salary.getEmployee().getName());
                salaryDto.setGrossSalary(salary.getGrossSalary());
                salaryDto.setNetSalary(salary.getNetSalary());
                salaryDto.setSalaryYear(salary.getSalaryYear());
                salaryDto.setSalaryMonth(salary.getSalaryMonth());
                salaryDtos.add(salaryDto);
            }
            dto.setSalaryHistory(salaryDtos);
        }
        
        // Add leave information
        LocalDate today = LocalDate.now();
        Optional<Leave> currentLeave = leaveRepository.findCurrentLeaveForEmployee(employee.getId(), today);
        if (currentLeave.isPresent()) {
            dto.setOnLeave(true);
            dto.setActiveLeaveId(currentLeave.get().getId());
            dto.setLeaveEndDate(currentLeave.get().getEndDate());
        }
        
        // Add pending leave count
        List<Long> pendingLeaveCountList = leaveRepository.countByEmployeeAndStatus(employee, Leave.Status.PENDING);
        long pendingLeaveCount = pendingLeaveCountList.isEmpty() ? 0L : pendingLeaveCountList.get(0);
        dto.setPendingLeaveRequests((int) pendingLeaveCount);
        
        // Add inactivity history
        List<EmployeeInactivity> inactivities = inactivityRepository.findByEmployeeIdOrderByStartDateDesc(id);
        if (!inactivities.isEmpty()) {
            List<EmployeeInactivityDto> inactivityDtos = new ArrayList<>();
            for (EmployeeInactivity inactivity : inactivities) {
                EmployeeInactivityDto inactivityDto = new EmployeeInactivityDto();
                inactivityDto.setId(inactivity.getId());
                inactivityDto.setEmployeeId(inactivity.getEmployee().getId());
                inactivityDto.setEmployeeName(inactivity.getEmployee().getName());
                inactivityDto.setStartDate(inactivity.getStartDate());
                inactivityDto.setEndDate(inactivity.getEndDate());
                inactivityDto.setReason(inactivity.getReason());
                inactivityDto.setType(inactivity.getType());
                
                // Calculate if this is the current inactivity period
                boolean isCurrent = inactivity.getStartDate().isEqual(today) || 
                                  inactivity.getStartDate().isBefore(today) && 
                                  (inactivity.getEndDate() == null || inactivity.getEndDate().isAfter(today));
                inactivityDto.setCurrent(isCurrent);
                
                // If this is the current inactivity, set the reason in the main DTO
                if (isCurrent) {
                    dto.setInactivityReason(inactivity.getReason());
                    dto.setCurrentInactivityId(inactivity.getId());
                }
                
                inactivityDtos.add(inactivityDto);
            }
            dto.setInactivityHistory(inactivityDtos);
        }
        
        return dto;
    }
    
    /**
     * Search employees by keyword
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> searchEmployees(String searchTerm) {
        User currentUser = authService.getCurrentUser();
        List<Employee> employees = employeeRepository.searchEmployees(currentUser, searchTerm);
        
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get employees by contract type
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByContractType(ContractType contractType) {
        User currentUser = authService.getCurrentUser();
        List<Employee> employees = employeeRepository.findByUserAndContractType(currentUser, contractType);
        
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get employees with contracts ending soon
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesWithContractsEndingSoon(int daysThreshold) {
        User currentUser = authService.getCurrentUser();
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        
        List<Employee> employees = employeeRepository.findEmployeesWithContractEndingSoon(currentUser, thresholdDate);
        
        return employees.stream()
                .map(employee -> {
                    EmployeeDto dto = convertToDto(employee);
                    if (employee.getEndDate() != null) {
                        dto.setDaysUntilContractEnd(
                                (int) ChronoUnit.DAYS.between(LocalDate.now(), employee.getEndDate()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get new employees (joined recently)
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getRecentEmployees(int daysThreshold) {
        User currentUser = authService.getCurrentUser();
        LocalDate thresholdDate = LocalDate.now().minusDays(daysThreshold);
        
        List<Employee> employees = employeeRepository.findRecentEmployees(currentUser, thresholdDate);
        
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get employees with upcoming work anniversaries
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesWithUpcomingAnniversaries(int daysThreshold) {
        User currentUser = authService.getCurrentUser();
        LocalDate today = LocalDate.now();
        
        // Get current month and next few weeks span
        int currentMonth = today.getMonthValue();
        int currentDay = today.getDayOfMonth();
        int lastDay = currentDay + daysThreshold;
        
        List<Employee> employees = employeeRepository.findEmployeesWithUpcomingAnniversaries(
                currentUser, currentMonth, currentDay, lastDay);
        
        return employees.stream()
                .map(employee -> {
                    EmployeeDto dto = convertToDto(employee);
                    
                    // Calculate years of service
                    LocalDate startDate = employee.getStartDate();
                    int yearsOfService = today.getYear() - startDate.getYear();
                    dto.setYearsOfService(yearsOfService);
                    
                    // Calculate days until anniversary
                    LocalDate thisYearAnniversary = startDate.withYear(today.getYear());
                    if (thisYearAnniversary.isBefore(today)) {
                        // Anniversary already passed this year, calculate for next year
                        thisYearAnniversary = thisYearAnniversary.plusYears(1);
                    }
                    
                    int daysUntilAnniversary = (int) ChronoUnit.DAYS.between(today, thisYearAnniversary);
                    dto.setDaysUntilAnniversary(daysUntilAnniversary);
                    dto.setUpcomingAnniversaryDate(thisYearAnniversary);
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get employees with no salary assigned
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesWithNoSalary() {
        User currentUser = authService.getCurrentUser();
        List<Employee> employees = employeeRepository.findEmployeesWithNoSalary(currentUser);
        
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get employees with pending leave requests
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesWithPendingLeaves() {
        User currentUser = authService.getCurrentUser();
        List<Employee> employees = employeeRepository.findEmployeesWithPendingLeaves(currentUser);
        
        return employees.stream()
                .map(employee -> {
                    EmployeeDto dto = convertToDto(employee);
                    
                    // Count pending leaves for this employee
                    long pendingLeaveCount = employee.getLeaves().stream()
                            .filter(leave -> leave.getStatus() == Leave.Status.PENDING)
                            .count();
                    
                    dto.setPendingLeaveRequests((int) pendingLeaveCount);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get employees with a specific role
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByRole(String role) {
        User currentUser = authService.getCurrentUser();
        List<Employee> employees = employeeRepository.findByUserAndRole(currentUser, role);
        
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get currently inactive employees
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getCurrentlyInactiveEmployees() {
        User currentUser = authService.getCurrentUser();
        List<Employee> employees = employeeRepository.findCurrentlyInactiveEmployees(currentUser);
        
        return employees.stream()
                .map(employee -> {
                    EmployeeDto dto = convertToDto(employee);
                    
                    // Get the current inactivity period
                    EmployeeInactivity currentInactivity = employee.getCurrentInactivityPeriod();
                    if (currentInactivity != null) {
                        dto.setInactiveFrom(currentInactivity.getStartDate());
                        dto.setInactiveTo(currentInactivity.getEndDate());
                        dto.setInactivityReason(currentInactivity.getReason());
                        dto.setCurrentInactivityId(currentInactivity.getId());
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get employees returning from inactivity soon
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesReturningFromInactivity(int daysThreshold) {
        User currentUser = authService.getCurrentUser();
        LocalDate futureDate = LocalDate.now().plusDays(daysThreshold);
        
        List<Employee> employees = employeeRepository.findEmployeesReturningFromInactivity(currentUser, futureDate);
        
        return employees.stream()
                .map(employee -> {
                    EmployeeDto dto = convertToDto(employee);
                    
                    // Get the current inactivity period
                    EmployeeInactivity currentInactivity = employee.getCurrentInactivityPeriod();
                    if (currentInactivity != null && currentInactivity.getEndDate() != null) {
                        dto.setInactiveFrom(currentInactivity.getStartDate());
                        dto.setInactiveTo(currentInactivity.getEndDate());
                        dto.setInactivityReason(currentInactivity.getReason());
                        dto.setCurrentInactivityId(currentInactivity.getId());
                        
                        // Calculate days until return
                        int daysUntilReturn = (int) ChronoUnit.DAYS.between(
                                LocalDate.now(), currentInactivity.getEndDate());
                        dto.setDaysUntilReturn(daysUntilReturn);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Create a new employee
     */
    @Transactional
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        User currentUser = authService.getCurrentUser();
        
        // Check for existing employee with same email
        if (employeeRepository.findByEmailAndUser(employeeDto.getEmail(), currentUser).isPresent()) {
            throw new BadRequestException("An employee with this email already exists");
        }
        
        // Validate department
        Department department = departmentRepository.findByIdAndUser(employeeDto.getDepartmentId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + employeeDto.getDepartmentId()));
        
        // Validate dates
        if (employeeDto.getEndDate() != null && employeeDto.getStartDate().isAfter(employeeDto.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }
        
        // Validate inactivity dates if employee is inactive
        if (!employeeDto.isActive()) {
            if (employeeDto.getInactiveFrom() == null) {
                throw new BadRequestException("Inactive from date is required for inactive employees");
            }
            
            if (employeeDto.getInactiveFrom().isBefore(employeeDto.getStartDate())) {
                throw new BadRequestException("Inactivity start date cannot be before employment start date");
            }
            
            if (employeeDto.getInactiveTo() != null && 
                employeeDto.getInactiveFrom().isAfter(employeeDto.getInactiveTo())) {
                throw new BadRequestException("Inactivity start date must be before end date");
            }
        }
        
        Employee employee = new Employee();
        employee.setName(employeeDto.getName());
        employee.setEmail(employeeDto.getEmail());
        employee.setPhone(employeeDto.getPhone());
        employee.setRole(employeeDto.getRole());
        employee.setDepartment(department);
        employee.setContractType(employeeDto.getContractType());
        employee.setStartDate(employeeDto.getStartDate());
        employee.setEndDate(employeeDto.getEndDate());
        employee.setStatus(employeeDto.isActive() ? Status.ACTIVE : Status.INACTIVE);
        employee.setUser(currentUser);
        
        Employee savedEmployee = employeeRepository.save(employee);
        
        // Create inactivity record if needed
        if (!employeeDto.isActive()) {
            EmployeeInactivity inactivity = new EmployeeInactivity();
            inactivity.setEmployee(savedEmployee);
            inactivity.setStartDate(employeeDto.getInactiveFrom());
            inactivity.setEndDate(employeeDto.getInactiveTo());
            inactivity.setReason(employeeDto.getInactivityReason() != null ? 
                    employeeDto.getInactivityReason() : "Initial inactivity status");
            inactivity.setType(InactivityType.ADMINISTRATIVE);
            inactivityRepository.save(inactivity);
        }
        
        return convertToDto(savedEmployee);
    }

    /**
     * Update an existing employee
     */
    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        // Check for existing employee with same email (excluding this one)
        Optional<Employee> existingWithEmail = employeeRepository.findByEmailAndUser(employeeDto.getEmail(), currentUser);
        if (existingWithEmail.isPresent() && !existingWithEmail.get().getId().equals(id)) {
            throw new BadRequestException("Another employee with this email already exists");
        }
        
        // Validate department
        Department department = departmentRepository.findByIdAndUser(employeeDto.getDepartmentId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + employeeDto.getDepartmentId()));
        
        // Validate dates
        if (employeeDto.getEndDate() != null && employeeDto.getStartDate().isAfter(employeeDto.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }
        
        // Validate inactivity dates if employee is becoming inactive
        boolean wasActive = employee.isActive();
        boolean isNowActive = employeeDto.isActive();
        
        if (!wasActive && !isNowActive) {
            // Already inactive, staying inactive - validate any updates to inactivity dates
            if (employeeDto.getInactiveFrom() == null) {
                throw new BadRequestException("Inactive from date is required for inactive employees");
            }
            
            if (employeeDto.getInactiveFrom().isBefore(employeeDto.getStartDate())) {
                throw new BadRequestException("Inactivity start date cannot be before employment start date");
            }
            
            if (employeeDto.getInactiveTo() != null && 
                employeeDto.getInactiveFrom().isAfter(employeeDto.getInactiveTo())) {
                throw new BadRequestException("Inactivity start date must be before end date");
            }
        } else if (wasActive && !isNowActive) {
            // Becoming inactive - validate new inactivity dates
            if (employeeDto.getInactiveFrom() == null) {
                throw new BadRequestException("Inactive from date is required for inactive employees");
            }
            
            if (employeeDto.getInactiveFrom().isBefore(employeeDto.getStartDate())) {
                throw new BadRequestException("Inactivity start date cannot be before employment start date");
            }
            
            if (employeeDto.getInactiveTo() != null && 
                employeeDto.getInactiveFrom().isAfter(employeeDto.getInactiveTo())) {
                throw new BadRequestException("Inactivity start date must be before end date");
            }
        }
        
        // Update basic employee info
        employee.setName(employeeDto.getName());
        employee.setEmail(employeeDto.getEmail());
        employee.setPhone(employeeDto.getPhone());
        employee.setRole(employeeDto.getRole());
        employee.setDepartment(department);
        employee.setContractType(employeeDto.getContractType());
        employee.setStartDate(employeeDto.getStartDate());
        employee.setEndDate(employeeDto.getEndDate());
        
        // Handle status changes if needed
        if (wasActive != isNowActive) {
            employee.setStatus(isNowActive ? Status.ACTIVE : Status.INACTIVE);
            
            // Create inactivity record if becoming inactive
            if (!isNowActive) {
                EmployeeInactivity inactivity = new EmployeeInactivity();
                inactivity.setEmployee(employee);
                inactivity.setStartDate(employeeDto.getInactiveFrom());
                inactivity.setEndDate(employeeDto.getInactiveTo());
                inactivity.setReason(employeeDto.getInactivityReason() != null ? 
                        employeeDto.getInactivityReason() : "Status changed to inactive");
                inactivity.setType(InactivityType.ADMINISTRATIVE);
                inactivityRepository.save(inactivity);
            } else {
                // Close any open inactivity periods
                LocalDate yesterday = LocalDate.now().minusDays(1);
                List<EmployeeInactivity> openInactivities = inactivityRepository.findByEmployeeId(id)
                        .stream()
                        .filter(ei -> ei.getEndDate() == null)
                        .collect(Collectors.toList());
                
                for (EmployeeInactivity existingInactivity : openInactivities) {
                    existingInactivity.setEndDate(yesterday);
                    inactivityRepository.save(existingInactivity);
                }
            }
        } else if (!isNowActive && employeeDto.getCurrentInactivityId() != null) {
            // Already inactive, update existing inactivity record
            EmployeeInactivity existingInactivity = inactivityRepository.findById(employeeDto.getCurrentInactivityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inactivity record not found with id: " + 
                            employeeDto.getCurrentInactivityId()));
            
            existingInactivity.setStartDate(employeeDto.getInactiveFrom());
            existingInactivity.setEndDate(employeeDto.getInactiveTo());
            
            if (employeeDto.getInactivityReason() != null) {
                existingInactivity.setReason(employeeDto.getInactivityReason());
            }
            
            inactivityRepository.save(existingInactivity);
        }
        
        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDto(updatedEmployee);
    }

    /**
     * Update employee status and inactivity
     */
    @Transactional
    public EmployeeDto updateEmployeeStatus(Long id, EmployeeInactivityDto inactivityDto) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        // Validate inactivity data
        if (inactivityDto.getStartDate() == null) {
            throw new BadRequestException("Start date is required for inactivity records");
        }
        
        if (inactivityDto.getStartDate().isBefore(employee.getStartDate())) {
            throw new BadRequestException("Inactivity start date cannot be before employment start date");
        }
        
        if (inactivityDto.getEndDate() != null && 
            inactivityDto.getStartDate().isAfter(inactivityDto.getEndDate())) {
            throw new BadRequestException("Inactivity start date must be before end date");
        }
        
        boolean isNowActive = !inactivityDto.isCurrent(); // If current inactivity, employee is inactive
        
        // Update employee status
        employee.setStatus(isNowActive ? Status.ACTIVE : Status.INACTIVE);
        
        if (!isNowActive) {
            // Check for existing open inactivity records
            List<EmployeeInactivity> existingOpenInactivities = inactivityRepository.findOpenInactivitiesForEmployee(id);
            
            // Close any existing open inactivity records
            LocalDate yesterday = LocalDate.now().minusDays(1);
            for (EmployeeInactivity existingInactivity : existingOpenInactivities) {
                existingInactivity.setEndDate(yesterday);
                inactivityRepository.save(existingInactivity);
            }
            
            // Create new inactivity record
            EmployeeInactivity inactivity = new EmployeeInactivity();
            inactivity.setEmployee(employee);
            inactivity.setStartDate(inactivityDto.getStartDate());
            inactivity.setEndDate(inactivityDto.getEndDate());
            inactivity.setReason(inactivityDto.getReason());
            inactivity.setType(inactivityDto.getType());
            inactivityRepository.save(inactivity);
        } else if (inactivityDto.getId() != null) {
            // Ending a specific inactivity period
            EmployeeInactivity existingInactivity = inactivityRepository.findById(inactivityDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inactivity record not found with id: " + 
                            inactivityDto.getId()));
            
            // Set end date to yesterday if not provided
            existingInactivity.setEndDate(inactivityDto.getEndDate() != null ? 
                    inactivityDto.getEndDate() : LocalDate.now().minusDays(1));
                    
            inactivityRepository.save(existingInactivity);
        } else {
            // Close any open inactivity periods
            LocalDate yesterday = LocalDate.now().minusDays(1);
            List<EmployeeInactivity> openInactivities = inactivityRepository.findByEmployeeId(id)
                    .stream()
                    .filter(ei -> ei.getEndDate() == null)
                    .collect(Collectors.toList());
            
            for (EmployeeInactivity existingInactivity : openInactivities) {
                existingInactivity.setEndDate(yesterday);
                inactivityRepository.save(existingInactivity);
            }
        }
        
        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDto(updatedEmployee);
    }

    /**
     * Delete an employee
     */
    @Transactional
    public void deleteEmployee(Long id) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        // Check if employee has dependent records (salaries, leaves, etc.)
        if (!employee.getSalaries().isEmpty()) {
            throw new BadRequestException("Cannot delete employee with salary records. " +
                    "Please remove salary records first or mark employee as inactive instead.");
        }
        
        if (!employee.getLeaves().isEmpty()) {
            throw new BadRequestException("Cannot delete employee with leave records. " +
                    "Please remove leave records first or mark employee as inactive instead.");
        }
        
        if (!employee.getMessages().isEmpty()) {
            throw new BadRequestException("Cannot delete employee with message records. " +
                    "Please remove message records first or mark employee as inactive instead.");
        }
        
        employeeRepository.delete(employee);
    }
    
    /**
     * Get employees by department
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByDepartment(Long departmentId) {
        User currentUser = authService.getCurrentUser();
        Department department = departmentRepository.findByIdAndUser(departmentId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
        
        List<Employee> employees = employeeRepository.findByDepartment(department);
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get employees on leave
     */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesOnLeave() {
        User currentUser = authService.getCurrentUser();
        LocalDate today = LocalDate.now();
        
        List<Employee> employeesOnLeave = employeeRepository.findEmployeesOnLeaveByDate(currentUser, today);
        return employeesOnLeave.stream()
                .map(employee -> {
                    EmployeeDto dto = convertToDto(employee);
                    
                    // Find the current leave record
                    Optional<Leave> currentLeave = employee.getLeaves().stream()
                            .filter(leave -> leave.getStatus() == Leave.Status.APPROVED &&
                                    !today.isBefore(leave.getStartDate()) &&
                                    !today.isAfter(leave.getEndDate()))
                            .findFirst();
                    
                    if (currentLeave.isPresent()) {
                        dto.setActiveLeaveId(currentLeave.get().getId());
                        dto.setLeaveEndDate(currentLeave.get().getEndDate());
                        
                        // Calculate days remaining on leave
                        int daysRemaining = (int) ChronoUnit.DAYS.between(today, currentLeave.get().getEndDate());
                        dto.setDaysRemainingOnLeave(daysRemaining);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get employee counts by role
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEmployeeCountsByRole() {
        User currentUser = authService.getCurrentUser();
        List<Object[]> roleCounts = employeeRepository.countEmployeesByRole(currentUser);
        
        return roleCounts.stream()
                .map(row -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("role", row[0]);
                    result.put("count", row[1]);
                    return result;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get employee counts by contract type
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEmployeeCountsByContractType() {
        User currentUser = authService.getCurrentUser();
        List<Object[]> contractTypeCounts = employeeRepository.countEmployeesByContractType(currentUser);
        
        return contractTypeCounts.stream()
                .map(row -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("contractType", row[0]);
                    result.put("count", row[1]);
                    return result;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get employee counts by department
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEmployeeCountsByDepartment() {
        User currentUser = authService.getCurrentUser();
        List<Object[]> departmentCounts = employeeRepository.countEmployeesByDepartment(currentUser);
        
        return departmentCounts.stream()
                .map(row -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("department", row[0]);
                    result.put("count", row[1]);
                    return result;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get employee hiring trends (employees by start date)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEmployeeHiringTrends() {
        User currentUser = authService.getCurrentUser();
        List<Object[]> hiringTrends = employeeRepository.countEmployeesByStartDate(currentUser);
        
        return hiringTrends.stream()
                .map(row -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("year", row[0]);
                    result.put("month", row[1]);
                    result.put("count", row[2]);
                    return result;
                })
                .collect(Collectors.toList());
    }

    /**
     * Helper method to convert Entity to DTO
     */
    public EmployeeDto convertToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setRole(employee.getRole());
        dto.setCreatedAt(employee.getCreatedAt());
        
        // Handle department (which might be null for some employees)
        if (employee.getDepartment() != null) {
            dto.setDepartmentId(employee.getDepartment().getId());
            dto.setDepartmentName(employee.getDepartment().getName());
            
            // Calculate budget usage if department has budget info
            double budget = employee.getDepartment().getBudget();
            double currentExpenses = employee.getDepartment().calculateCurrentExpenses();
            
            if (budget > 0) {
                double usagePercentage = (currentExpenses / budget) * 100;
                dto.setDepartmentBudgetUsagePercent(usagePercentage);
            }
        }
        
        dto.setContractType(employee.getContractType());
        dto.setStartDate(employee.getStartDate());
        dto.setEndDate(employee.getEndDate());
        dto.setActive(employee.isActive());
        dto.setStatus(employee.getStatus());
        
        // Calculate tenure
        LocalDate today = LocalDate.now();
        if (employee.getStartDate() != null) {
            long days = ChronoUnit.DAYS.between(employee.getStartDate(), today);
            dto.setTenureDays((int) days);
        }
        
        // Get inactivity data from employee's current inactivity period if any
        EmployeeInactivity currentInactivity = employee.getCurrentInactivityPeriod();
        if (currentInactivity != null) {
            dto.setInactiveFrom(currentInactivity.getStartDate());
            dto.setInactiveTo(currentInactivity.getEndDate());
            dto.setInactivityReason(currentInactivity.getReason());
            dto.setCurrentInactivityId(currentInactivity.getId());
        }
        
        // Calculate if employee is on leave
        dto.setOnLeave(employee.isOnLeave());
        
        // Get current salary if available
        Salary currentSalary = employee.getCurrentSalary();
        if (currentSalary != null) {
            dto.setCurrentSalary(currentSalary.getGrossSalary());
            dto.setNetSalary(currentSalary.getNetSalary());
        }
        
        // Count pending leaves
        long pendingLeaveCount = employee.getLeaves().stream()
                .filter(leave -> leave.getStatus() == Leave.Status.PENDING)
                .count();
        dto.setPendingLeaveRequests((int) pendingLeaveCount);
        
        // Check if employee has messages
        dto.setHasMessages(!employee.getMessages().isEmpty());
        
        return dto;
    }
    
    /**
     * Helper method to convert DTO to Entity for creating new employee
     */
    private Employee convertToEntity(EmployeeDto dto, User currentUser) {
        Employee employee = new Employee();
        
        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setRole(dto.getRole());
        employee.setContractType(dto.getContractType());
        employee.setStartDate(dto.getStartDate());
        employee.setEndDate(dto.getEndDate());
        employee.setStatus(dto.isActive() ? Status.ACTIVE : Status.INACTIVE);
        employee.setUser(currentUser);
        
        return employee;
    }
}
