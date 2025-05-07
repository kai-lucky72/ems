package com.ems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.ems.model.Department.BudgetType;

public class DepartmentDto {

    private Long id;

    @NotBlank(message = "Department name is required")
    @Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Budget is required")
    @Positive(message = "Budget must be a positive number")
    private Double budget;

    @NotNull(message = "Budget type is required")
    private BudgetType budgetType;

    // This field is calculated, not required in input
    private Double currentExpenses;

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

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public BudgetType getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(BudgetType budgetType) {
        this.budgetType = budgetType;
    }

    public Double getCurrentExpenses() {
        return currentExpenses;
    }

    public void setCurrentExpenses(Double currentExpenses) {
        this.currentExpenses = currentExpenses;
    }
}
