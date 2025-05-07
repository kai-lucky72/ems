package com.ems.model;

import jakarta.persistence.*;

@Entity
@Table(name = "deductions")
public class Deduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeductionType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private boolean isPercentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_id", nullable = false)
    private Salary salary;

    public enum DeductionType {
        TAX, INSURANCE, CUSTOM
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
}
