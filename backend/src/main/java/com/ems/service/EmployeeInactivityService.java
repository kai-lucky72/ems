package com.ems.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.EmployeeInactivityDto;
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

    public List<EmployeeInactivityDto> getAllInactivitiesByUser(User user) {
        List<EmployeeInactivity> inactivities = employeeInactivityRepository.findByUser(user);
        return inactivities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<EmployeeInactivityDto> getInactivitiesByEmployee(Long employeeId, User user) {
        Employee employee = employeeRepository.findByIdAndUser(employeeId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        List<EmployeeInactivity> inactivities = employeeInactivityRepository.findByEmployee(employee);
        return inactivities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public EmployeeInactivityDto getInactivityById(Long id, User user) {
        EmployeeInactivity inactivity = employeeInactivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inactivity record not found"));
        
        // Verify ownership
        if (!inactivity.getEmployee().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Inactivity record not found");
        }
        
        return convertToDto(inactivity);
    }

    public Optional<EmployeeInactivityDto> getCurrentInactivityByEmployee(Long employeeId, User user) {
        Employee employee = employeeRepository.findByIdAndUser(employeeId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        Optional<EmployeeInactivity> currentInactivity = employeeInactivityRepository.findCurrentInactivityByEmployeeId(employeeId);
        return currentInactivity.map(this::convertToDto);
    }

    @Transactional
    public EmployeeInactivityDto createInactivity(EmployeeInactivityDto dto, User user) {
        Employee employee = employeeRepository.findByIdAndUser(dto.getEmployeeId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        EmployeeInactivity inactivity = new EmployeeInactivity();
        inactivity.setEmployee(employee);
        inactivity.setStartDate(dto.getStartDate());
        inactivity.setEndDate(dto.getEndDate());
        inactivity.setReason(dto.getReason());
        inactivity.setType(dto.getType());
        
        // When adding a new inactivity record without end date (indefinite), 
        // close any existing open inactivity periods
        if (dto.getEndDate() == null) {
            List<EmployeeInactivity> openInactivities = employeeInactivityRepository.findByEmployeeId(employee.getId())
                    .stream()
                    .filter(ei -> ei.getEndDate() == null)
                    .collect(Collectors.toList());
            
            // Close other open inactivity periods
            LocalDate yesterday = LocalDate.now().minusDays(1);
            for (EmployeeInactivity existingInactivity : openInactivities) {
                existingInactivity.setEndDate(yesterday);
                employeeInactivityRepository.save(existingInactivity);
            }
        }
        
        EmployeeInactivity savedInactivity = employeeInactivityRepository.save(inactivity);
        
        // If employee is active but adding inactivity, set the employee to inactive
        if (employee.isActive() && inactivity.isCurrent()) {
            employee.setStatus(Employee.Status.INACTIVE);
            employeeRepository.save(employee);
        }
        
        return convertToDto(savedInactivity);
    }

    @Transactional
    public EmployeeInactivityDto updateInactivity(Long id, EmployeeInactivityDto dto, User user) {
        EmployeeInactivity inactivity = employeeInactivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inactivity record not found"));
        
        // Verify ownership
        if (!inactivity.getEmployee().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Inactivity record not found");
        }
        
        // Update fields
        inactivity.setStartDate(dto.getStartDate());
        inactivity.setEndDate(dto.getEndDate());
        inactivity.setReason(dto.getReason());
        inactivity.setType(dto.getType());
        
        EmployeeInactivity updatedInactivity = employeeInactivityRepository.save(inactivity);
        
        // Check and update employee status if needed
        updateEmployeeStatus(inactivity.getEmployee());
        
        return convertToDto(updatedInactivity);
    }

    @Transactional
    public void deleteInactivity(Long id, User user) {
        EmployeeInactivity inactivity = employeeInactivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inactivity record not found"));
        
        // Verify ownership
        if (!inactivity.getEmployee().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Inactivity record not found");
        }
        
        Employee employee = inactivity.getEmployee();
        employeeInactivityRepository.delete(inactivity);
        
        // Update employee status after deletion
        updateEmployeeStatus(employee);
    }
    
    @Transactional
    public EmployeeInactivityDto endInactivity(Long id, LocalDate endDate, User user) {
        EmployeeInactivity inactivity = employeeInactivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inactivity record not found"));
        
        // Verify ownership
        if (!inactivity.getEmployee().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Inactivity record not found");
        }
        
        inactivity.setEndDate(endDate);
        EmployeeInactivity updatedInactivity = employeeInactivityRepository.save(inactivity);
        
        // Update employee status
        Employee employee = inactivity.getEmployee();
        updateEmployeeStatus(employee);
        
        return convertToDto(updatedInactivity);
    }
    
    public List<EmployeeInactivityDto> getCurrentInactivities(User user) {
        List<EmployeeInactivity> currentInactivities = employeeInactivityRepository.findCurrentInactivities(user);
        return currentInactivities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<Object[]> getInactivitiesByMonth(User user) {
        return employeeInactivityRepository.countInactivitiesByMonth(user);
    }
    
    // Helper method to update employee status based on inactivity records
    private void updateEmployeeStatus(Employee employee) {
        boolean hasCurrentInactivity = employeeInactivityRepository.findByEmployee(employee)
                .stream()
                .anyMatch(EmployeeInactivity::isCurrent);
        
        if (hasCurrentInactivity && employee.isActive()) {
            employee.setStatus(Employee.Status.INACTIVE);
            employeeRepository.save(employee);
        } else if (!hasCurrentInactivity && !employee.isActive()) {
            employee.setStatus(Employee.Status.ACTIVE);
            employeeRepository.save(employee);
        }
    }

    // Convert Entity to DTO
    private EmployeeInactivityDto convertToDto(EmployeeInactivity inactivity) {
        EmployeeInactivityDto dto = new EmployeeInactivityDto();
        dto.setId(inactivity.getId());
        dto.setEmployeeId(inactivity.getEmployee().getId());
        dto.setEmployeeName(inactivity.getEmployee().getName());
        dto.setStartDate(inactivity.getStartDate());
        dto.setEndDate(inactivity.getEndDate());
        dto.setReason(inactivity.getReason());
        dto.setType(inactivity.getType());
        dto.setCurrent(inactivity.isCurrent());
        dto.setDurationInDays(inactivity.getDurationInDays());
        return dto;
    }
}