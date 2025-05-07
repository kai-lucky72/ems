package com.ems.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonInclude(Include.NON_NULL)
public class SalaryDto {

    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    // These fields are calculated, not required in input
    private String employeeName;
    private String departmentName;
    private String employeeEmail;
    private String employeeRole;

    @NotNull(message = "Gross salary is required")
    @Positive(message = "Gross salary must be positive")
    private Double grossSalary;

    // These fields are calculated from deductions, not required in input
    private Double netSalary;
    private Double taxDeduction;
    private Double insuranceDeduction;
    private Double otherDeductions;
    
    // Month and year for the salary
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer salaryMonth;
    
    @Min(value = 2000, message = "Year must be valid (2000 or later)")
    @Max(value = 2100, message = "Year must be valid (before 2100)")
    private Integer salaryYear;
    
    private LocalDateTime createdAt;
    
    private Boolean isCurrent;

    @Valid
    private List<DeductionDto> deductions = new ArrayList<>();

    // Constructors
    public SalaryDto() {
    }
    
    // Copy constructor
    public SalaryDto(SalaryDto source) {
        this.id = source.id;
        this.employeeId = source.employeeId;
        this.employeeName = source.employeeName;
        this.departmentName = source.departmentName;
        this.employeeEmail = source.employeeEmail;
        this.employeeRole = source.employeeRole;
        this.grossSalary = source.grossSalary;
        this.netSalary = source.netSalary;
        this.taxDeduction = source.taxDeduction;
        this.insuranceDeduction = source.insuranceDeduction;
        this.otherDeductions = source.otherDeductions;
        this.salaryMonth = source.salaryMonth;
        this.salaryYear = source.salaryYear;
        this.createdAt = source.createdAt;
        this.isCurrent = source.isCurrent;
        
        if (source.deductions != null) {
            this.deductions = new ArrayList<>(source.deductions);
        }
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public String getEmployeeEmail() {
        return employeeEmail;
    }
    
    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }
    
    public String getEmployeeRole() {
        return employeeRole;
    }
    
    public void setEmployeeRole(String employeeRole) {
        this.employeeRole = employeeRole;
    }

    public Double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(Double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public Double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(Double netSalary) {
        this.netSalary = netSalary;
    }
    
    public Double getTaxDeduction() {
        return taxDeduction;
    }
    
    public void setTaxDeduction(Double taxDeduction) {
        this.taxDeduction = taxDeduction;
    }
    
    public Double getInsuranceDeduction() {
        return insuranceDeduction;
    }
    
    public void setInsuranceDeduction(Double insuranceDeduction) {
        this.insuranceDeduction = insuranceDeduction;
    }
    
    public Double getOtherDeductions() {
        return otherDeductions;
    }
    
    public void setOtherDeductions(Double otherDeductions) {
        this.otherDeductions = otherDeductions;
    }
    
    public Integer getSalaryMonth() {
        return salaryMonth;
    }
    
    public void setSalaryMonth(Integer salaryMonth) {
        this.salaryMonth = salaryMonth;
    }
    
    public Integer getSalaryYear() {
        return salaryYear;
    }
    
    public void setSalaryYear(Integer salaryYear) {
        this.salaryYear = salaryYear;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getIsCurrent() {
        return isCurrent;
    }
    
    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public List<DeductionDto> getDeductions() {
        return deductions;
    }

    public void setDeductions(List<DeductionDto> deductions) {
        this.deductions = deductions;
    }
    
    public void addDeduction(DeductionDto deduction) {
        if (this.deductions == null) {
            this.deductions = new ArrayList<>();
        }
        this.deductions.add(deduction);
    }
    
    // Calculate net salary from deductions
    public void calculateNetSalary() {
        if (grossSalary == null) {
            return;
        }
        
        double totalDeductions = 0.0;
        taxDeduction = 0.0;
        insuranceDeduction = 0.0;
        otherDeductions = 0.0;
        
        for (DeductionDto deduction : deductions) {
            double amount;
            if (deduction.isPercentage()) {
                amount = grossSalary * (deduction.getValue() / 100.0);
            } else {
                amount = deduction.getValue();
            }
            
            switch (deduction.getType()) {
                case TAX:
                    taxDeduction += amount;
                    break;
                case INSURANCE:
                    insuranceDeduction += amount;
                    break;
                case CUSTOM:
                    otherDeductions += amount;
                    break;
            }
            
            totalDeductions += amount;
        }
        
        netSalary = grossSalary - totalDeductions;
        if (netSalary < 0) {
            netSalary = 0.0;
        }
    }
    
    // Get formatted month year string (e.g., "January 2023")
    public String getFormattedMonthYear() {
        if (salaryMonth == null || salaryYear == null) {
            return null;
        }
        
        String month = switch (salaryMonth) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "Unknown";
        };
        
        return month + " " + salaryYear;
    }
}
