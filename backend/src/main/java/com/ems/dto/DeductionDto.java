package com.ems.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.ems.model.Deduction.DeductionType;

@JsonInclude(Include.NON_NULL)
public class DeductionDto {

    private Long id;

    @NotNull(message = "Deduction type is required")
    private DeductionType type;

    @NotBlank(message = "Deduction name is required")
    @Size(max = 100, message = "Deduction name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Deduction value is required")
    @Positive(message = "Deduction value must be positive")
    private Double value;

    @NotNull(message = "Percentage indicator is required")
    private boolean isPercentage;
    
    // Additional fields for UI display
    private String displayValue;
    private Double calculatedAmount;
    private Long salaryId;

    // Constructors
    public DeductionDto() {
    }
    
    // Constructor with essential fields
    public DeductionDto(DeductionType type, String name, Double value, boolean isPercentage) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.isPercentage = isPercentage;
    }
    
    // Copy constructor
    public DeductionDto(DeductionDto source) {
        this.id = source.id;
        this.type = source.type;
        this.name = source.name;
        this.value = source.value;
        this.isPercentage = source.isPercentage;
        this.displayValue = source.displayValue;
        this.calculatedAmount = source.calculatedAmount;
        this.salaryId = source.salaryId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeductionType getType() {
        return type;
    }

    public void setType(DeductionType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public void setPercentage(boolean isPercentage) {
        this.isPercentage = isPercentage;
    }
    
    public String getDisplayValue() {
        if (displayValue != null) {
            return displayValue;
        }
        
        if (isPercentage) {
            return value + "%";
        } else {
            return "$" + String.format("%.2f", value);
        }
    }
    
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public Double getCalculatedAmount() {
        return calculatedAmount;
    }
    
    public void setCalculatedAmount(Double calculatedAmount) {
        this.calculatedAmount = calculatedAmount;
    }
    
    public Long getSalaryId() {
        return salaryId;
    }
    
    public void setSalaryId(Long salaryId) {
        this.salaryId = salaryId;
    }
    
    // Calculate the actual deduction amount based on gross salary
    public Double calculateAmount(Double grossSalary) {
        if (grossSalary == null) {
            return 0.0;
        }
        
        if (isPercentage) {
            return grossSalary * (value / 100.0);
        } else {
            return value;
        }
    }
    
    // Update calculated amount based on gross salary
    public void updateCalculatedAmount(Double grossSalary) {
        this.calculatedAmount = calculateAmount(grossSalary);
    }
    
    // Get display type as human readable string
    public String getDisplayType() {
        if (type == null) {
            return "";
        }
        
        return switch (type) {
            case TAX -> "Tax";
            case INSURANCE -> "Insurance";
            case CUSTOM -> "Other";
        };
    }
}
