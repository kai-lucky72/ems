package com.ems.dto;

import java.util.List;

public class AnalyticsDto {

    private DepartmentBudgetAnalytics departmentBudget;
    private SalaryAnalytics salaryData;
    private DistributionAnalytics employeeDistribution;
    private DistributionAnalytics leaveStatus;
    private DistributionAnalytics roleDistribution;
    private DistributionAnalytics contractTypeDistribution;
    private EmployeeTimelineAnalytics employeeTimeline;

    // Department Budget Analytics
    public static class DepartmentBudgetAnalytics {
        private List<String> labels;
        private List<Double> actual;
        private List<Double> budget;

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        public List<Double> getActual() {
            return actual;
        }

        public void setActual(List<Double> actual) {
            this.actual = actual;
        }

        public List<Double> getBudget() {
            return budget;
        }

        public void setBudget(List<Double> budget) {
            this.budget = budget;
        }
    }

    // Salary Analytics
    public static class SalaryAnalytics {
        private double totalGross;
        private double totalNet;
        private double averageSalary;
        private List<DepartmentSalary> departmentSalaries;

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
    }

    // Distribution Analytics
    public static class DistributionAnalytics {
        private List<String> labels;
        private List<Integer> counts;

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        public List<Integer> getCounts() {
            return counts;
        }

        public void setCounts(List<Integer> counts) {
            this.counts = counts;
        }
    }

    // Employee Analytics
    public static class EmployeeAnalytics {
        private DistributionAnalytics statusDistribution;
        private DistributionAnalytics roleDistribution;
        private DistributionAnalytics contractTypeDistribution;

        public DistributionAnalytics getStatusDistribution() {
            return statusDistribution;
        }

        public void setStatusDistribution(DistributionAnalytics statusDistribution) {
            this.statusDistribution = statusDistribution;
        }

        public DistributionAnalytics getRoleDistribution() {
            return roleDistribution;
        }

        public void setRoleDistribution(DistributionAnalytics roleDistribution) {
            this.roleDistribution = roleDistribution;
        }

        public DistributionAnalytics getContractTypeDistribution() {
            return contractTypeDistribution;
        }

        public void setContractTypeDistribution(DistributionAnalytics contractTypeDistribution) {
            this.contractTypeDistribution = contractTypeDistribution;
        }
    }

    // Leave Analytics
    public static class LeaveAnalytics {
        private DistributionAnalytics statusDistribution;

        public DistributionAnalytics getStatusDistribution() {
            return statusDistribution;
        }

        public void setStatusDistribution(DistributionAnalytics statusDistribution) {
            this.statusDistribution = statusDistribution;
        }
    }

    // Employee Timeline Analytics
    public static class EmployeeTimelineAnalytics {
        private List<String> months;
        private List<Integer> active;
        private List<Integer> inactive;

        public List<String> getMonths() {
            return months;
        }

        public void setMonths(List<String> months) {
            this.months = months;
        }

        public List<Integer> getActive() {
            return active;
        }

        public void setActive(List<Integer> active) {
            this.active = active;
        }

        public List<Integer> getInactive() {
            return inactive;
        }

        public void setInactive(List<Integer> inactive) {
            this.inactive = inactive;
        }
    }

    // Getters and Setters
    public DepartmentBudgetAnalytics getDepartmentBudget() {
        return departmentBudget;
    }

    public void setDepartmentBudget(DepartmentBudgetAnalytics departmentBudget) {
        this.departmentBudget = departmentBudget;
    }

    public SalaryAnalytics getSalaryData() {
        return salaryData;
    }

    public void setSalaryData(SalaryAnalytics salaryData) {
        this.salaryData = salaryData;
    }

    public DistributionAnalytics getEmployeeDistribution() {
        return employeeDistribution;
    }

    public void setEmployeeDistribution(DistributionAnalytics employeeDistribution) {
        this.employeeDistribution = employeeDistribution;
    }

    public DistributionAnalytics getLeaveStatus() {
        return leaveStatus;
    }

    public void setLeaveStatus(DistributionAnalytics leaveStatus) {
        this.leaveStatus = leaveStatus;
    }

    public DistributionAnalytics getRoleDistribution() {
        return roleDistribution;
    }

    public void setRoleDistribution(DistributionAnalytics roleDistribution) {
        this.roleDistribution = roleDistribution;
    }

    public DistributionAnalytics getContractTypeDistribution() {
        return contractTypeDistribution;
    }

    public void setContractTypeDistribution(DistributionAnalytics contractTypeDistribution) {
        this.contractTypeDistribution = contractTypeDistribution;
    }

    public EmployeeTimelineAnalytics getEmployeeTimeline() {
        return employeeTimeline;
    }

    public void setEmployeeTimeline(EmployeeTimelineAnalytics employeeTimeline) {
        this.employeeTimeline = employeeTimeline;
    }
}
