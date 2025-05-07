package com.ems.dto;

import java.util.List;

/**
 * DTO for salary analytics data
 */
public class SalaryAnalytics {
    
    private double totalGross;
    private double totalNet;
    private double averageSalary;
    private List<DepartmentSalary> departmentSalaries;
    
    public double getTotalGross() {
        return totalGross;
    }
    
    public void setTotalGross(double totalGross) {
        this.totalGross = totalGross;
    }
    
    public double getTotalNet() {
        return totalNet;
    }
    
    public void setTotalNet(double totalNet) {
        this.totalNet = totalNet;
    }
    
    public double getAverageSalary() {
        return averageSalary;
    }
    
    public void setAverageSalary(double averageSalary) {
        this.averageSalary = averageSalary;
    }
    
    public List<DepartmentSalary> getDepartmentSalaries() {
        return departmentSalaries;
    }
    
    public void setDepartmentSalaries(List<DepartmentSalary> departmentSalaries) {
        this.departmentSalaries = departmentSalaries;
    }
    
    /**
     * Inner class for department salary data
     */
    public static class DepartmentSalary {
        private String department;
        private double totalSalary;
        
        public String getDepartment() {
            return department;
        }
        
        public void setDepartment(String department) {
            this.department = department;
        }
        
        public double getTotalSalary() {
            return totalSalary;
        }
        
        public void setTotalSalary(double totalSalary) {
            this.totalSalary = totalSalary;
        }
    }
}