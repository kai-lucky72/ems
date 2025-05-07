package com.ems.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "budget_amount", nullable = false)
    private Double budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "budget_type", nullable = false)
    private BudgetType budgetType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();

    public enum BudgetType {
        MONTHLY, YEARLY;
        
        // Convert from database values
        public static BudgetType fromString(String str) {
            if (str == null) return null;
            
            return switch (str.toLowerCase()) {
                case "monthly" -> MONTHLY;
                case "yearly" -> YEARLY;
                default -> throw new IllegalArgumentException("Unknown budget type: " + str);
            };
        }
        
        // Convert to database values
        public String toDatabaseValue() {
            return switch (this) {
                case MONTHLY -> "monthly";
                case YEARLY -> "yearly";
            };
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    // Calculate current expenses
    public Double calculateCurrentExpenses() {
        double totalExpenses = 0.0;
        for (Employee employee : employees) {
            Salary salary = employee.getSalary();
            if (salary != null && employee.isActive()) {
                totalExpenses += salary.getGrossSalary();
            }
        }
        return totalExpenses;
    }
    
    // Calculate budget usage percentage
    public Double calculateBudgetUsagePercentage() {
        Double expenses = calculateCurrentExpenses();
        if (budget == null || budget == 0) {
            return 100.0; // Avoid division by zero
        }
        return (expenses / budget) * 100;
    }
    
    // Check if budget is overrun
    public boolean isBudgetOverrun() {
        return calculateBudgetUsagePercentage() > 100;
    }
}
