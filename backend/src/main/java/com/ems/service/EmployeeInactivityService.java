package com.ems.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.EmployeeInactivityDto;
import com.ems.exception.BadRequestException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Employee;
import com.ems.model.EmployeeInactivity;
import com.ems.model.User;
import com.ems.repository.EmployeeInactivityRepository;
import com.ems.repository.EmployeeRepository;

@Service
public class EmployeeInactivityService {

    @Autowired
    private EmployeeInactivityRepository employeeInactivityRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private AuthService authService;
    
    @Transactional(readOnly = true)
    public List<EmployeeInactivityDto> getAllInactivitiesForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<EmployeeInactivity> inactivities = employeeInactivityRepository.findByUser(currentUser);
        
        return inactivities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<EmployeeInactivityDto> getInactivitiesForEmployee(Long employeeId) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(employeeId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        List<EmployeeInactivity> inactivities = employeeInactivityRepository.findByEmployee(employee);
        
        return inactivities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public EmployeeInactivityDto createInactivity(EmployeeInactivityDto inactivityDto) {
        User currentUser = authService.getCurrentUser();
        
        // Validate employee
        Employee employee = employeeRepository.findByIdAndUser(inactivityDto.getEmployeeId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + inactivityDto.getEmployeeId()));
        
        // Validate dates
        if (inactivityDto.getEndDate() != null && inactivityDto.getStartDate().isAfter(inactivityDto.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }
        
        EmployeeInactivity inactivity = new EmployeeInactivity();
        inactivity.setEmployee(employee);
        inactivity.setStartDate(inactivityDto.getStartDate());
        inactivity.setEndDate(inactivityDto.getEndDate());
        inactivity.setReason(inactivityDto.getReason());
        
        // Also update employee's active status
        employee.setActive(false);
        employee.setInactiveFrom(inactivityDto.getStartDate());
        employee.setInactiveTo(inactivityDto.getEndDate());
        employeeRepository.save(employee);
        
        EmployeeInactivity savedInactivity = employeeInactivityRepository.save(inactivity);
        return convertToDto(savedInactivity);
    }
    
    @Transactional
    public EmployeeInactivityDto updateInactivity(Long id, EmployeeInactivityDto inactivityDto) {
        User currentUser = authService.getCurrentUser();
        
        // Find inactivity record
        EmployeeInactivity inactivity = employeeInactivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inactivity record not found with id: " + id));
        
        // Check if inactivity belongs to current user's employee
        if (!inactivity.getEmployee().getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Inactivity record not found with id: " + id);
        }
        
        // Validate dates
        if (inactivityDto.getEndDate() != null && inactivityDto.getStartDate().isAfter(inactivityDto.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }
        
        inactivity.setStartDate(inactivityDto.getStartDate());
        inactivity.setEndDate(inactivityDto.getEndDate());
        inactivity.setReason(inactivityDto.getReason());
        
        // Update employee's inactivity dates if this is the current inactivity period
        Employee employee = inactivity.getEmployee();
        LocalDate today = LocalDate.now();
        if (!employee.isActive() && 
            (today.isEqual(inactivity.getStartDate()) || today.isAfter(inactivity.getStartDate())) && 
            (inactivity.getEndDate() == null || today.isBefore(inactivity.getEndDate()) || today.isEqual(inactivity.getEndDate()))) {
            
            employee.setInactiveFrom(inactivityDto.getStartDate());
            employee.setInactiveTo(inactivityDto.getEndDate());
            employeeRepository.save(employee);
        }
        
        EmployeeInactivity updatedInactivity = employeeInactivityRepository.save(inactivity);
        return convertToDto(updatedInactivity);
    }
    
    @Transactional
    public void deleteInactivity(Long id) {
        User currentUser = authService.getCurrentUser();
        
        // Find inactivity record
        EmployeeInactivity inactivity = employeeInactivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inactivity record not found with id: " + id));
        
        // Check if inactivity belongs to current user's employee
        if (!inactivity.getEmployee().getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Inactivity record not found with id: " + id);
        }
        
        // If this is the current inactivity and we're deleting it, update employee active status
        Employee employee = inactivity.getEmployee();
        LocalDate today = LocalDate.now();
        if (!employee.isActive() && 
            (today.isEqual(inactivity.getStartDate()) || today.isAfter(inactivity.getStartDate())) && 
            (inactivity.getEndDate() == null || today.isBefore(inactivity.getEndDate()) || today.isEqual(inactivity.getEndDate()))) {
            
            employee.setActive(true);
            employee.setInactiveFrom(null);
            employee.setInactiveTo(null);
            employeeRepository.save(employee);
        }
        
        employeeInactivityRepository.delete(inactivity);
    }
    
    // Helper method to convert Entity to DTO
    private EmployeeInactivityDto convertToDto(EmployeeInactivity inactivity) {
        EmployeeInactivityDto dto = new EmployeeInactivityDto();
        dto.setId(inactivity.getId());
        dto.setEmployeeId(inactivity.getEmployee().getId());
        dto.setEmployeeName(inactivity.getEmployee().getName());
        dto.setStartDate(inactivity.getStartDate());
        dto.setEndDate(inactivity.getEndDate());
        dto.setReason(inactivity.getReason());
        dto.setDurationDays(inactivity.getDurationInDays());
        return dto;
    }
}