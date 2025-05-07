package com.ems.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.EmployeeInactivityDto;
import com.ems.exception.BadRequestException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.EmployeeInactivity;
import com.ems.model.User;
import com.ems.model.EmployeeInactivity.InactivityType;
import com.ems.repository.EmployeeInactivityRepository;
import com.ems.repository.EmployeeRepository;

@Service
public class EmployeeInactivityService {

    @Autowired
    private EmployeeInactivityRepository employeeInactivityRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<EmployeeInactivityDto> getAllInactivitiesByUser(User user) {
        List<EmployeeInactivity> inactivities = employeeInactivityRepository.findByUserOrderByStartDateDesc(user);
        return inactivities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeInactivityDto> getInactivitiesByEmployee(Long employeeId, User user) {
        Employee employee = employeeRepository.findByIdAndUser(employeeId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        List<EmployeeInactivity> inactivities = employeeInactivityRepository.findByEmployeeOrderByStartDateDesc(employee);
        return inactivities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeInactivityDto getInactivityById(Long id, User user) {
        EmployeeInactivity inactivity = employeeInactivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inactivity record not found with id: " + id));
        
        // Verify ownership
        if (!inactivity.getEmployee().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Inactivity record not found with id: " + id);
        }
        
        return convertToDto(inactivity);
    }

    @Transactional(readOnly = true)
    public Optional<EmployeeInactivityDto> getCurrentInactivityByEmployee(Long employeeId, User user) {
        Employee employee = employeeRepository.findByIdAndUser(employeeId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        Optional<EmployeeInactivity> currentInactivity = employeeInactivityRepository.findCurrentInactivityByEmployeeId(employeeId);
        return currentInactivity.map(this::convertToDto);
    }

    @Transactional
    public EmployeeInactivityDto createInactivity(EmployeeInactivityDto dto, User user) {
        // Validate input
        if (dto.getStartDate() == null) {
            throw new BadRequestException("Start date is required");
        }
        
        if (dto.getType() == null) {
            throw new BadRequestException("Inactivity type is required");
        }
        
        // Check if end date is after start date if provided
        if (dto.getEndDate() != null && dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }
        
        // Get employee and verify ownership
        Employee employee = employeeRepository.findByIdAndUser(dto.getEmployeeId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));
        
        // Check for overlapping inactivity periods
        List<EmployeeInactivity> overlappingInactivities = findOverlappingInactivities(
                employee, dto.getStartDate(), dto.getEndDate(), null);
        
        if (!overlappingInactivities.isEmpty()) {
            throw new BadRequestException("This inactivity period overlaps with an existing period");
        }
        
        // Create new inactivity record
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
        
        // Update employee status if needed
        if (inactivity.isCurrent()) {
            employee.setStatus(Employee.Status.INACTIVE);
            employee.setInactiveFrom(inactivity.getStartDate());
            employee.setInactiveTo(inactivity.getEndDate());
            employeeRepository.save(employee);
        }
        
        return convertToDto(savedInactivity);
    }

    @Transactional
    public EmployeeInactivityDto updateInactivity(Long id, EmployeeInactivityDto dto, User user) {
        // Validate input
        if (dto.getStartDate() == null) {
            throw new BadRequestException("Start date is required");
        }
        
        if (dto.getType() == null) {
            throw new BadRequestException("Inactivity type is required");
        }
        
        // Check if end date is after start date if provided
        if (dto.getEndDate() != null && dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }
        
        // Get inactivity record and verify ownership
        EmployeeInactivity inactivity = employeeInactivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inactivity record not found with id: " + id));
        
        if (!inactivity.getEmployee().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Inactivity record not found with id: " + id);
        }
        
        // Check for overlapping inactivity periods
        List<EmployeeInactivity> overlappingInactivities = findOverlappingInactivities(
                inactivity.getEmployee(), dto.getStartDate(), dto.getEndDate(), id);
        
        if (!overlappingInactivities.isEmpty()) {
            throw new BadRequestException("This inactivity period overlaps with an existing period");
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
                .orElseThrow(() -> new ResourceNotFoundException("Inactivity record not found with id: " + id));
        
        // Verify ownership
        if (!inactivity.getEmployee().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Inactivity record not found with id: " + id);
        }
        
        Employee employee = inactivity.getEmployee();
        employeeInactivityRepository.delete(inactivity);
        
        // Update employee status after deletion
        updateEmployeeStatus(employee);
    }
    
    @Transactional
    public EmployeeInactivityDto endInactivity(Long id, LocalDate endDate, User user) {
        // Validate input
        if (endDate == null) {
            throw new BadRequestException("End date is required");
        }
        
        // Get inactivity record and verify ownership
        EmployeeInactivity inactivity = employeeInactivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inactivity record not found with id: " + id));
        
        if (!inactivity.getEmployee().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Inactivity record not found with id: " + id);
        }
        
        // Check if end date is after start date
        if (inactivity.getStartDate().isAfter(endDate)) {
            throw new BadRequestException("End date must be after start date");
        }
        
        // Update end date
        inactivity.setEndDate(endDate);
        EmployeeInactivity updatedInactivity = employeeInactivityRepository.save(inactivity);
        
        // Update employee status
        Employee employee = inactivity.getEmployee();
        updateEmployeeStatus(employee);
        
        return convertToDto(updatedInactivity);
    }
    
    @Transactional(readOnly = true)
    public List<EmployeeInactivityDto> getCurrentInactivities(User user) {
        LocalDate today = LocalDate.now();
        List<EmployeeInactivity> currentInactivities = employeeInactivityRepository.findCurrentInactivities(user);
        return currentInactivities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<EmployeeInactivityDto> getInactivitiesByType(InactivityType type, User user) {
        List<EmployeeInactivity> inactivities = employeeInactivityRepository.findByTypeAndUser(type, user);
        return inactivities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<EmployeeInactivityDto> getInactivitiesByDateRange(LocalDate startDate, LocalDate endDate, User user) {
        List<EmployeeInactivity> inactivities = employeeInactivityRepository.findByDateRange(startDate, endDate, user);
        return inactivities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getInactivityStatistics(User user) {
        Map<String, Object> statistics = new HashMap<>();
        
        // Count by type
        Map<InactivityType, Long> countByType = new HashMap<>();
        for (InactivityType type : InactivityType.values()) {
            List<Long> countList = employeeInactivityRepository.countByTypeAndUser(type, user);
            long count = countList.isEmpty() ? 0L : countList.get(0);
            countByType.put(type, count);
        }
        statistics.put("countByType", countByType);
        
        // Current inactivities
        List<Long> currentCountList = employeeInactivityRepository.countCurrentInactivities(user);
        long currentCount = currentCountList.isEmpty() ? 0L : currentCountList.get(0);
        statistics.put("currentCount", currentCount);
        
        // Average duration (in days)
        Optional<Double> averageDurationOpt = employeeInactivityRepository.calculateAverageDuration(user);
        Double averageDuration = averageDurationOpt.orElse(0.0);
        statistics.put("averageDuration", averageDuration);
        
        // Inactivities by month
        List<Object[]> byMonth = employeeInactivityRepository.countInactivitiesByMonth(user);
        statistics.put("byMonth", byMonth);
        
        return statistics;
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getInactivitiesByMonth(User user) {
        return employeeInactivityRepository.countInactivitiesByMonth(user);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getInactivitiesByDepartment(User user) {
        return employeeInactivityRepository.countInactivitiesByDepartment(user);
    }
    
    // Helper method to update employee status based on inactivity records
    private void updateEmployeeStatus(Employee employee) {
        LocalDate today = LocalDate.now();
        
        // Find the current inactivity record (if any)
        Optional<EmployeeInactivity> currentInactivity = employeeInactivityRepository.findByEmployee(employee)
                .stream()
                .filter(ei -> ei.isActiveOn(today))
                .findFirst();
        
        if (currentInactivity.isPresent()) {
            // Employee has a current inactivity period
            EmployeeInactivity inactivity = currentInactivity.get();
            if (employee.isActive()) {
                employee.setStatus(Employee.Status.INACTIVE);
                employee.setInactiveFrom(inactivity.getStartDate());
                employee.setInactiveTo(inactivity.getEndDate());
                employeeRepository.save(employee);
            } else {
                // Update inactivity period if needed
                if (!inactivity.getStartDate().equals(employee.getInactiveFrom()) ||
                    (inactivity.getEndDate() != null && employee.getInactiveTo() != null && 
                     !inactivity.getEndDate().equals(employee.getInactiveTo()))) {
                    employee.setInactiveFrom(inactivity.getStartDate());
                    employee.setInactiveTo(inactivity.getEndDate());
                    employeeRepository.save(employee);
                }
            }
        } else {
            // Employee has no current inactivity period
            if (!employee.isActive()) {
                employee.setStatus(Employee.Status.ACTIVE);
                employee.setInactiveFrom(null);
                employee.setInactiveTo(null);
                employeeRepository.save(employee);
            }
        }
    }
    
    // Find overlapping inactivity periods for an employee
    private List<EmployeeInactivity> findOverlappingInactivities(Employee employee, 
                                                               LocalDate startDate, 
                                                               LocalDate endDate,
                                                               Long excludeId) {
        if (startDate == null) {
            return List.of();
        }
        
        // If endDate is null, it means indefinite period (use far future date for comparison)
        LocalDate effectiveEndDate = endDate != null ? endDate : LocalDate.of(9999, 12, 31);
        
        return employeeInactivityRepository.findByEmployeeId(employee.getId())
                .stream()
                .filter(ei -> !ei.getId().equals(excludeId)) // Exclude the record being updated
                .filter(ei -> {
                    LocalDate eiEndDate = ei.getEndDate() != null ? ei.getEndDate() : LocalDate.of(9999, 12, 31);
                    
                    // Check if periods overlap
                    return !(effectiveEndDate.isBefore(ei.getStartDate()) || startDate.isAfter(eiEndDate));
                })
                .collect(Collectors.toList());
    }

    // Convert Entity to DTO
    private EmployeeInactivityDto convertToDto(EmployeeInactivity inactivity) {
        EmployeeInactivityDto dto = new EmployeeInactivityDto();
        dto.setId(inactivity.getId());
        dto.setEmployeeId(inactivity.getEmployee().getId());
        dto.setEmployeeName(inactivity.getEmployee().getName());
        
        // Set additional employee and department info
        dto.setEmployeeEmail(inactivity.getEmployee().getEmail());
        Department department = inactivity.getEmployee().getDepartment();
        if (department != null) {
            dto.setDepartmentName(department.getName());
        }
        
        // Set basic properties
        dto.setStartDate(inactivity.getStartDate());
        dto.setEndDate(inactivity.getEndDate());
        dto.setReason(inactivity.getReason());
        dto.setType(inactivity.getType());
        
        // Calculate and set derived properties
        dto.setCurrent(inactivity.isCurrent());
        dto.setDurationInDays(inactivity.getDurationInDays());
        
        // These will be calculated automatically with our enhanced DTO
        dto.setStartDate(inactivity.getStartDate()); 
        dto.setEndDate(inactivity.getEndDate());
        dto.setType(inactivity.getType());
        
        return dto;
    }
}