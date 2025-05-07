package com.ems.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.ems.service.AuthService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class JwtSecurityConfig {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    
    private static final String[] PUBLIC_URLS = {
        "/api/auth/**",
        "/api/activate/**",
        "/api/reset-password/**",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html"
    };
    
    public JwtSecurityConfig(JwtTokenProvider jwtTokenProvider, AuthService authService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
    }
    
    @Bean(name = "jwtFilterChain")
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(authService).passwordEncoder(jwtPasswordEncoder());
        
        // Get AuthenticationManager
        AuthenticationManager authenticationManager = authManagerBuilder.build();
        
        http
            .cors().and()
            .csrf().disable()
            .authenticationManager(authenticationManager)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers("/api/manager/**").hasRole("MANAGER")
                .requestMatchers("/api/employee/**").hasRole("EMPLOYEE")
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean(name = "jwtPasswordEncoder")
    public PasswordEncoder jwtPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean(name = "jwtAuthManager")
    public AuthenticationManager jwtAuthManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(authService)
                .passwordEncoder(jwtPasswordEncoder())
                .and()
                .build();
    }
    
    @Bean(name = "jwtCorsFilter")
    public CorsFilter jwtCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}