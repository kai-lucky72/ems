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
        PERSONAL, MEDICAL, ADMINISTRATIVE, SABBATICAL, TERMINATION, OTHER;
        
        // Convert from database values
        public static InactivityType fromString(String str) {
            if (str == null) return null;
            
            return switch (str.toUpperCase()) {
                case "PERSONAL" -> PERSONAL;
                case "MEDICAL" -> MEDICAL;
                case "ADMINISTRATIVE" -> ADMINISTRATIVE;
                case "SABBATICAL" -> SABBATICAL;
                case "TERMINATION" -> TERMINATION;
                case "OTHER" -> OTHER;
                default -> throw new IllegalArgumentException("Unknown inactivity type: " + str);
            };
        }
        
        // Convert to database values
        public String toDatabaseValue() {
            return name().toLowerCase();
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
    
    // Check if inactivity overlaps with given date range
    public boolean overlapsWithDateRange(LocalDate rangeStart, LocalDate rangeEnd) {
        if (endDate == null) {
            // Indefinite inactivity period
            return !rangeEnd.isBefore(startDate);
        }
        
        return !(rangeEnd.isBefore(startDate) || rangeStart.isAfter(endDate));
    }
}