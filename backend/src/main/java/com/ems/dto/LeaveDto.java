package com.ems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.ems.model.Leave.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Data Transfer Object for Employee Leave requests
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveDto {

    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    // These fields are calculated, not required in input
    private String employeeName;
    private String employeeEmail;
    private String departmentName;
    private Long departmentId;
    private boolean employeeIsActive;

    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotBlank(message = "Reason is required")
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;

    // This field has a default value (PENDING), not required in input
    private Status status = Status.PENDING;

    // These fields are calculated, not required in input
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate requestDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime decisionDate;
    
    private String decisionDateFormatted;
    private String statusDisplayName;
    private int durationInDays;
    private String durationDisplayText;
    private boolean isCurrentlyActive;
    private int daysUntilStart;
    private int daysUntilEnd;
    private boolean isOverlapping;
    
    // Track conflicting leave IDs
    private Long[] conflictingLeaveIds;
    
    /**
     * Default constructor
     */
    public LeaveDto() {
    }
    
    /**
     * Constructor with essential fields
     */
    public LeaveDto(Long employeeId, LocalDate startDate, LocalDate endDate, String reason) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = Status.PENDING;
        updateCalculatedFields();
    }
    
    /**
     * Copy constructor
     */
    public LeaveDto(LeaveDto source) {
        this.id = source.id;
        this.employeeId = source.employeeId;
        this.employeeName = source.employeeName;
        this.employeeEmail = source.employeeEmail;
        this.departmentName = source.departmentName;
        this.departmentId = source.departmentId;
        this.employeeIsActive = source.employeeIsActive;
        this.startDate = source.startDate;
        this.endDate = source.endDate;
        this.reason = source.reason;
        this.status = source.status;
        this.requestDate = source.requestDate;
        this.decisionDate = source.decisionDate;
        this.statusDisplayName = source.statusDisplayName;
        this.durationInDays = source.durationInDays;
        this.durationDisplayText = source.durationDisplayText;
        this.isCurrentlyActive = source.isCurrentlyActive;
        updateCalculatedFields();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    
    public String getEmployeeEmail() {
        return employeeEmail;
    }
    
    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }
    
    public String getDepartmentName() {
        return departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public Long getDepartmentId() {
        return departmentId;
    }
    
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
    
    public boolean isEmployeeIsActive() {
        return employeeIsActive;
    }
    
    public void setEmployeeIsActive(boolean employeeIsActive) {
        this.employeeIsActive = employeeIsActive;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        updateCalculatedFields();
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        updateCalculatedFields();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        updateStatusDisplayName();
    }
    
    public LocalDate getRequestDate() {
        return requestDate;
    }
    
    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }
    
    public LocalDateTime getDecisionDate() {
        return decisionDate;
    }
    
    public void setDecisionDate(LocalDateTime decisionDate) {
        this.decisionDate = decisionDate;
        
        // Format the decision date for display
        if (decisionDate != null) {
            this.decisionDateFormatted = decisionDate.toString()
                    .replace("T", " ")
                    .substring(0, 19);
        }
    }
    
    public String getDecisionDateFormatted() {
        return decisionDateFormatted;
    }
    
    public void setDecisionDateFormatted(String decisionDateFormatted) {
        this.decisionDateFormatted = decisionDateFormatted;
    }
    
    public String getStatusDisplayName() {
        return statusDisplayName;
    }
    
    public void setStatusDisplayName(String statusDisplayName) {
        this.statusDisplayName = statusDisplayName;
    }
    
    public int getDurationInDays() {
        return durationInDays;
    }
    
    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }
    
    public String getDurationDisplayText() {
        return durationDisplayText;
    }
    
    public void setDurationDisplayText(String durationDisplayText) {
        this.durationDisplayText = durationDisplayText;
    }
    
    public boolean isCurrentlyActive() {
        return isCurrentlyActive;
    }
    
    public void setCurrentlyActive(boolean isCurrentlyActive) {
        this.isCurrentlyActive = isCurrentlyActive;
    }
    
    public int getDaysUntilStart() {
        return daysUntilStart;
    }
    
    public void setDaysUntilStart(int daysUntilStart) {
        this.daysUntilStart = daysUntilStart;
    }
    
    public int getDaysUntilEnd() {
        return daysUntilEnd;
    }
    
    public void setDaysUntilEnd(int daysUntilEnd) {
        this.daysUntilEnd = daysUntilEnd;
    }
    
    public boolean isOverlapping() {
        return isOverlapping;
    }
    
    public void setOverlapping(boolean isOverlapping) {
        this.isOverlapping = isOverlapping;
    }
    
    public Long[] getConflictingLeaveIds() {
        return conflictingLeaveIds;
    }
    
    public void setConflictingLeaveIds(Long[] conflictingLeaveIds) {
        this.conflictingLeaveIds = conflictingLeaveIds;
    }
    
    /**
     * Update calculated fields based on current state
     */
    private void updateCalculatedFields() {
        // Calculate duration in days
        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
                this.durationInDays = 0;
                this.durationDisplayText = "Invalid date range";
            } else {
                this.durationInDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1; // Include both start and end dates
                this.durationDisplayText = this.durationInDays + (this.durationInDays == 1 ? " day" : " days");
            }
        }
        
        // Calculate if leave is currently active
        LocalDate today = LocalDate.now();
        if (startDate != null && endDate != null && status == Status.APPROVED) {
            this.isCurrentlyActive = !today.isBefore(startDate) && !today.isAfter(endDate);
            
            // Calculate days until start (if not started yet)
            if (startDate.isAfter(today)) {
                this.daysUntilStart = (int) ChronoUnit.DAYS.between(today, startDate);
            } else {
                this.daysUntilStart = 0;
            }
            
            // Calculate days until end (if currently active)
            if (isCurrentlyActive) {
                this.daysUntilEnd = (int) ChronoUnit.DAYS.between(today, endDate);
            } else {
                this.daysUntilEnd = 0;
            }
        } else {
            this.isCurrentlyActive = false;
        }
        
        // Update status display name
        updateStatusDisplayName();
    }
    
    /**
     * Update status display name based on current status
     */
    private void updateStatusDisplayName() {
        if (status == null) {
            this.statusDisplayName = "";
            return;
        }
        
        this.statusDisplayName = switch (status) {
            case PENDING -> "Pending";
            case APPROVED -> "Approved";
            case DENIED -> "Denied";
        };
    }
}
