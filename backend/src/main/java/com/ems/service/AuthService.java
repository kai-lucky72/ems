package com.ems.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.LoginRequestDto;
import com.ems.dto.TokenResponseDto;
import com.ems.dto.UserDto;
import com.ems.exception.BadRequestException;
import com.ems.exception.ResourceNotFoundException;

// Use custom authentication exception to avoid ambiguity with Spring Security's version
import com.ems.exception.AuthenticationException;
import com.ems.model.Employee;
import com.ems.model.User;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.UserRepository;
import com.ems.security.JwtTokenProvider;

@Service
public class AuthService implements UserDetailsService {

    private static final String ROLE_MANAGER = "ROLE_MANAGER";
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    
    @Value("${app.token.expiration.hours:24}")
    private int tokenExpirationHours;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Utility method to handle List<Boolean> return types from repository methods
     * @param booleanList The list returned from repository
     * @return true if the list contains at least one true value, false otherwise
     */
    private boolean getBooleanResult(List<Boolean> booleanList) {
        return booleanList != null && !booleanList.isEmpty() && booleanList.get(0);
    }
    
    /**
     * Utility method to get the first user from a list or throw exception if not found
     * @param users The list of users
     * @param errorMessage The error message if user not found
     * @return The first user in the list
     * @throws UsernameNotFoundException if list is empty
     */
    private User getUserFromList(List<User> users, String errorMessage) {
        if (users == null || users.isEmpty()) {
            throw new UsernameNotFoundException(errorMessage);
        }
        return users.get(0);
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // First check if it's a manager (User)
        if (getBooleanResult(userRepository.existsByEmail(email))) {
            User user = getUserFromList(userRepository.findByEmail(email), 
                    "User not found with email: " + email);
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(ROLE_MANAGER));
            
            for (String role : user.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
            
            return new org.springframework.security.core.userdetails.User(
                    "M_" + user.getEmail(), // Prefix with 'M_' to distinguish from employees
                    user.getPassword(),
                    user.isActive(),
                    true, true, true,
                    authorities
            );
        } 
        // Then check if it's an employee
        else if (getBooleanResult(employeeRepository.existsByEmail(email))) {
            Employee employee = getEmployeeFromList(employeeRepository.findByEmail(email), 
                    "Employee not found with email: " + email);
            
            // Check if employee account is activated
            if (!employee.isAccountActivated()) {
                throw new AuthenticationException("Employee account not activated");
            }
            
            // Check if employee is active
            if (!employee.isActive()) {
                throw new AuthenticationException("Employee account is inactive");
            }
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(ROLE_EMPLOYEE));
            
            for (String role : employee.getAuthRoles()) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
            
            return new org.springframework.security.core.userdetails.User(
                    "E_" + employee.getEmail(), // Prefix with 'E_' to distinguish from managers
                    employee.getPasswordHash(),
                    employee.isActive() && employee.isAccountActivated(),
                    true, true, true,
                    authorities
            );
        }
        
