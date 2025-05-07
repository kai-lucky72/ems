package com.ems.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SalaryDto {

    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    // This field is calculated, not required in input
    private String employeeName;

    // This field is calculated, not required in input
    private String departmentName;

    @NotNull(message = "Gross salary is required")
    @Positive(message = "Gross salary must be positive")
    private Double grossSalary;

    // This field is calculated, not required in input
    private Double netSalary;

    @Valid
    private List<DeductionDto> deductions = new ArrayList<>();

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

    public List<DeductionDto> getDeductions() {
        return deductions;
    }

    public void setDeductions(List<DeductionDto> deductions) {
        this.deductions = deductions;
    }
}
