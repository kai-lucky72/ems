package com.ems.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.LeaveDto;
import com.ems.exception.BadRequestException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Employee;
import com.ems.model.Leave;
import com.ems.model.User;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.LeaveRepository;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public List<LeaveDto> getAllLeavesForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<Leave> leaves = leaveRepository.findByUser(currentUser);
        
        return leaves.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LeaveDto getLeaveById(Long id) {
        User currentUser = authService.getCurrentUser();
        Leave leave = leaveRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + id));
        
        return convertToDto(leave);
    }

    @Transactional(readOnly = true)
    public List<LeaveDto> getLeavesByEmployeeId(Long employeeId) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(employeeId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        List<Leave> leaves = leaveRepository.findByEmployee(employee);
        
        return leaves.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeaveDto createLeave(LeaveDto leaveDto) {
        User currentUser = authService.getCurrentUser();
        
        // Validate employee
        Employee employee = employeeRepository.findByIdAndUser(leaveDto.getEmployeeId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + leaveDto.getEmployeeId()));
        
        // Validate dates
        if (leaveDto.getStartDate().isAfter(leaveDto.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }
        
        if (leaveDto.getStartDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Start date cannot be in the past");
        }
        
        Leave leave = new Leave();
        leave.setEmployee(employee);
        leave.setStartDate(leaveDto.getStartDate());
        leave.setEndDate(leaveDto.getEndDate());
        leave.setReason(leaveDto.getReason());
        leave.setStatus(Leave.Status.PENDING);
        
        Leave savedLeave = leaveRepository.save(leave);
        return convertToDto(savedLeave);
    }

    @Transactional
    public LeaveDto updateLeaveStatus(Long id, Leave.Status status) {
        User currentUser = authService.getCurrentUser();
        Leave leave = leaveRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with id: " + id));
        
        if (leave.getStatus() != Leave.Status.PENDING) {
            throw new BadRequestException("Cannot update status of a leave that is not pending");
        }
        
        leave.setStatus(status);
        Leave updatedLeave = leaveRepository.save(leave);
        return convertToDto(updatedLeave);
    }

    // Helper method to convert Entity to DTO
    private LeaveDto convertToDto(Leave leave) {
        LeaveDto dto = new LeaveDto();
        dto.setId(leave.getId());
        dto.setEmployeeId(leave.getEmployee().getId());
        dto.setEmployeeName(leave.getEmployee().getName());
        dto.setStartDate(leave.getStartDate());
        dto.setEndDate(leave.getEndDate());
        dto.setReason(leave.getReason());
        dto.setStatus(leave.getStatus());
        dto.setCreatedAt(leave.getCreatedAt());
        return dto;
    }
}
