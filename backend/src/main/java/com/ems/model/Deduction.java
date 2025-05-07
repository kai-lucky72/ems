package com.ems.model;

import jakarta.persistence.*;

@Entity
@Table(name = "salary_deductions")
public class Deduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "deduction_type", nullable = false)
    private DeductionType type;

    @Column(nullable = false)
    private String name;

    @Column(name = "deduction_value", nullable = false)
    private Double value;

    @Column(name = "is_percentage", nullable = false)
    private boolean isPercentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_id", nullable = false)
    private Salary salary;

    public enum DeductionType {
        TAX, INSURANCE, CUSTOM;
        
        // Convert from database values
        public static DeductionType fromString(String str) {
            if (str == null) return null;
            
            return switch (str.toUpperCase()) {
                case "TAX" -> TAX;
                case "INSURANCE" -> INSURANCE;
                case "CUSTOM" -> CUSTOM;
                default -> throw new IllegalArgumentException("Unknown deduction type: " + str);
            };
        }
        
        // Convert to database values
        public String toDatabaseValue() {
            return name().toLowerCase();
        }
    }
    
    // Default constructor for JPA
    public Deduction() {
    }
    
    // Constructor for creating a new deduction
    public Deduction(DeductionType type, String name, Double value, boolean isPercentage) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.isPercentage = isPercentage;
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

    public Salary getSalary() {
        return salary;
    }

    public void setSalary(Salary salary) {
        this.salary = salary;
    }
    
    // Calculate actual deduction amount
    public Double calculateAmount(Double grossSalary) {
        if (isPercentage) {
            return grossSalary * (value / 100.0);
        } else {
            return value;
        }
    }
    
    // Create a copy of this deduction
    public Deduction copy() {
        Deduction copy = new Deduction();
        copy.type = this.type;
        copy.name = this.name;
        copy.value = this.value;
        copy.isPercentage = this.isPercentage;
        return copy;
    }
}
