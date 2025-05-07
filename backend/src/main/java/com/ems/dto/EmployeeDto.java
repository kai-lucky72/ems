package com.ems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.ems.model.Employee.ContractType;
import com.ems.model.Employee.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class EmployeeDto {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    private String phone;

    @NotBlank(message = "Role is required")
    @Size(min = 2, max = 100, message = "Role must be between 2 and 100 characters")
    private String role;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    // These fields are calculated, not required in input
    private String departmentName;
    private Double departmentBudgetUsagePercent;

    @NotNull(message = "Contract type is required")
    private ContractType contractType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private boolean isActive = true;
    private Status status;

    // Inactivity information
    private LocalDate inactiveFrom;
    private LocalDate inactiveTo;
    private String inactivityReason;
    private Long currentInactivityId;

    // Salary information
    private Double currentSalary;
    private Double netSalary;

    // Leave information
    private boolean onLeave;
    private Long activeLeaveId;
    private LocalDate leaveEndDate;

    // Additional metadata
    private LocalDateTime createdAt;
    private boolean hasMessages;
    private int pendingLeaveRequests;
    private List<SalaryDto> salaryHistory;
    private List<EmployeeInactivityDto> inactivityHistory;

    // Constructors
    public EmployeeDto() {
    }
    
    // Copy constructor for creating a new instance with same values
    public EmployeeDto(EmployeeDto source) {
        this.id = source.id;
        this.name = source.name;
        this.email = source.email;
        this.phone = source.phone;
        this.role = source.role;
        this.departmentId = source.departmentId;
        this.departmentName = source.departmentName;
        this.contractType = source.contractType;
        this.startDate = source.startDate;
        this.endDate = source.endDate;
        this.isActive = source.isActive;
        this.status = source.status;
        this.inactiveFrom = source.inactiveFrom;
        this.inactiveTo = source.inactiveTo;
        this.inactivityReason = source.inactivityReason;
        this.onLeave = source.onLeave;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public Double getDepartmentBudgetUsagePercent() {
        return departmentBudgetUsagePercent;
    }
    
    public void setDepartmentBudgetUsagePercent(Double departmentBudgetUsagePercent) {
        this.departmentBudgetUsagePercent = departmentBudgetUsagePercent;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
        this.isActive = (status == Status.ACTIVE);
    }

    public LocalDate getInactiveFrom() {
        return inactiveFrom;
    }

    public void setInactiveFrom(LocalDate inactiveFrom) {
        this.inactiveFrom = inactiveFrom;
    }

    public LocalDate getInactiveTo() {
        return inactiveTo;
    }

    public void setInactiveTo(LocalDate inactiveTo) {
        this.inactiveTo = inactiveTo;
    }
    
    public String getInactivityReason() {
        return inactivityReason;
    }
    
    public void setInactivityReason(String inactivityReason) {
        this.inactivityReason = inactivityReason;
    }
    
    public Long getCurrentInactivityId() {
        return currentInactivityId;
    }
    
    public void setCurrentInactivityId(Long currentInactivityId) {
        this.currentInactivityId = currentInactivityId;
    }
    
    public Double getCurrentSalary() {
        return currentSalary;
    }
    
    public void setCurrentSalary(Double currentSalary) {
        this.currentSalary = currentSalary;
    }
    
    public Double getNetSalary() {
        return netSalary;
    }
    
    public void setNetSalary(Double netSalary) {
        this.netSalary = netSalary;
    }
    
    public boolean isOnLeave() {
        return onLeave;
    }
    
    public void setOnLeave(boolean onLeave) {
        this.onLeave = onLeave;
    }
    
    public Long getActiveLeaveId() {
        return activeLeaveId;
    }
    
    public void setActiveLeaveId(Long activeLeaveId) {
        this.activeLeaveId = activeLeaveId;
    }
    
    public LocalDate getLeaveEndDate() {
        return leaveEndDate;
    }
    
    public void setLeaveEndDate(LocalDate leaveEndDate) {
        this.leaveEndDate = leaveEndDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isHasMessages() {
        return hasMessages;
    }
    
    public void setHasMessages(boolean hasMessages) {
        this.hasMessages = hasMessages;
    }
    
    public int getPendingLeaveRequests() {
        return pendingLeaveRequests;
    }
    
    public void setPendingLeaveRequests(int pendingLeaveRequests) {
        this.pendingLeaveRequests = pendingLeaveRequests;
    }
    
    public List<SalaryDto> getSalaryHistory() {
        return salaryHistory;
    }
    
    public void setSalaryHistory(List<SalaryDto> salaryHistory) {
        this.salaryHistory = salaryHistory;
    }
    
    public void addSalary(SalaryDto salary) {
        if (this.salaryHistory == null) {
            this.salaryHistory = new ArrayList<>();
        }
        this.salaryHistory.add(salary);
    }
    
    public List<EmployeeInactivityDto> getInactivityHistory() {
        return inactivityHistory;
    }
    
    public void setInactivityHistory(List<EmployeeInactivityDto> inactivityHistory) {
        this.inactivityHistory = inactivityHistory;
    }
    
    public void addInactivity(EmployeeInactivityDto inactivity) {
        if (this.inactivityHistory == null) {
            this.inactivityHistory = new ArrayList<>();
        }
        this.inactivityHistory.add(inactivity);
    }
}
