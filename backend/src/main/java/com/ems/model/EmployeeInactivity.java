package com.ems.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_inactivity_periods")
public class EmployeeInactivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 1000)
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "inactivity_type", nullable = false)
    private InactivityType type = InactivityType.PERSONAL;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum InactivityType {
        PERSONAL,        // Personal reasons
        MEDICAL,         // Medical leave
        ADMINISTRATIVE,  // Administrative reasons
        SABBATICAL,      // Sabbatical leave
        SUSPENSION,      // Employee suspended
        UNPAID_LEAVE,    // Unpaid leave
        PARENTAL;        // Parental leave
        
        // Convert from database values
        public static InactivityType fromString(String str) {
            if (str == null) return null;
            
            return switch (str.toUpperCase()) {
                case "PERSONAL" -> PERSONAL;
                case "MEDICAL" -> MEDICAL;
                case "ADMINISTRATIVE" -> ADMINISTRATIVE;
                case "SABBATICAL" -> SABBATICAL;
                case "SUSPENSION" -> SUSPENSION;
                case "UNPAID_LEAVE" -> UNPAID_LEAVE;
                case "PARENTAL" -> PARENTAL;
                default -> throw new IllegalArgumentException("Unknown inactivity type: " + str);
            };
        }
        
        // Convert to database values
        public String toDatabaseValue() {
            return name().toLowerCase();
        }
        
        // Check if this type affects salary calculation
        public boolean affectsSalary() {
            return this == UNPAID_LEAVE || this == SUSPENSION;
        }
        
        // Get display name
        public String getDisplayName() {
            return switch (this) {
                case PERSONAL -> "Personal";
                case MEDICAL -> "Medical Leave";
                case ADMINISTRATIVE -> "Administrative";
                case SABBATICAL -> "Sabbatical";
                case SUSPENSION -> "Suspension";
                case UNPAID_LEAVE -> "Unpaid Leave";
                case PARENTAL -> "Parental Leave";
            };
        }
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Default constructor
    public EmployeeInactivity() {
    }
    
    // Constructor with essential fields
    public EmployeeInactivity(Employee employee, LocalDate startDate, LocalDate endDate, String reason, InactivityType type) {
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Calculate duration in days
    public int getDurationInDays() {
        if (endDate == null) {
            // If end date is not set, calculate using current date
            return (int) (LocalDate.now().toEpochDay() - startDate.toEpochDay() + 1);
        }
        return (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);
    }
    
    // Check if this inactivity is current (ongoing)
    public boolean isCurrent() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && (endDate == null || !today.isAfter(endDate));
    }
    
    // Check if this inactivity is active on a specific date
    public boolean isActiveOnDate(LocalDate date) {
        return !date.isBefore(startDate) && (endDate == null || !date.isAfter(endDate));
    }
    
    // Alias method with a different name, for code clarity in some contexts
    public boolean isActiveOn(LocalDate date) {
        return isActiveOnDate(date);
    }
    
    // Check if inactivity overlaps with given date range
    public boolean overlapsWithDateRange(LocalDate rangeStart, LocalDate rangeEnd) {
        if (endDate == null) {
            // Indefinite inactivity period
            return !rangeEnd.isBefore(startDate);
        }
        
        return !(rangeEnd.isBefore(startDate) || rangeStart.isAfter(endDate));
    }
    
    // Check if this inactivity overlaps with another
    public boolean overlapsWithInactivity(EmployeeInactivity other) {
        if (other == null) {
            return false;
        }
        
        LocalDate thisEnd = this.endDate != null ? this.endDate : LocalDate.MAX;
        LocalDate otherEnd = other.getEndDate() != null ? other.getEndDate() : LocalDate.MAX;
        
        return !(thisEnd.isBefore(other.getStartDate()) || this.startDate.isAfter(otherEnd));
    }
    
    // Get the duration formatted as a string (e.g., "2 months, 5 days")
    public String getFormattedDuration() {
        if (startDate == null) {
            return "";
        }
        
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        if (end.isBefore(startDate)) {
            return "0 days";
        }
        
        long days = getDurationInDays();
        long months = days / 30;
        long years = months / 12;
        
        days = days % 30;
        months = months % 12;
        
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
        
        return sb.toString();
    }
    
    // Get the date range formatted as a string (e.g., "Jan 5, 2023 - Mar 10, 2023")
    public String getFormattedDateRange() {
        if (startDate == null) {
            return "";
        }
        
        String start = startDate.getMonth().toString().substring(0, 3) + " " + 
                      startDate.getDayOfMonth() + ", " + startDate.getYear();
        
        String end = endDate != null 
            ? endDate.getMonth().toString().substring(0, 3) + " " + 
              endDate.getDayOfMonth() + ", " + endDate.getYear()
            : "Present";
        
        return start + " - " + end;
    }
}