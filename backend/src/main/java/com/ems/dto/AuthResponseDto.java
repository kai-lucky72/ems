package com.ems.dto;

public class AuthResponseDto {

    private Long id;
    private String fullName;
    private String email;
    private String token;

    public AuthResponseDto() {
    }

    public AuthResponseDto(Long id, String fullName, String email, String token) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.token = token;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
