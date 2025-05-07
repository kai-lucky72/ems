package com.ems.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "full_name", nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String phone;

    @Column(nullable = false)
    private String role;
    
    // Authentication related fields
    @Column(name = "password_hash")
    private String passwordHash;
    
    @Column(name = "is_account_activated", nullable = false)
    private boolean isAccountActivated = false;
    
    @Column(name = "activation_token")
    private String activationToken;
    
    @Column(name = "activation_token_expiry")
    private LocalDateTime activationTokenExpiry;
    
    @Column(name = "reset_token")
    private String resetToken;
    
    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "employee_roles", joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "role")
    private Set<String> authRoles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false)
    private ContractType contractType;

    @Column(name = "contract_start", nullable = false)
    private LocalDate startDate;

    @Column(name = "contract_end")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Salary> salaries = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Leave> leaves = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<EmployeeInactivity> inactivityPeriods = new ArrayList<>();

    public enum ContractType {
        FULL_TIME, PART_TIME, REMOTE;
        
        // Convert from database values
        public static ContractType fromString(String str) {
            if (str == null) return null;
            
            return switch (str.toLowerCase().replace('-', '_')) {
                case "full_time" -> FULL_TIME;
                case "part_time" -> PART_TIME;
                case "remote" -> REMOTE;
                default -> throw new IllegalArgumentException("Unknown contract type: " + str);
            };
        }
        
        // Convert to database values
        public String toDatabaseValue() {
            return switch (this) {
                case FULL_TIME -> "full-time";
                case PART_TIME -> "part-time";
                case REMOTE -> "remote";
            };
        }
    }
    
    public enum Status {
        ACTIVE, INACTIVE;
        
        // Convert from database values
        public static Status fromString(String str) {
            if (str == null) return null;
            
            return switch (str.toLowerCase()) {
                case "active" -> ACTIVE;
                case "inactive" -> INACTIVE;
                default -> throw new IllegalArgumentException("Unknown status: " + str);
            };
        }
        
        // Convert to database values
        public String toDatabaseValue() {
            return name().toLowerCase();
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public void setActive(boolean isActive) {
        this.status = isActive ? Status.ACTIVE : Status.INACTIVE;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Salary> getSalaries() {
        return salaries;
    }

    public void setSalaries(List<Salary> salaries) {
        this.salaries = salaries;
    }
    
    // Get the most recent salary
    public Salary getCurrentSalary() {
        if (salaries == null || salaries.isEmpty()) {
            return null;
        }
        
        Salary latestSalary = null;
        for (Salary salary : salaries) {
            if (latestSalary == null || 
                (salary.getSalaryYear() > latestSalary.getSalaryYear()) ||
                (salary.getSalaryYear().equals(latestSalary.getSalaryYear()) && 
                 salary.getSalaryMonth() > latestSalary.getSalaryMonth())) {
                latestSalary = salary;
            }
        }
        return latestSalary;
    }

    public List<Leave> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<Leave> leaves) {
        this.leaves = leaves;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    public List<EmployeeInactivity> getInactivityPeriods() {
        return inactivityPeriods;
    }
    
    public void setInactivityPeriods(List<EmployeeInactivity> inactivityPeriods) {
        this.inactivityPeriods = inactivityPeriods;
    }
    
    public void addInactivityPeriod(EmployeeInactivity inactivity) {
        inactivityPeriods.add(inactivity);
        inactivity.setEmployee(this);
    }
    
    public void removeInactivityPeriod(EmployeeInactivity inactivity) {
        inactivityPeriods.remove(inactivity);
        inactivity.setEmployee(null);
    }

    // Check if employee is on leave
    public boolean isOnLeave() {
        LocalDate today = LocalDate.now();
        for (Leave leave : leaves) {
            if (leave.getStatus() == Leave.Status.APPROVED && 
                !today.isBefore(leave.getStartDate()) && 
                !today.isAfter(leave.getEndDate())) {
                return true;
            }
        }
        return false;
    }
    
    // Check if employee is currently inactive based on inactivity records
    public boolean isCurrentlyInactive() {
        if (status == Status.INACTIVE) {
            return true;
        }
        
        LocalDate today = LocalDate.now();
        for (EmployeeInactivity inactivity : inactivityPeriods) {
            if (!today.isBefore(inactivity.getStartDate()) && 
                (inactivity.getEndDate() == null || !today.isAfter(inactivity.getEndDate()))) {
                return true;
            }
        }
        return false;
    }
    
    // Get employee's most recent inactivity period (if any)
    public EmployeeInactivity getCurrentInactivityPeriod() {
        if (status == Status.ACTIVE || inactivityPeriods.isEmpty()) {
            return null;
        }
        
        LocalDate today = LocalDate.now();
        for (EmployeeInactivity inactivity : inactivityPeriods) {
            if (!today.isBefore(inactivity.getStartDate()) && 
                (inactivity.getEndDate() == null || !today.isAfter(inactivity.getEndDate()))) {
                return inactivity;
            }
        }
        
        // If no current inactivity, return the most recent one
        return inactivityPeriods.stream()
            .sorted((a, b) -> b.getStartDate().compareTo(a.getStartDate()))
            .findFirst()
            .orElse(null);
    }
    
    // Get inactivity from/to for DTO
    public LocalDate getInactiveFrom() {
        EmployeeInactivity inactivity = getCurrentInactivityPeriod();
        return inactivity != null ? inactivity.getStartDate() : null;
    }
    
    public LocalDate getInactiveTo() {
        EmployeeInactivity inactivity = getCurrentInactivityPeriod();
        return inactivity != null ? inactivity.getEndDate() : null;
    }
    
    // Authentication-related getters and setters
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isAccountActivated() {
        return isAccountActivated;
    }

    public void setAccountActivated(boolean isAccountActivated) {
        this.isAccountActivated = isAccountActivated;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public LocalDateTime getActivationTokenExpiry() {
        return activationTokenExpiry;
    }

    public void setActivationTokenExpiry(LocalDateTime activationTokenExpiry) {
        this.activationTokenExpiry = activationTokenExpiry;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Set<String> getAuthRoles() {
        return authRoles;
    }

    public void setAuthRoles(Set<String> authRoles) {
        this.authRoles = authRoles;
    }
    
    public void addAuthRole(String role) {
        this.authRoles.add(role.toUpperCase());
    }
    
    public boolean hasAuthRole(String role) {
        return this.authRoles.contains(role.toUpperCase());
    }
    
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
}
