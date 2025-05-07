package com.ems.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.ems.model.Department.BudgetType;

/**
 * Data Transfer Object for Department information
 */
@JsonInclude(Include.NON_NULL)
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

    // These fields are calculated, not required in input
    private Double currentExpenses;
    private Integer employeeCount;
    private Integer activeEmployeeCount;
    private Double budgetUsagePercentage;
    private Boolean isBudgetOverrun;
    private Double averageSalary;
    
    // Advanced statistics for analytics
    private List<EmployeeDto> employees;
    private List<SalaryTrendDto> salaryTrends;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // Constructors
    public DepartmentDto() {
    }
    
    // Copy constructor
    public DepartmentDto(DepartmentDto source) {
        this.id = source.id;
        this.name = source.name;
        this.budget = source.budget;
        this.budgetType = source.budgetType;
        this.currentExpenses = source.currentExpenses;
        this.employeeCount = source.employeeCount;
        this.activeEmployeeCount = source.activeEmployeeCount;
        this.budgetUsagePercentage = source.budgetUsagePercentage;
        this.isBudgetOverrun = source.isBudgetOverrun;
        this.averageSalary = source.averageSalary;
        this.createdAt = source.createdAt;
        
        if (source.employees != null) {
            this.employees = new ArrayList<>(source.employees);
        }
        
        if (source.salaryTrends != null) {
            this.salaryTrends = new ArrayList<>(source.salaryTrends);
        }
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
    
    public Integer getEmployeeCount() {
        return employeeCount;
    }
    
    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }
    
    public Integer getActiveEmployeeCount() {
        return activeEmployeeCount;
    }
    
    public void setActiveEmployeeCount(Integer activeEmployeeCount) {
        this.activeEmployeeCount = activeEmployeeCount;
    }
    
    public Double getBudgetUsagePercentage() {
        return budgetUsagePercentage;
    }
    
    public void setBudgetUsagePercentage(Double budgetUsagePercentage) {
        this.budgetUsagePercentage = budgetUsagePercentage;
    }
    
    public Boolean getIsBudgetOverrun() {
        return isBudgetOverrun;
    }
    
    public void setIsBudgetOverrun(Boolean isBudgetOverrun) {
        this.isBudgetOverrun = isBudgetOverrun;
    }
    
    public Double getAverageSalary() {
        return averageSalary;
    }
    
    public void setAverageSalary(Double averageSalary) {
        this.averageSalary = averageSalary;
    }
    
    public List<EmployeeDto> getEmployees() {
        return employees;
    }
    
    public void setEmployees(List<EmployeeDto> employees) {
        this.employees = employees;
    }
    
    public List<SalaryTrendDto> getSalaryTrends() {
        return salaryTrends;
    }
    
    public void setSalaryTrends(List<SalaryTrendDto> salaryTrends) {
        this.salaryTrends = salaryTrends;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public String getFormattedCreatedAt() {
        if (createdAt == null) {
            return null;
        }
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    public void calculateBudgetUsage() {
        if (budget != null && currentExpenses != null && budget > 0) {
            budgetUsagePercentage = (currentExpenses / budget) * 100;
            isBudgetOverrun = budgetUsagePercentage > 100;
        } else {
            budgetUsagePercentage = 0.0;
            isBudgetOverrun = false;
        }
    }
    
    public String getBudgetTypeDisplay() {
        if (budgetType == null) {
            return "";
        }
        
        return switch (budgetType) {
            case MONTHLY -> "Monthly";
            case YEARLY -> "Yearly";
        };
    }
    
    public String getBudgetWithType() {
        if (budget == null) {
            return "";
        }
        
        String budgetStr = String.format("$%.2f", budget);
        
        if (budgetType == null) {
            return budgetStr;
        }
        
        return switch (budgetType) {
            case MONTHLY -> budgetStr + " / month";
            case YEARLY -> budgetStr + " / year";
        };
    }
    
    public String getBudgetStatus() {
        if (budgetUsagePercentage == null) {
            calculateBudgetUsage();
        }
        
        if (isBudgetOverrun) {
            return "Over Budget";
        } else if (budgetUsagePercentage >= 90) {
            return "Near Limit";
        } else if (budgetUsagePercentage >= 75) {
            return "High Usage";
        } else if (budgetUsagePercentage >= 50) {
            return "Moderate";
        } else {
            return "Good";
        }
    }
    
    public String getBudgetStatusCssClass() {
        if (budgetUsagePercentage == null) {
            calculateBudgetUsage();
        }
        
        if (isBudgetOverrun) {
            return "danger";
        } else if (budgetUsagePercentage >= 90) {
            return "warning";
        } else if (budgetUsagePercentage >= 75) {
            return "attention";
        } else if (budgetUsagePercentage >= 50) {
            return "normal";
        } else {
            return "success";
        }
    }
    
    /**
     * Nested class for salary trend data within a department
     */
    @JsonInclude(Include.NON_NULL)
    public static class SalaryTrendDto {
        private Integer year;
        private Integer month;
        private String periodLabel;
        private Double totalSalary;
        private Integer employeeCount;
        
        public SalaryTrendDto() {
        }
        
        public SalaryTrendDto(Integer year, Integer month, Double totalSalary, Integer employeeCount) {
            this.year = year;
            this.month = month;
            this.totalSalary = totalSalary;
            this.employeeCount = employeeCount;
            
            // Create period label (e.g., "Jan 2023")
            String monthName = switch (month) {
                case 1 -> "Jan";
                case 2 -> "Feb";
                case 3 -> "Mar";
                case 4 -> "Apr";
                case 5 -> "May";
                case 6 -> "Jun";
                case 7 -> "Jul";
                case 8 -> "Aug";
                case 9 -> "Sep";
                case 10 -> "Oct";
                case 11 -> "Nov";
                case 12 -> "Dec";
                default -> "";
            };
            
            this.periodLabel = monthName + " " + year;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public String getPeriodLabel() {
            return periodLabel;
        }

        public void setPeriodLabel(String periodLabel) {
            this.periodLabel = periodLabel;
        }

        public Double getTotalSalary() {
            return totalSalary;
        }

        public void setTotalSalary(Double totalSalary) {
            this.totalSalary = totalSalary;
        }

        public Integer getEmployeeCount() {
            return employeeCount;
        }

        public void setEmployeeCount(Integer employeeCount) {
            this.employeeCount = employeeCount;
        }
        
        public Double getAverageSalary() {
            if (totalSalary == null || employeeCount == null || employeeCount == 0) {
                return 0.0;
            }
            return totalSalary / employeeCount;
        }
    }
}
