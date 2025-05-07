package com.ems.dto;

import java.util.List;

/**
 * DTO for employee timeline analytics data
 */
public class EmployeeTimelineAnalytics {
    
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