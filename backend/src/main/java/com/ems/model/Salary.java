package com.ems.model;

import java.time.Year;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "salaries")
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "gross_salary", nullable = false)
    private Double grossSalary;

    @Column(name = "tax_deduction", nullable = false)
    private Double taxDeduction = 0.0;

    @Column(name = "insurance_deduction", nullable = false)
    private Double insuranceDeduction = 0.0;

    @Column(name = "other_deductions", nullable = false)
    private Double otherDeductions = 0.0;

    // Net salary will be calculated by the database as a stored generated column
    @Column(name = "net_salary", nullable = false)
    private Double netSalary;

    @Column(name = "salary_month", nullable = false)
    private Integer salaryMonth;

    @Column(name = "salary_year", nullable = false)
    private Integer salaryYear;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deduction> deductions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.salaryMonth == null) {
            this.salaryMonth = Month.valueOf(LocalDateTime.now().getMonth().name()).getValue();
        }
        if (this.salaryYear == null) {
            this.salaryYear = Year.now().getValue();
        }
        calculateNetSalary();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateNetSalary();
    }

    // Calculate net salary
    public void calculateNetSalary() {
        // Calculate other deductions from custom deductions
        this.otherDeductions = 0.0;
        for (Deduction deduction : deductions) {
            if (deduction.getType() == Deduction.DeductionType.TAX) {
                if (deduction.isPercentage()) {
                    this.taxDeduction = grossSalary * (deduction.getValue() / 100.0);
                } else {
                    this.taxDeduction = deduction.getValue();
                }
            } else if (deduction.getType() == Deduction.DeductionType.INSURANCE) {
                if (deduction.isPercentage()) {
                    this.insuranceDeduction = grossSalary * (deduction.getValue() / 100.0);
                } else {
                    this.insuranceDeduction = deduction.getValue();
                }
            } else if (deduction.getType() == Deduction.DeductionType.CUSTOM) {
                if (deduction.isPercentage()) {
                    this.otherDeductions += grossSalary * (deduction.getValue() / 100.0);
                } else {
                    this.otherDeductions += deduction.getValue();
                }
            }
        }
        
        this.netSalary = grossSalary - taxDeduction - insuranceDeduction - otherDeductions;
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

    public Double getTaxDeduction() {
        return taxDeduction;
    }

    public void setTaxDeduction(Double taxDeduction) {
        this.taxDeduction = taxDeduction;
    }

    public Double getInsuranceDeduction() {
        return insuranceDeduction;
    }

    public void setInsuranceDeduction(Double insuranceDeduction) {
        this.insuranceDeduction = insuranceDeduction;
    }

    public Double getOtherDeductions() {
        return otherDeductions;
    }

    public void setOtherDeductions(Double otherDeductions) {
        this.otherDeductions = otherDeductions;
    }

    public Double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(Double netSalary) {
        this.netSalary = netSalary;
    }

    public Integer getSalaryMonth() {
        return salaryMonth;
    }

    public void setSalaryMonth(Integer salaryMonth) {
        this.salaryMonth = salaryMonth;
    }

    public Integer getSalaryYear() {
        return salaryYear;
    }

    public void setSalaryYear(Integer salaryYear) {
        this.salaryYear = salaryYear;
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
}
