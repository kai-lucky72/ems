package com.ems.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.EmployeeDto;
import com.ems.exception.BadRequestException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.User;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployeesForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<Employee> employees = employeeRepository.findByUser(currentUser);
        
        return employees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeById(Long id) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        return convertToDto(employee);
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        User currentUser = authService.getCurrentUser();
        
        // Validate department
        Department department = departmentRepository.findByIdAndUser(employeeDto.getDepartmentId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + employeeDto.getDepartmentId()));
        
        // Validate dates
        if (employeeDto.getEndDate() != null && employeeDto.getStartDate().isAfter(employeeDto.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
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
        employee.setActive(employeeDto.isActive());
        employee.setUser(currentUser);
        
        if (!employeeDto.isActive()) {
            if (employeeDto.getInactiveFrom() == null) {
                throw new BadRequestException("Inactive from date is required for inactive employees");
            }
            employee.setInactiveFrom(employeeDto.getInactiveFrom());
            employee.setInactiveTo(employeeDto.getInactiveTo());
        }
        
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDto(savedEmployee);
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        // Validate department
        Department department = departmentRepository.findByIdAndUser(employeeDto.getDepartmentId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + employeeDto.getDepartmentId()));
        
        // Validate dates
        if (employeeDto.getEndDate() != null && employeeDto.getStartDate().isAfter(employeeDto.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }
        
        employee.setName(employeeDto.getName());
        employee.setEmail(employeeDto.getEmail());
        employee.setPhone(employeeDto.getPhone());
        employee.setRole(employeeDto.getRole());
        employee.setDepartment(department);
        employee.setContractType(employeeDto.getContractType());
        employee.setStartDate(employeeDto.getStartDate());
        employee.setEndDate(employeeDto.getEndDate());
        employee.setActive(employeeDto.isActive());
        
        if (!employeeDto.isActive()) {
            if (employeeDto.getInactiveFrom() == null) {
                throw new BadRequestException("Inactive from date is required for inactive employees");
            }
            employee.setInactiveFrom(employeeDto.getInactiveFrom());
            employee.setInactiveTo(employeeDto.getInactiveTo());
        } else {
            employee.setInactiveFrom(null);
            employee.setInactiveTo(null);
        }
        
        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDto(updatedEmployee);
    }

    @Transactional
    public EmployeeDto updateEmployeeStatus(Long id, boolean isActive, String inactiveFromStr, String inactiveToStr) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        employee.setActive(isActive);
        
        if (!isActive) {
            if (inactiveFromStr == null) {
                throw new BadRequestException("Inactive from date is required for inactive employees");
            }
            
            LocalDate inactiveFrom = LocalDate.parse(inactiveFromStr);
            LocalDate inactiveTo = inactiveToStr != null ? LocalDate.parse(inactiveToStr) : null;
            
            if (inactiveTo != null && inactiveFrom.isAfter(inactiveTo)) {
                throw new BadRequestException("Inactive from date must be before inactive to date");
            }
            
            employee.setInactiveFrom(inactiveFrom);
            employee.setInactiveTo(inactiveTo);
        } else {
            employee.setInactiveFrom(null);
            employee.setInactiveTo(null);
        }
        
        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDto(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        employeeRepository.delete(employee);
    }

    // Helper method to convert Entity to DTO
    private EmployeeDto convertToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setRole(employee.getRole());
        dto.setDepartmentId(employee.getDepartment().getId());
        dto.setDepartmentName(employee.getDepartment().getName());
        dto.setContractType(employee.getContractType());
        dto.setStartDate(employee.getStartDate());
        dto.setEndDate(employee.getEndDate());
        dto.setActive(employee.isActive());
        dto.setInactiveFrom(employee.getInactiveFrom());
        dto.setInactiveTo(employee.getInactiveTo());
        return dto;
    }
}