        throw new UsernameNotFoundException("No user or employee found with email: " + email);
    }

    @Transactional
    public User registerUser(UserDto userDto) {
        // Check if email is already used by a manager
        if (getBooleanResult(userRepository.existsByEmail(userDto.getEmail()))) {
            throw new BadRequestException("Email is already registered as a manager");
        }
        
        // Check if email is already used by an employee
        if (getBooleanResult(employeeRepository.existsByEmail(userDto.getEmail()))) {
            throw new BadRequestException("Email is already registered as an employee");
        }

        User user = new User();
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setCompanyName(userDto.getCompanyName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.addRole(ROLE_MANAGER); // Add manager role

        return userRepository.save(user);
    }
    
    @Transactional
    public TokenResponseDto login(LoginRequestDto loginRequest) throws AuthenticationException {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        String prefix = null;
        String role = null;
        
        try {
            // Try to authenticate as Manager
            if (getBooleanResult(userRepository.existsByEmail(email))) {
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken("M_" + email, password, Collections.emptyList())
                );
                
                User user = getUserFromList(userRepository.findByEmail(email), 
                        "User not found");
                
                // Update last login time
                user.updateLastLogin();
                userRepository.save(user);
                
                prefix = "M_";
                role = ROLE_MANAGER;
            } 
            // Try to authenticate as Employee
            else if (getBooleanResult(employeeRepository.existsByEmail(email))) {
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken("E_" + email, password, Collections.emptyList())
                );
                
                Employee employee = getEmployeeFromList(employeeRepository.findByEmail(email),
                        "Employee not found");
                
                // Update last login time
                employee.updateLastLogin();
                employeeRepository.save(employee);
                
                prefix = "E_";
                role = ROLE_EMPLOYEE;
            } else {
                throw new UsernameNotFoundException("No account found with email: " + email);
            }
            
            // Generate token
            String token = jwtTokenProvider.createToken(prefix + email, role);
            
            return new TokenResponseDto(token, role);
            
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new com.ems.exception.AuthenticationException("Invalid email/password");
        }
    }
    
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return getUserFromList(userRepository.findByEmail(email),
                "User not found with email: " + email);
    }
    
    /**
     * Utility method to get the first employee from a list or throw exception if not found
     * @param employees The list of employees
     * @param errorMessage The error message if employee not found
     * @return The first employee in the list
     * @throws UsernameNotFoundException if list is empty
     */
    private Employee getEmployeeFromList(List<Employee> employees, String errorMessage) {
        if (employees == null || employees.isEmpty()) {
            throw new UsernameNotFoundException(errorMessage);
        }
        return employees.get(0);
    }
    
    @Transactional(readOnly = true)
    public Employee getEmployeeByEmail(String email) {
        return getEmployeeFromList(employeeRepository.findByEmail(email),
                "Employee not found with email: " + email);
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Not authenticated");
        }
        
        String principal = authentication.getName();
        
        // Check if this is a manager (User)
        if (principal.startsWith("M_")) {
            String email = principal.substring(2); // Remove the "M_" prefix
            return getUserByEmail(email);
        }
        
        throw new AuthenticationException("Current authentication is not for a manager");
    }
    
    @Transactional(readOnly = true)
    public Employee getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Not authenticated");
        }
        
        String principal = authentication.getName();
        
        // Check if this is an employee
        if (principal.startsWith("E_")) {
            String email = principal.substring(2); // Remove the "E_" prefix
            return getEmployeeByEmail(email);
        }
        
        throw new AuthenticationException("Current authentication is not for an employee");
    }
    
    @Transactional(readOnly = true)
    public boolean isManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ROLE_MANAGER));
    }
    
    @Transactional(readOnly = true)
    public boolean isEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ROLE_EMPLOYEE));
    }
    
    @Transactional
    public String generateEmployeeActivationToken(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        // Generate a random token
        String token = UUID.randomUUID().toString();
        
        // Set expiry time (24 hours from now)
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(tokenExpirationHours);
        
        // Save the token and expiry to the employee
        employee.setActivationToken(token);
        employee.setActivationTokenExpiry(expiryTime);
        employeeRepository.save(employee);
        
        return token;
    }
    
    @Transactional
    public void activateEmployeeAccount(String token, String password) {
        Employee employee = getEmployeeFromList(employeeRepository.findByActivationToken(token),
                "Invalid activation token");
        
        // Check if token is expired
        if (employee.getActivationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Activation token has expired");
        }
        
        // Set the password and activate the account
        employee.setPasswordHash(passwordEncoder.encode(password));
        employee.setAccountActivated(true);
        employee.setActivationToken(null);
        employee.setActivationTokenExpiry(null);
        employee.addAuthRole(ROLE_EMPLOYEE);
        
        employeeRepository.save(employee);
    }
    
    @Transactional
    public String generatePasswordResetToken(String email) {
        // Check if it's a manager
        if (getBooleanResult(userRepository.existsByEmail(email))) {
            User user = getUserByEmail(email);
            
            // Generate a random token
            String token = UUID.randomUUID().toString();
            
            // Set expiry time (24 hours from now)
            LocalDateTime expiryTime = LocalDateTime.now().plusHours(tokenExpirationHours);
            
            // Save the token and expiry to the user
            user.setResetToken(token);
            user.setResetTokenExpiry(expiryTime);
            userRepository.save(user);
            
            return token;
        } 
        // Check if it's an employee
        else if (getBooleanResult(employeeRepository.existsByEmail(email))) {
            Employee employee = getEmployeeByEmail(email);
            
            // Generate a random token
            String token = UUID.randomUUID().toString();
            
            // Set expiry time (24 hours from now)
            LocalDateTime expiryTime = LocalDateTime.now().plusHours(tokenExpirationHours);
            
            // Save the token and expiry to the employee
            employee.setResetToken(token);
            employee.setResetTokenExpiry(expiryTime);
            employeeRepository.save(employee);
            
            return token;
        }
        
        throw new ResourceNotFoundException("No account found with email: " + email);
    }
    
    @Transactional
    public void resetPassword(String token, String password) {
        // Check if it's a manager token
        if (getBooleanResult(userRepository.existsByResetToken(token))) {
            User user = getUserFromList(userRepository.findByResetToken(token),
                    "Invalid reset token");
            
            // Check if token is expired
            if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Reset token has expired");
            }
            
            // Update the password and clear the token
            user.setPassword(passwordEncoder.encode(password));
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
        } 
        // Check if it's an employee token
        else if (getBooleanResult(employeeRepository.existsByResetToken(token))) {
            Employee employee = getEmployeeFromList(employeeRepository.findByResetToken(token),
                    "Invalid reset token");
            
            // Check if token is expired
            if (employee.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Reset token has expired");
            }
            
            // Update the password and clear the token
            employee.setPasswordHash(passwordEncoder.encode(password));
            employee.setResetToken(null);
            employee.setResetTokenExpiry(null);
            employeeRepository.save(employee);
        } else {
            throw new BadRequestException("Invalid reset token");
        }
    }
    
    @Transactional
    public void sendActivationEmail(Employee employee, String activationToken) {
        String activationLink = "/activate?token=" + activationToken;
        
        // Create email content
        String subject = "Welcome to " + employee.getUser().getCompanyName() + " - Activate Your Account";
        String content = "Dear " + employee.getName() + ",\n\n" +
                "Welcome to the Employee Management System at " + employee.getUser().getCompanyName() + ".\n\n" +
                "Please click the link below to activate your account and set your password:\n" +
                activationLink + "\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you did not expect this email, please ignore it.\n\n" +
                "Regards,\n" +
                "Employee Management System";
        
        // Send the email
        try {
            emailService.sendEmail(employee.getEmail(), subject, content);
        } catch (Exception e) {
            throw new AuthenticationException("Failed to send activation email: " + e.getMessage());
        }
    }
}
