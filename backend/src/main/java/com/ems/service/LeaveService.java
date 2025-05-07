package com.ems.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.LeaveDto;
import com.ems.exception.BadRequestException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.Leave;
import com.ems.model.User;
import com.ems.model.Leave.Status;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.LeaveRepository;

/**
 * Service for managing employee leave requests
 */
@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private AuthService authService;
    
    /**
     * Get all leave requests for a user's company
     */
    @Transactional(readOnly = true)
    public List<LeaveDto> getAllLeaves() {
        User currentUser = authService.getCurrentUser();
        List<Leave> leaves = leaveRepository.findByUser(currentUser);
        
        return leaves.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get leave requests by status
     */
    @Transactional(readOnly = true)
    public List<LeaveDto> getLeavesByStatus(Status status) {
        User currentUser = authService.getCurrentUser();
        List<Leave> leaves = leaveRepository.findByUserAndStatus(currentUser, status);
        
        return leaves.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get paginated leave requests
     */
    @Transactional(readOnly = true)
    public Page<LeaveDto> getPaginatedLeaves(int page, int size, String sortBy, boolean ascending) {
        User currentUser = authService.getCurrentUser();
        Sort sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Leave> leavePage = leaveRepository.findByUserPaginated(currentUser, pageable);
        
        return leavePage.map(this::convertToDto);
    }
    
    /**
     * Get leave request by ID
     */
    @Transactional(readOnly = true)
    public LeaveDto getLeaveById(Long id) {
        User currentUser = authService.getCurrentUser();
        Leave leave = leaveRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        
        return convertToDto(leave);
    }
    
    /**
     * Get leave requests for a specific employee
     */
    @Transactional(readOnly = true)
    public List<LeaveDto> getLeavesByEmployee(Long employeeId) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(employeeId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        List<Leave> leaves = leaveRepository.findByEmployeeOrderByRequestDateDesc(employee);
        
        return leaves.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get leave requests for a specific department
     */
    @Transactional(readOnly = true)
    public List<LeaveDto> getLeavesByDepartment(Long departmentId) {
        User currentUser = authService.getCurrentUser();
        Department department = departmentRepository.findByIdAndUser(departmentId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
        
        List<Leave> leaves = leaveRepository.findByDepartment(department);
        
        return leaves.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get pending leave requests
     */
    @Transactional(readOnly = true)
    public List<LeaveDto> getPendingLeaves() {
        User currentUser = authService.getCurrentUser();
        List<Leave> leaves = leaveRepository.findPendingLeaves(currentUser);
        
        return leaves.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get current active leaves (employees currently on leave)
     */
    @Transactional(readOnly = true)
    public List<LeaveDto> getCurrentLeaves() {
        User currentUser = authService.getCurrentUser();
        List<Leave> leaves = leaveRepository.findCurrentLeavesByUser(currentUser, Status.APPROVED);
        
        return leaves.stream()
                .map(leave -> {
                    LeaveDto dto = convertToDto(leave);
                    dto.setCurrentlyActive(true);
                    
                    // Calculate days until end
                    LocalDate today = LocalDate.now();
                    int daysRemaining = (int) ChronoUnit.DAYS.between(today, leave.getEndDate());
                    dto.setDaysUntilEnd(daysRemaining);
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get upcoming leaves (approved but not yet started)
     */
    @Transactional(readOnly = true)
    public List<LeaveDto> getUpcomingLeaves() {
        User currentUser = authService.getCurrentUser();
        List<Leave> leaves = leaveRepository.findUpcomingLeaves(currentUser);
        
        return leaves.stream()
                .map(leave -> {
                    LeaveDto dto = convertToDto(leave);
                    
                    // Calculate days until start
                    LocalDate today = LocalDate.now();
                    int daysUntil = (int) ChronoUnit.DAYS.between(today, leave.getStartDate());
                    dto.setDaysUntilStart(daysUntil);
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get leaves starting soon
     */
    @Transactional(readOnly = true)
    public List<LeaveDto> getLeavesStartingSoon(int days) {
        User currentUser = authService.getCurrentUser();
        LocalDate futureDate = LocalDate.now().plusDays(days);
        
        List<Leave> leaves = leaveRepository.findLeavesStartingSoon(currentUser, futureDate);
        
        return leaves.stream()
                .map(leave -> {
                    LeaveDto dto = convertToDto(leave);
                    
                    // Calculate days until start
                    LocalDate today = LocalDate.now();
                    int daysUntil = (int) ChronoUnit.DAYS.between(today, leave.getStartDate());
                    dto.setDaysUntilStart(daysUntil);
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get leaves ending soon
     */
    @Transactional(readOnly = true)
    public List<LeaveDto> getLeavesEndingSoon(int days) {
        User currentUser = authService.getCurrentUser();
        LocalDate futureDate = LocalDate.now().plusDays(days);
        
        List<Leave> leaves = leaveRepository.findLeavesEndingSoon(currentUser, futureDate);
        
        return leaves.stream()
                .map(leave -> {
                    LeaveDto dto = convertToDto(leave);
                    
                    // Calculate days until end
                    LocalDate today = LocalDate.now();
                    int daysUntil = (int) ChronoUnit.DAYS.between(today, leave.getEndDate());
                    dto.setDaysUntilEnd(daysUntil);
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Search leaves by employee name
     */
    @Transactional(readOnly = true)
    public List<LeaveDto> searchLeavesByEmployeeName(String searchTerm) {
        User currentUser = authService.getCurrentUser();
        List<Leave> leaves = leaveRepository.searchLeavesByEmployeeName(currentUser, searchTerm);
        
        return leaves.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get leave statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getLeaveStatistics() {
        User currentUser = authService.getCurrentUser();
        Map<String, Object> statistics = new HashMap<>();
        
        // Count leaves by status
        List<Object[]> statusCounts = leaveRepository.countLeavesByStatus(currentUser);
        Map<String, Long> countsByStatus = new HashMap<>();
        long totalLeaves = 0;
        
        for (Object[] result : statusCounts) {
            Status status = (Status) result[0];
            Long count = (Long) result[1];
            totalLeaves += count;
            countsByStatus.put(status.toString(), count);
        }
        
        statistics.put("totalLeaves", totalLeaves);
        statistics.put("countsByStatus", countsByStatus);
        
        // Count leaves by department
        List<Object[]> departmentCounts = leaveRepository.countLeavesByDepartmentAndStatus(
                currentUser, Status.APPROVED);
        
        List<Map<String, Object>> departmentStats = new ArrayList<>();
        for (Object[] result : departmentCounts) {
            Map<String, Object> departmentData = new HashMap<>();
            departmentData.put("departmentName", result[0]);
            departmentData.put("approvedLeaveCount", result[1]);
            departmentStats.add(departmentData);
        }
        
        statistics.put("departmentStats", departmentStats);
        
        // Get leave trends by month
        List<Object[]> monthlyTrends = leaveRepository.getLeaveTrendsByMonth(currentUser, Status.APPROVED);
        List<Map<String, Object>> trendData = new ArrayList<>();
        
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        
        for (Object[] result : monthlyTrends) {
            Map<String, Object> monthData = new HashMap<>();
            int year = ((Number) result[0]).intValue();
            int month = ((Number) result[1]).intValue();
            Long count = (Long) result[2];
            
            String monthName = LocalDate.of(year, month, 1).format(monthFormatter);
            
            monthData.put("month", monthName);
            monthData.put("count", count);
            trendData.add(monthData);
        }
        
        statistics.put("monthlyTrends", trendData);
        
        // Get current statistics
        long currentOnLeave = leaveRepository.findCurrentLeavesByUser(currentUser, Status.APPROVED).size();
        List<Long> pendingRequestsList = leaveRepository.countByUserAndStatus(currentUser, Status.PENDING);
        long pendingRequests = pendingRequestsList.isEmpty() ? 0L : pendingRequestsList.get(0);
        
        statistics.put("currentOnLeave", currentOnLeave);
        statistics.put("pendingRequests", pendingRequests);
        
        return statistics;
    }
    
    /**
     * Create a new leave request
     */
    @Transactional
    public LeaveDto createLeave(LeaveDto leaveDto) {
        User currentUser = authService.getCurrentUser();
        
        // Validate employee
        Employee employee = employeeRepository.findByIdAndUser(leaveDto.getEmployeeId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + leaveDto.getEmployeeId()));
        
        // Check if employee is active
        if (!employee.isActive()) {
            throw new BadRequestException("Cannot create leave request for inactive employee");
        }
        
        // Validate dates
        validateLeaveDates(leaveDto);
        
        // Check for overlapping approved leaves
        List<Leave> overlappingLeaves = leaveRepository.findOverlappingLeavesForEmployee(
                employee, Status.APPROVED, leaveDto.getStartDate(), leaveDto.getEndDate());
        
        if (!overlappingLeaves.isEmpty()) {
            throw new BadRequestException("Employee already has approved leave during this period");
        }
        
        Leave leave = new Leave();
        leave.setEmployee(employee);
        leave.setStartDate(leaveDto.getStartDate());
        leave.setEndDate(leaveDto.getEndDate());
        leave.setReason(leaveDto.getReason());
        leave.setStatus(Status.PENDING);
        
        Leave savedLeave = leaveRepository.save(leave);
        return convertToDto(savedLeave);
    }
    
    /**
     * Update a leave request (only if pending)
     */
    @Transactional
    public LeaveDto updateLeave(Long id, LeaveDto leaveDto) {
        User currentUser = authService.getCurrentUser();
        Leave leave = leaveRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        
        // Only pending leaves can be updated
        if (leave.getStatus() != Status.PENDING) {
            throw new BadRequestException("Only pending leave requests can be updated");
        }
        
        // Validate dates
        validateLeaveDates(leaveDto);
        
        // Check for overlapping approved leaves (excluding this one)
        List<Leave> overlappingLeaves = leaveRepository.findOverlappingLeavesForEmployee(
                leave.getEmployee(), Status.APPROVED, leaveDto.getStartDate(), leaveDto.getEndDate());
        
        overlappingLeaves.removeIf(l -> l.getId().equals(id));
        
        if (!overlappingLeaves.isEmpty()) {
            throw new BadRequestException("Employee already has approved leave during this period");
        }
        
        leave.setStartDate(leaveDto.getStartDate());
        leave.setEndDate(leaveDto.getEndDate());
        leave.setReason(leaveDto.getReason());
        
        Leave updatedLeave = leaveRepository.save(leave);
        return convertToDto(updatedLeave);
    }
    
    /**
     * Approve a leave request
     */
    @Transactional
    public LeaveDto approveLeave(Long id) {
        User currentUser = authService.getCurrentUser();
        Leave leave = leaveRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        
        // Only pending leaves can be approved
        if (leave.getStatus() != Status.PENDING) {
            throw new BadRequestException("Only pending leave requests can be approved");
        }
        
        // Check for overlapping approved leaves
        List<Leave> overlappingLeaves = leaveRepository.findOverlappingLeavesForEmployee(
                leave.getEmployee(), Status.APPROVED, leave.getStartDate(), leave.getEndDate());
        
        if (!overlappingLeaves.isEmpty()) {
            throw new BadRequestException("Employee already has approved leave during this period");
        }
        
        leave.setStatus(Status.APPROVED);
        Leave updatedLeave = leaveRepository.save(leave);
        return convertToDto(updatedLeave);
    }
    
    /**
     * Deny a leave request
     */
    @Transactional
    public LeaveDto denyLeave(Long id) {
        User currentUser = authService.getCurrentUser();
        Leave leave = leaveRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        
        // Only pending leaves can be denied
        if (leave.getStatus() != Status.PENDING) {
            throw new BadRequestException("Only pending leave requests can be denied");
        }
        
        leave.setStatus(Status.DENIED);
        Leave updatedLeave = leaveRepository.save(leave);
        return convertToDto(updatedLeave);
    }
    
    /**
     * Cancel a leave request (delete it)
     */
    @Transactional
    public void cancelLeave(Long id) {
        User currentUser = authService.getCurrentUser();
        Leave leave = leaveRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        
        // Only pending leaves can be cancelled
        if (leave.getStatus() != Status.PENDING) {
            throw new BadRequestException("Only pending leave requests can be cancelled");
        }
        
        leaveRepository.delete(leave);
    }
    
    /**
     * Get overlapping leaves for an employee to check for conflicts
     */
    @Transactional(readOnly = true)
    public List<LeaveDto> checkLeaveOverlap(Long employeeId, LocalDate startDate, LocalDate endDate) {
        User currentUser = authService.getCurrentUser();
        
        // Validate employee
        Employee employee = employeeRepository.findByIdAndUser(employeeId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        // Find overlapping leaves
        List<Leave> overlappingLeaves = leaveRepository.findOverlappingLeavesForEmployee(
                employee, Status.APPROVED, startDate, endDate);
        
        return overlappingLeaves.stream()
                .map(leave -> {
                    LeaveDto dto = convertToDto(leave);
                    dto.setOverlapping(true);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Validate leave request dates
     */
    private void validateLeaveDates(LeaveDto leaveDto) {
        LocalDate today = LocalDate.now();
        
        if (leaveDto.getStartDate() == null) {
            throw new BadRequestException("Start date is required");
        }
        
        if (leaveDto.getEndDate() == null) {
            throw new BadRequestException("End date is required");
        }
        
        if (leaveDto.getStartDate().isBefore(today)) {
            throw new BadRequestException("Leave cannot start in the past");
        }
        
        if (leaveDto.getEndDate().isBefore(leaveDto.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }
    }
    
    /**
     * Convert Leave entity to LeaveDto
     */
    public LeaveDto convertToDto(Leave leave) {
        LeaveDto dto = new LeaveDto();
        dto.setId(leave.getId());
        dto.setEmployeeId(leave.getEmployee().getId());
        dto.setEmployeeName(leave.getEmployee().getName());
        dto.setEmployeeEmail(leave.getEmployee().getEmail());
        dto.setEmployeeIsActive(leave.getEmployee().isActive());
        
        if (leave.getEmployee().getDepartment() != null) {
            dto.setDepartmentId(leave.getEmployee().getDepartment().getId());
            dto.setDepartmentName(leave.getEmployee().getDepartment().getName());
        }
        
        dto.setStartDate(leave.getStartDate());
        dto.setEndDate(leave.getEndDate());
        dto.setReason(leave.getReason());
        dto.setStatus(leave.getStatus());
        dto.setRequestDate(leave.getRequestDate());
        dto.setDecisionDate(leave.getDecisionDate());
        
        // Calculate duration
        int durationInDays = leave.getDurationInDays();
        dto.setDurationInDays(durationInDays);
        dto.setDurationDisplayText(durationInDays + (durationInDays == 1 ? " day" : " days"));
        
        // Determine if currently active
        LocalDate today = LocalDate.now();
        if (leave.getStatus() == Status.APPROVED && 
            !today.isBefore(leave.getStartDate()) && 
            !today.isAfter(leave.getEndDate())) {
            dto.setCurrentlyActive(true);
            dto.setDaysUntilEnd((int) ChronoUnit.DAYS.between(today, leave.getEndDate()));
        } else if (leave.getStatus() == Status.APPROVED && today.isBefore(leave.getStartDate())) {
            dto.setDaysUntilStart((int) ChronoUnit.DAYS.between(today, leave.getStartDate()));
        }
        
        return dto;
    }
}