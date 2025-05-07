package com.ems.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ems.dto.LoginRequestDto;
import com.ems.dto.TokenResponseDto;
import com.ems.dto.UserDto;
import com.ems.exception.AuthenticationException;
import com.ems.model.User;
import com.ems.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    /**
     * Register a new user (manager/account owner)
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto userDto) {
        User user = authService.registerUser(userDto);
        
        UserDto responseDto = new UserDto();
        responseDto.setId(user.getId());
        responseDto.setFullName(user.getFullName());
        responseDto.setEmail(user.getEmail());
        responseDto.setPhoneNumber(user.getPhoneNumber());
        responseDto.setCompanyName(user.getCompanyName());
        
        return ResponseEntity.ok(responseDto);
    }
    
    /**
     * Login user or employee
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            TokenResponseDto tokenResponse = authService.login(loginRequest);
            return ResponseEntity.ok(tokenResponse);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(null);
        }
    }
    
    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile() {
        try {
            User user = authService.getCurrentUser();
            
            UserDto responseDto = new UserDto();
            responseDto.setId(user.getId());
            responseDto.setFullName(user.getFullName());
            responseDto.setEmail(user.getEmail());
            responseDto.setPhoneNumber(user.getPhoneNumber());
            responseDto.setCompanyName(user.getCompanyName());
            
            return ResponseEntity.ok(responseDto);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(null);
        }
    }
    
    /**
     * Activate employee account with token
     */
    @PostMapping("/activate")
    public ResponseEntity<String> activateEmployeeAccount(
            @RequestParam String token, 
            @RequestParam String password) {
        authService.activateEmployeeAccount(token, password);
        return ResponseEntity.ok("Account activated successfully");
    }
    
    /**
     * Request password reset token
     */
    @PostMapping("/reset-password-request")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        String token = authService.generatePasswordResetToken(email);
        return ResponseEntity.ok("Password reset token generated");
    }
    
    /**
     * Reset password with token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token, 
            @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successful");
    }
    
    /**
     * Check if user is authenticated
     */
    @GetMapping("/check")
    public ResponseEntity<String> checkAuthentication() {
        if (authService.isManager()) {
            return ResponseEntity.ok("ROLE_MANAGER");
        } else if (authService.isEmployee()) {
            return ResponseEntity.ok("ROLE_EMPLOYEE");
        } else {
            return ResponseEntity.status(401).body("Not authenticated");
        }
    }
}