package com.ems.dto;

/**
 * DTO for leave analytics data
 */
public class LeaveAnalytics {
    
    private DistributionAnalytics statusDistribution;
    
    public DistributionAnalytics getStatusDistribution() {
        return statusDistribution;
    }
    
    public void setStatusDistribution(DistributionAnalytics statusDistribution) {
        this.statusDistribution = statusDistribution;
    }
}