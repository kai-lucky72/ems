package com.ems.dto;

import java.util.List;

/**
 * DTO for department budget analytics data
 */
public class DepartmentBudgetAnalytics {
    
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