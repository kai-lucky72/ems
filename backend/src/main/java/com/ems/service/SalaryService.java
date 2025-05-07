package com.ems.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.DeductionDto;
import com.ems.dto.SalaryDto;
import com.ems.exception.BadRequestException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Deduction;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.Salary;
import com.ems.model.User;
import com.ems.repository.DeductionRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.SalaryRepository;

@Service
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DeductionRepository deductionRepository;
    
    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public List<SalaryDto> getAllSalariesForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<Salary> salaries = salaryRepository.findByUser(currentUser);
        
        return salaries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SalaryDto getSalaryById(Long id) {
        User currentUser = authService.getCurrentUser();
        Salary salary = salaryRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Salary not found with id: " + id));
        
        return convertToDto(salary);
    }

    @Transactional(readOnly = true)
    public SalaryDto getSalaryByEmployeeId(Long employeeId) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(employeeId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        Salary salary = salaryRepository.findByEmployee(employee)
                .orElseThrow(() -> new ResourceNotFoundException("Salary not found for employee with id: " + employeeId));
        
        return convertToDto(salary);
    }

    @Transactional
    public SalaryDto createSalary(SalaryDto salaryDto) {
        User currentUser = authService.getCurrentUser();
        
        // Validate employee
        Employee employee = employeeRepository.findByIdAndUser(salaryDto.getEmployeeId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + salaryDto.getEmployeeId()));
        
        // Check if salary already exists for this employee
        if (salaryRepository.findByEmployee(employee).isPresent()) {
            throw new BadRequestException("Salary already exists for this employee");
        }
        
        // Validate budget
        Department department = employee.getDepartment();
        double currentExpenses = department.calculateCurrentExpenses();
        
        if (currentExpenses + salaryDto.getGrossSalary() > department.getBudget()) {
            if (department.getBudgetType() == Department.BudgetType.MONTHLY) {
                throw new BadRequestException("Adding this salary would exceed the monthly budget for department: " + department.getName());
            } else {
                throw new BadRequestException("Adding this salary would exceed the yearly budget for department: " + department.getName());
            }
        }
        
        Salary salary = new Salary();
        salary.setEmployee(employee);
        salary.setGrossSalary(salaryDto.getGrossSalary());
        
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

    @Transactional
    public SalaryDto updateSalary(Long id, SalaryDto salaryDto) {
        User currentUser = authService.getCurrentUser();
        Salary salary = salaryRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Salary not found with id: " + id));
        
        // Validate employee (should be the same employee)
        if (!salary.getEmployee().getId().equals(salaryDto.getEmployeeId())) {
            throw new BadRequestException("Cannot change the employee for an existing salary");
        }
        
        // Validate budget if gross salary increases
        if (salaryDto.getGrossSalary() > salary.getGrossSalary()) {
            Department department = salary.getEmployee().getDepartment();
            double currentExpenses = department.calculateCurrentExpenses() - salary.getGrossSalary();
            
            if (currentExpenses + salaryDto.getGrossSalary() > department.getBudget()) {
                if (department.getBudgetType() == Department.BudgetType.MONTHLY) {
                    throw new BadRequestException("Updating this salary would exceed the monthly budget for department: " + department.getName());
                } else {
                    throw new BadRequestException("Updating this salary would exceed the yearly budget for department: " + department.getName());
                }
            }
        }
        
        salary.setGrossSalary(salaryDto.getGrossSalary());
        
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

    // Helper method to convert Entity to DTO
    private SalaryDto convertToDto(Salary salary) {
        SalaryDto dto = new SalaryDto();
        dto.setId(salary.getId());
        dto.setEmployeeId(salary.getEmployee().getId());
        dto.setEmployeeName(salary.getEmployee().getName());
        dto.setDepartmentName(salary.getEmployee().getDepartment().getName());
        dto.setGrossSalary(salary.getGrossSalary());
        dto.setNetSalary(salary.getNetSalary());
        
        List<DeductionDto> deductionDtos = salary.getDeductions().stream()
                .map(this::convertToDeductionDto)
                .collect(Collectors.toList());
        dto.setDeductions(deductionDtos);
        
        return dto;
    }
    
    // Helper method to convert Deduction Entity to DTO
    private DeductionDto convertToDeductionDto(Deduction deduction) {
        DeductionDto dto = new DeductionDto();
        dto.setId(deduction.getId());
        dto.setType(deduction.getType());
        dto.setName(deduction.getName());
        dto.setValue(deduction.getValue());
        dto.setPercentage(deduction.isPercentage());
        return dto;
    }
    
    // Helper method to convert Deduction DTO to Entity
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
