package com.ems.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ems.model.Department;
import com.ems.model.Department.BudgetType;
import com.ems.model.User;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByUser(User user);
    
    Optional<Department> findByIdAndUser(Long id, User user);
    
    boolean existsByNameAndUser(String name, User user);
    
    @Query("SELECT d FROM Department d WHERE d.user = :user ORDER BY d.name ASC")
    List<Department> findByUserOrderByNameAsc(@Param("user") User user);
    
    @Query("SELECT d FROM Department d JOIN d.employees e WHERE e.id = :employeeId")
    Optional<Department> findByEmployeeId(@Param("employeeId") Long employeeId);
    
    @Query("SELECT d.name, COUNT(e) FROM Department d LEFT JOIN d.employees e " +
           "WHERE d.user = :user GROUP BY d.name ORDER BY COUNT(e) DESC")
    List<Object[]> countEmployeesByDepartment(@Param("user") User user);
    
    @Query("SELECT COUNT(d) FROM Department d WHERE d.user = :user")
    long countByUser(@Param("user") User user);
    
    @Query("SELECT d FROM Department d WHERE d.user = :user AND d.budgetType = :budgetType")
    List<Department> findByUserAndBudgetType(@Param("user") User user, @Param("budgetType") BudgetType budgetType);
    
    @Query("SELECT SUM(d.budget) FROM Department d WHERE d.user = :user")
    Double sumBudgetByUser(@Param("user") User user);
    
    @Query("SELECT d FROM Department d WHERE d.user.id = :userId AND " +
           "(SELECT SUM(s.grossSalary) FROM Salary s JOIN s.employee e WHERE e.department = d) > d.budget")
    List<Department> findOverBudgetDepartments(@Param("userId") Long userId);
}
