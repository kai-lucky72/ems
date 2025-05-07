package com.ems.dto;

/**
 * DTO for employee analytics data
 */
public class EmployeeAnalytics {
    
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