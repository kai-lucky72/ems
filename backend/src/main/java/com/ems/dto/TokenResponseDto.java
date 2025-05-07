package com.ems.dto;

/**
 * DTO for authentication token responses
 */
public class TokenResponseDto {
    
    private String token;
    private String role;
    
    // Default constructor
    public TokenResponseDto() {
    }
    
    // Constructor with parameters
    public TokenResponseDto(String token, String role) {
        this.token = token;
        this.role = role;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}