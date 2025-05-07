package com.ems.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ems.model.Department;
import com.ems.model.Department.BudgetType;
import com.ems.model.Employee.Status;
import com.ems.model.User;

/**
 * Repository for Department entity with various query methods for department operations
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // Basic queries
    List<Department> findByUser(User user);
    
    Optional<Department> findByIdAndUser(Long id, User user);
    
    boolean existsByNameAndUser(String name, User user);
    
    Optional<Department> findByNameAndUser(String name, User user);
    
    // Sorted queries
    @Query("SELECT d FROM Department d WHERE d.user = :user ORDER BY d.name ASC")
    List<Department> findByUserOrderByNameAsc(@Param("user") User user);
    
    @Query("SELECT d FROM Department d WHERE d.user = :user ORDER BY d.budget DESC")
    List<Department> findByUserOrderByBudgetDesc(@Param("user") User user);
    
    // Pagination support
    Page<Department> findByUser(User user, Pageable pageable);
    
    // Employee-related queries
    @Query("SELECT d FROM Department d JOIN d.employees e WHERE e.id = :employeeId")
    Optional<Department> findByEmployeeId(@Param("employeeId") Long employeeId);
    
    @Query("SELECT d FROM Department d LEFT JOIN d.employees e GROUP BY d.id HAVING COUNT(e) = 0")
    List<Department> findDepartmentsWithNoEmployees();
    
    @Query("SELECT d FROM Department d LEFT JOIN d.employees e WHERE d.user = :user GROUP BY d.id HAVING COUNT(e) = 0")
    List<Department> findEmptyDepartmentsByUser(@Param("user") User user);
    
    // Employee count statistics
    @Query("SELECT d.name, COUNT(e) FROM Department d LEFT JOIN d.employees e " +
           "WHERE d.user = :user GROUP BY d.id, d.name ORDER BY COUNT(e) DESC")
    List<Object[]> countEmployeesByDepartment(@Param("user") User user);
    
    @Query("SELECT d.id, d.name, COUNT(e) FROM Department d LEFT JOIN d.employees e " +
           "WHERE d.user = :user AND e.status = :status GROUP BY d.id, d.name")
    List<Object[]> countActiveEmployeesByDepartment(@Param("user") User user, @Param("status") Status status);
    
    @Query("SELECT COUNT(d) FROM Department d WHERE d.user = :user")
    long countByUser(@Param("user") User user);
    
    // Budget-related queries
    @Query("SELECT d FROM Department d WHERE d.user = :user AND d.budgetType = :budgetType")
    List<Department> findByUserAndBudgetType(@Param("user") User user, @Param("budgetType") BudgetType budgetType);
    
    @Query("SELECT SUM(d.budget) FROM Department d WHERE d.user = :user")
    Double sumBudgetByUser(@Param("user") User user);
    
    @Query("SELECT d.budgetType, SUM(d.budget) FROM Department d WHERE d.user = :user GROUP BY d.budgetType")
    List<Object[]> sumBudgetByTypeAndUser(@Param("user") User user);
    
    // Budget analysis
    @Query("SELECT d FROM Department d WHERE d.user = :user AND " +
           "(SELECT SUM(s.grossSalary) FROM Salary s JOIN s.employee e WHERE e.department = d) > d.budget")
    List<Department> findOverBudgetDepartments(@Param("user") User user);
    
    @Query("SELECT d FROM Department d WHERE d.user = :user AND " + 
           "(SELECT SUM(s.grossSalary) FROM Salary s JOIN s.employee e WHERE e.department = d) / d.budget > 0.9")
    List<Department> findNearBudgetLimitDepartments(@Param("user") User user);
    
    @Query("SELECT d, (SELECT SUM(s.grossSalary) FROM Salary s JOIN s.employee e WHERE e.department = d) AS totalSalary " +
           "FROM Department d WHERE d.user = :user ORDER BY totalSalary DESC")
    List<Object[]> findDepartmentsByTotalSalary(@Param("user") User user);
    
    // Complex analytics
    @Query("SELECT d.name, COUNT(e), AVG(s.grossSalary) " +
           "FROM Department d LEFT JOIN d.employees e LEFT JOIN e.salary s " +
           "WHERE d.user = :user GROUP BY d.name")
    List<Object[]> getDepartmentEmployeeAndSalaryStats(@Param("user") User user);
    
    @Query("SELECT d.name, d.budgetType, d.budget, " +
           "(SELECT SUM(s.grossSalary) FROM Salary s JOIN s.employee e WHERE e.department = d) AS totalSalary " +
           "FROM Department d WHERE d.user = :user")
    List<Object[]> getDepartmentBudgetReport(@Param("user") User user);
    
    // Search
    @Query("SELECT d FROM Department d WHERE d.user = :user AND LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Department> searchByNameContaining(@Param("user") User user, @Param("keyword") String keyword);
    
    // Department growth
    @Query("SELECT d, COUNT(e) AS empCount FROM Department d JOIN d.employees e " +
           "WHERE d.user = :user AND e.startDate >= CURRENT_DATE - 30 " +
           "GROUP BY d.id ORDER BY empCount DESC")
    List<Object[]> findFastestGrowingDepartments(@Param("user") User user);
}
