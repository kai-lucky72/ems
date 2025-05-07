package com.ems.dto;

import java.util.List;

/**
 * DTO for distribution analytics data
 */
public class DistributionAnalytics {
    
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