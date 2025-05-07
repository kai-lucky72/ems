package com.ems.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "salaries")
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", unique = true, nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private Double grossSalary;

    @Column(nullable = false)
    private Double netSalary;

    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deduction> deductions = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        calculateNetSalary();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateNetSalary();
    }

    // Calculate net salary
    public void calculateNetSalary() {
        double totalDeductions = 0.0;
        for (Deduction deduction : deductions) {
            if (deduction.isPercentage()) {
                totalDeductions += grossSalary * (deduction.getValue() / 100.0);
            } else {
                totalDeductions += deduction.getValue();
            }
        }
        this.netSalary = grossSalary - totalDeductions;
        if (this.netSalary < 0) {
            this.netSalary = 0.0;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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

    public List<Deduction> getDeductions() {
        return deductions;
    }

    public void setDeductions(List<Deduction> deductions) {
        this.deductions = deductions;
    }

    public void addDeduction(Deduction deduction) {
        deduction.setSalary(this);
        this.deductions.add(deduction);
    }

    public void removeDeduction(Deduction deduction) {
        this.deductions.remove(deduction);
        deduction.setSalary(null);
    }

    public void clearDeductions() {
        for (Deduction deduction : new ArrayList<>(deductions)) {
            removeDeduction(deduction);
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
