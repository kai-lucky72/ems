package com.ems.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.config.JwtTokenUtil;
import com.ems.dto.AuthRequestDto;
import com.ems.dto.AuthResponseDto;
import com.ems.dto.UserDto;
import com.ems.model.User;
import com.ems.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody UserDto userDto) {
        User user = authService.registerUser(userDto);
        
        UserDetails userDetails = authService.loadUserByUsername(user.getEmail());
        String token = jwtTokenUtil.generateToken(userDetails);
        
        return ResponseEntity.ok(new AuthResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                token
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);
        
        User user = authService.getUserByEmail(userDetails.getUsername());
        
        return ResponseEntity.ok(new AuthResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                token
        ));
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        User user = authService.getCurrentUser();
        
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFullName(user.getFullName());
        userDto.setEmail(user.getEmail());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setCompanyName(user.getCompanyName());
        
        return ResponseEntity.ok(userDto);
    }
}
