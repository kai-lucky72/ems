package com.ems.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.User;
import com.ems.model.Employee.ContractType;
import com.ems.model.Employee.Status;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Basic queries
    List<Employee> findByUser(User user);
    
    Optional<Employee> findByIdAndUser(Long id, User user);
    
    List<Employee> findByDepartment(Department department);
    
    List<Employee> findByUserAndStatus(User user, Status status);
    
    Optional<Employee> findByEmailAndUser(String email, User user);
    
    @Query("SELECT e FROM Employee e WHERE e.email = :email")
    List<Employee> findByEmail(@Param("email") String email);
    
    List<Boolean> existsByEmail(String email);
    
    List<Employee> findByActivationToken(String activationToken);
    
    List<Employee> findByResetToken(String resetToken);
    
    List<Boolean> existsByResetToken(String resetToken);
    
    // Ordered queries for UI display
    @Query("SELECT e FROM Employee e WHERE e.user = :user ORDER BY e.name ASC")
    List<Employee> findByUserOrderByNameAsc(@Param("user") User user);
    
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.status = :status ORDER BY e.name ASC")
    List<Employee> findByUserAndStatusOrderByNameAsc(@Param("user") User user, @Param("status") Status status);
    
    @Query("SELECT e FROM Employee e WHERE e.user = :user ORDER BY e.name ASC")
    Page<Employee> findByUserPageable(@Param("user") User user, Pageable pageable);
    
    // Find employees on leave
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.status = 'ACTIVE' " +
           "AND EXISTS (SELECT l FROM Leave l WHERE l.employee = e AND l.status = 'APPROVED' " +
           "AND :date BETWEEN l.startDate AND l.endDate)")
    List<Employee> findEmployeesOnLeaveByDate(@Param("user") User user, @Param("date") LocalDate date);
    
    // Count queries for analytics
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.user = :user AND e.status = :status " +
           "AND (:date IS NULL OR e.createdAt <= :date)")
    List<Long> countByUserAndStatusAndDate(@Param("user") User user, @Param("status") Status status, 
                                    @Param("date") LocalDate date);
    
    @Query("SELECT e.role AS role, COUNT(e) AS count FROM Employee e WHERE e.user = :user GROUP BY e.role")
    List<Object[]> countEmployeesByRole(@Param("user") User user);
    
    @Query("SELECT e.contractType AS contractType, COUNT(e) AS count FROM Employee e WHERE e.user = :user GROUP BY e.contractType")
    List<Object[]> countEmployeesByContractType(@Param("user") User user);
    
    @Query("SELECT d.name AS department, COUNT(e) AS count FROM Employee e " +
           "JOIN e.department d WHERE e.user = :user GROUP BY d.name")
    List<Object[]> countEmployeesByDepartment(@Param("user") User user);
    
    @Query("SELECT EXTRACT(YEAR FROM e.startDate) as year, EXTRACT(MONTH FROM e.startDate) as month, COUNT(e) as count " +
          "FROM Employee e WHERE e.user = :user GROUP BY EXTRACT(YEAR FROM e.startDate), EXTRACT(MONTH FROM e.startDate) " +
          "ORDER BY EXTRACT(YEAR FROM e.startDate) ASC, EXTRACT(MONTH FROM e.startDate) ASC")
    List<Object[]> countEmployeesByStartDate(@Param("user") User user);
    
    // Find employees by contract type
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.contractType = :contractType ORDER BY e.name ASC")
    List<Employee> findByUserAndContractType(@Param("user") User user, @Param("contractType") ContractType contractType);
    
    // Find active employees by department
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId AND e.status = 'ACTIVE' ORDER BY e.name ASC")
    List<Employee> findActiveEmployeesByDepartmentId(@Param("departmentId") Long departmentId);
    
    // Find active employees by department for a specific user
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId AND e.status = 'ACTIVE' " +
           "AND e.user = :user ORDER BY e.name ASC")
    List<Employee> findActiveEmployeesByDepartmentIdAndUser(
            @Param("departmentId") Long departmentId, 
            @Param("user") User user);
    
    // Find employees with no salary assigned
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.status = 'ACTIVE' " +
           "AND NOT EXISTS (SELECT s FROM Salary s WHERE s.employee = e)")
    List<Employee> findEmployeesWithNoSalary(@Param("user") User user);
    
    // Find employees with pending leaves
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.status = 'ACTIVE' " +
           "AND EXISTS (SELECT l FROM Leave l WHERE l.employee = e AND l.status = 'PENDING')")
    List<Employee> findEmployeesWithPendingLeaves(@Param("user") User user);
    
    // Find employees with specific role
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.role = :role ORDER BY e.name ASC")
    List<Employee> findByUserAndRole(@Param("user") User user, @Param("role") String role);
    
    // Employees whose contract is ending soon
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.status = 'ACTIVE' " +
           "AND e.endDate IS NOT NULL AND e.endDate <= :endDateThreshold " +
           "ORDER BY e.endDate ASC")
    List<Employee> findEmployeesWithContractEndingSoon(
            @Param("user") User user, 
            @Param("endDateThreshold") LocalDate endDateThreshold);
    
    // Find new employees (joined recently)
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.startDate >= :recentDate ORDER BY e.startDate DESC")
    List<Employee> findRecentEmployees(@Param("user") User user, @Param("recentDate") LocalDate recentDate);
    
    // Get employees with anniversaries coming up
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.status = 'ACTIVE' " +
           "AND EXTRACT(MONTH FROM e.startDate) = :month AND EXTRACT(DAY FROM e.startDate) BETWEEN :dayStart AND :dayEnd " +
           "ORDER BY EXTRACT(DAY FROM e.startDate)")
    List<Employee> findEmployeesWithUpcomingAnniversaries(
            @Param("user") User user, 
            @Param("month") int month, 
            @Param("dayStart") int dayStart, 
            @Param("dayEnd") int dayEnd);
    
    // Search employees by name, email, role or department
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND " +
           "(LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.role) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.department.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY e.name ASC")
    List<Employee> searchEmployees(@Param("user") User user, @Param("searchTerm") String searchTerm);
    
    // Currently inactive employees with valid inactivity records
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND " + 
          "e.status = 'INACTIVE' AND " +
          "EXISTS (SELECT i FROM EmployeeInactivity i WHERE i.employee = e AND " +
          "(i.endDate IS NULL OR i.endDate >= :currentDate) AND i.startDate <= :currentDate)")
    List<Employee> findCurrentlyInactiveEmployees(@Param("user") User user, @Param("currentDate") LocalDate currentDate);
    
    // Employees returning from inactivity in the next X days
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.status = 'INACTIVE' " +
           "AND EXISTS (SELECT i FROM EmployeeInactivity i WHERE i.employee = e " +
           "AND i.endDate IS NOT NULL AND i.endDate BETWEEN :currentDate AND :futureDate)")
    List<Employee> findEmployeesReturningFromInactivity(
            @Param("user") User user, 
            @Param("currentDate") LocalDate currentDate,
            @Param("futureDate") LocalDate futureDate);
}
