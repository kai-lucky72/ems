package com.ems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.ems.model.Employee.ContractType;
import java.time.LocalDate;

public class EmployeeDto {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phone;

    @NotBlank(message = "Role is required")
    @Size(min = 2, max = 100, message = "Role must be between 2 and 100 characters")
    private String role;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    // This field is calculated, not required in input
    private String departmentName;

    @NotNull(message = "Contract type is required")
    private ContractType contractType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private boolean isActive = true;

    private LocalDate inactiveFrom;

    private LocalDate inactiveTo;

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
}
