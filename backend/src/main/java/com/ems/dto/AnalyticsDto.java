package com.ems.dto;

/**
 * DTO for comprehensive analytics data
 */
public class AnalyticsDto {

    private DepartmentBudgetAnalytics departmentBudget;
    private SalaryAnalytics salaryData;
    private DistributionAnalytics employeeDistribution;
    private DistributionAnalytics leaveStatus;
    private DistributionAnalytics roleDistribution;
    private DistributionAnalytics contractTypeDistribution;
    private EmployeeTimelineAnalytics employeeTimeline;

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
