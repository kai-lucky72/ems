package com.ems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.ems.model.Deduction.DeductionType;

public class DeductionDto {

    private Long id;

    @NotNull(message = "Deduction type is required")
    private DeductionType type;

    @NotBlank(message = "Deduction name is required")
    private String name;

    @NotNull(message = "Deduction value is required")
    @Positive(message = "Deduction value must be positive")
    private Double value;

    @NotNull(message = "Percentage indicator is required")
    private boolean isPercentage;

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
}
