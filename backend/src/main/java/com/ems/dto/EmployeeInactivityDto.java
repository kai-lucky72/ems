package com.ems.dto;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.ems.model.EmployeeInactivity.InactivityType;

@JsonInclude(Include.NON_NULL)
public class EmployeeInactivityDto {

    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    // These fields are calculated, not required in input
    private String employeeName;
    private String employeeEmail;
    private String departmentName;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;

    @NotNull(message = "Inactivity type is required")
    private InactivityType type;

    // These fields are calculated, not required in input
    private boolean isCurrent;
    private int durationInDays;
    private String formattedDuration;
    private String formattedDateRange;
    private String typeDisplayName;
    private boolean affectsSalary;

    // Constructors
    public EmployeeInactivityDto() {
    }
    
    // Constructor with essential fields
    public EmployeeInactivityDto(Long employeeId, LocalDate startDate, LocalDate endDate, 
                                String reason, InactivityType type) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.type = type;
        updateCalculatedFields();
    }
    
    // Copy constructor
    public EmployeeInactivityDto(EmployeeInactivityDto source) {
        this.id = source.id;
        this.employeeId = source.employeeId;
        this.employeeName = source.employeeName;
        this.employeeEmail = source.employeeEmail;
        this.departmentName = source.departmentName;
        this.startDate = source.startDate;
        this.endDate = source.endDate;
        this.reason = source.reason;
        this.type = source.type;
        this.isCurrent = source.isCurrent;
        this.durationInDays = source.durationInDays;
        this.formattedDuration = source.formattedDuration;
        this.formattedDateRange = source.formattedDateRange;
        this.typeDisplayName = source.typeDisplayName;
        this.affectsSalary = source.affectsSalary;
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

    public InactivityType getType() {
        return type;
    }

    public void setType(InactivityType type) {
        this.type = type;
        // Update type-dependent calculated fields
        if (type != null) {
            updateTypeDisplayName();
            this.affectsSalary = type == InactivityType.UNPAID_LEAVE || 
                                 type == InactivityType.SUSPENSION;
        }
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }
    
    public String getFormattedDuration() {
        return formattedDuration;
    }
    
    public void setFormattedDuration(String formattedDuration) {
        this.formattedDuration = formattedDuration;
    }
    
    public String getFormattedDateRange() {
        return formattedDateRange;
    }
    
    public void setFormattedDateRange(String formattedDateRange) {
        this.formattedDateRange = formattedDateRange;
    }
    
    public String getTypeDisplayName() {
        return typeDisplayName;
    }
    
    public void setTypeDisplayName(String typeDisplayName) {
        this.typeDisplayName = typeDisplayName;
    }
    
    public boolean isAffectsSalary() {
        return affectsSalary;
    }
    
    public void setAffectsSalary(boolean affectsSalary) {
        this.affectsSalary = affectsSalary;
    }
    
    // Helper methods
    private void updateCalculatedFields() {
        // Update current status
        LocalDate today = LocalDate.now();
        this.isCurrent = startDate != null && 
                         (startDate.isEqual(today) || startDate.isBefore(today)) && 
                         (endDate == null || endDate.isAfter(today));
        
        // Update duration
        if (startDate != null) {
            LocalDate end = endDate != null ? endDate : today;
            if (end.isBefore(startDate)) {
                this.durationInDays = 0;
            } else {
                this.durationInDays = (int) ChronoUnit.DAYS.between(startDate, end) + 1; // inclusive
            }
            updateFormattedDuration();
        }
        
        // Update date range
        updateFormattedDateRange();
        
        // Update type display name if type is set
        if (type != null) {
            updateTypeDisplayName();
        }
    }
    
    private void updateFormattedDuration() {
        if (startDate == null) {
            this.formattedDuration = "";
            return;
        }
        
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        if (end.isBefore(startDate)) {
            this.formattedDuration = "0 days";
            return;
        }
        
        Period period = Period.between(startDate, end);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays() + 1; // Include both start and end date
        
        StringBuilder sb = new StringBuilder();
        if (years > 0) {
            sb.append(years).append(years == 1 ? " year" : " years");
            if (months > 0 || days > 0) sb.append(", ");
        }
        if (months > 0) {
            sb.append(months).append(months == 1 ? " month" : " months");
            if (days > 0) sb.append(", ");
        }
        if (days > 0 || (years == 0 && months == 0)) {
            sb.append(days).append(days == 1 ? " day" : " days");
        }
        
        this.formattedDuration = sb.toString();
    }
    
    private void updateFormattedDateRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        if (startDate == null) {
            this.formattedDateRange = "";
            return;
        }
        
        String start = startDate.format(formatter);
        String end = endDate != null ? endDate.format(formatter) : "Present";
        this.formattedDateRange = start + " - " + end;
    }
    
    private void updateTypeDisplayName() {
        if (type == null) {
            this.typeDisplayName = "";
            return;
        }
        
        this.typeDisplayName = switch (type) {
            case ADMINISTRATIVE -> "Administrative";
            case UNPAID_LEAVE -> "Unpaid Leave";
            case SUSPENSION -> "Suspension";
            case SABBATICAL -> "Sabbatical";
            case PERSONAL -> "Personal";
            case MEDICAL -> "Medical Leave";
            case PARENTAL -> "Parental Leave";
        };
    }
    
    // Check if this inactivity overlaps with another
    public boolean overlaps(EmployeeInactivityDto other) {
        if (other == null || other.getStartDate() == null || this.startDate == null) {
            return false;
        }
        
        LocalDate thisEnd = this.endDate != null ? this.endDate : LocalDate.MAX;
        LocalDate otherEnd = other.getEndDate() != null ? other.getEndDate() : LocalDate.MAX;
        
        return !(thisEnd.isBefore(other.getStartDate()) || this.startDate.isAfter(otherEnd));
    }
    
    // Check if this inactivity is active on a specific date
    public boolean isActiveOn(LocalDate date) {
        if (date == null || startDate == null) {
            return false;
        }
        
        boolean afterOrEqualStart = date.isEqual(startDate) || date.isAfter(startDate);
        boolean beforeOrEqualEnd = endDate == null || date.isEqual(endDate) || date.isBefore(endDate);
        
        return afterOrEqualStart && beforeOrEqualEnd;
    }
}