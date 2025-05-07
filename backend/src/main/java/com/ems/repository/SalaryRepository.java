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
import com.ems.model.Employee;
import com.ems.model.Salary;
import com.ems.model.User;
import com.ems.model.Employee.ContractType;
import com.ems.model.Employee.Status;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    // Basic queries
    List<Salary> findByEmployee(Employee employee);
    
    Optional<Salary> findByEmployeeAndSalaryMonthAndSalaryYear(Employee employee, Integer month, Integer year);
    
    // Find most recent salary for an employee
    @Query("SELECT s FROM Salary s WHERE s.employee = :employee " +
           "ORDER BY s.salaryYear DESC, s.salaryMonth DESC LIMIT 1")
    Optional<Salary> findMostRecentByEmployee(@Param("employee") Employee employee);
    
    // Find salaries by user (company owner/manager)
    @Query("SELECT s FROM Salary s WHERE s.employee.user = :user ORDER BY s.salaryYear DESC, s.salaryMonth DESC")
    List<Salary> findByUser(@Param("user") User user);
    
    @Query("SELECT s FROM Salary s WHERE s.employee.user = :user ORDER BY s.salaryYear DESC, s.salaryMonth DESC")
    Page<Salary> findByUserPaginated(@Param("user") User user, Pageable pageable);
    
    // Find salary by ID with user verification
    @Query("SELECT s FROM Salary s WHERE s.id = :id AND s.employee.user = :user")
    Optional<Salary> findByIdAndUser(@Param("id") Long id, @Param("user") User user);
    
    // Find salaries for a specific employee
    @Query("SELECT s FROM Salary s WHERE s.employee.id = :employeeId AND s.employee.user = :user " +
           "ORDER BY s.salaryYear DESC, s.salaryMonth DESC")
    List<Salary> findByEmployeeIdAndUser(@Param("employeeId") Long employeeId, @Param("user") User user);
    
    // Find most recent salary for each employee of a user
    @Query("SELECT s1 FROM Salary s1 WHERE s1.employee.user = :user AND " +
           "NOT EXISTS (SELECT 1 FROM Salary s2 WHERE s2.employee = s1.employee AND " +
           "(s2.salaryYear > s1.salaryYear OR (s2.salaryYear = s1.salaryYear AND s2.salaryMonth > s1.salaryMonth)))")
    List<Salary> findMostRecentSalariesByUser(@Param("user") User user);
    
    // Aggregate queries for current month
    @Query("SELECT SUM(s.grossSalary) FROM Salary s WHERE s.employee.user = :user AND " +
           "s.employee.status = :status AND s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) AND " +
           "s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE)")
    Double sumGrossSalaryByUserForCurrentMonth(@Param("user") User user, @Param("status") Status status);
    
    @Query("SELECT SUM(s.netSalary) FROM Salary s WHERE s.employee.user = :user AND " +
           "s.employee.status = :status AND s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) AND " +
           "s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE)")
    Double sumNetSalaryByUserForCurrentMonth(@Param("user") User user, @Param("status") Status status);
    
    @Query("SELECT AVG(s.grossSalary) FROM Salary s WHERE s.employee.user = :user AND " +
           "s.employee.status = :status AND s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) AND " +
           "s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE)")
    Double averageGrossSalaryByUserForCurrentMonth(@Param("user") User user, @Param("status") Status status);
    
    // Aggregate queries for specific month/year
    @Query("SELECT SUM(s.grossSalary) FROM Salary s WHERE s.employee.user = :user AND " +
           "s.employee.status = :status AND s.salaryYear = :year AND s.salaryMonth = :month")
    Double sumGrossSalaryByUserForMonth(@Param("user") User user, @Param("status") Status status, 
                                      @Param("year") Integer year, @Param("month") Integer month);
    
    @Query("SELECT SUM(s.netSalary) FROM Salary s WHERE s.employee.user = :user AND " +
           "s.employee.status = :status AND s.salaryYear = :year AND s.salaryMonth = :month")
    Double sumNetSalaryByUserForMonth(@Param("user") User user, @Param("status") Status status, 
                                    @Param("year") Integer year, @Param("month") Integer month);
    
    // Department-based salary summaries
    @Query("SELECT d.name, SUM(s.grossSalary) FROM Salary s JOIN s.employee e JOIN e.department d " +
           "WHERE e.user = :user AND e.status = :status AND s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) " +
           "AND s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE) GROUP BY d.name")
    List<Object[]> sumSalaryByDepartmentForCurrentMonth(@Param("user") User user, @Param("status") Status status);
    
    @Query("SELECT d.name, SUM(s.grossSalary) FROM Salary s JOIN s.employee e JOIN e.department d " +
           "WHERE e.user = :user AND e.status = :status AND s.salaryYear = :year " +
           "AND s.salaryMonth = :month GROUP BY d.name")
    List<Object[]> sumSalaryByDepartmentForMonth(@Param("user") User user, @Param("status") Status status,
                                               @Param("year") Integer year, @Param("month") Integer month);
    
    // Contract type based salary summaries
    @Query("SELECT e.contractType, AVG(s.grossSalary) FROM Salary s JOIN s.employee e " +
           "WHERE e.user = :user AND e.status = :status AND s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) " +
           "AND s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE) GROUP BY e.contractType")
    List<Object[]> avgSalaryByContractTypeForCurrentMonth(@Param("user") User user, @Param("status") Status status);
    
    // Role-based salary summaries
    @Query("SELECT e.role, AVG(s.grossSalary), MIN(s.grossSalary), MAX(s.grossSalary) FROM Salary s JOIN s.employee e " +
           "WHERE e.user = :user AND e.status = :status AND s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) " +
           "AND s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE) GROUP BY e.role")
    List<Object[]> salaryStatsByRoleForCurrentMonth(@Param("user") User user, @Param("status") Status status);
    
    // Month/Year summaries
    @Query("SELECT DISTINCT s.salaryYear, s.salaryMonth FROM Salary s WHERE s.employee.user = :user " +
           "ORDER BY s.salaryYear DESC, s.salaryMonth DESC")
    List<Object[]> findDistinctSalaryMonthsAndYears(@Param("user") User user);
    
    @Query("SELECT s FROM Salary s WHERE s.employee.user = :user AND s.salaryYear = :year AND s.salaryMonth = :month")
    List<Salary> findByUserAndYearAndMonth(@Param("user") User user, @Param("year") Integer year, @Param("month") Integer month);
    
    // Historical trend queries
    @Query("SELECT s.salaryYear, s.salaryMonth, SUM(s.grossSalary) FROM Salary s WHERE s.employee.user = :user " +
           "GROUP BY s.salaryYear, s.salaryMonth ORDER BY s.salaryYear, s.salaryMonth")
    List<Object[]> getSalaryTrendByMonth(@Param("user") User user);
    
    // Department-specific queries
    @Query("SELECT s FROM Salary s JOIN s.employee e WHERE e.department = :department " +
           "AND s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) AND s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE)")
    List<Salary> findCurrentSalariesByDepartment(@Param("department") Department department);
    
    // Top earners
    @Query("SELECT s FROM Salary s WHERE s.employee.user = :user AND " +
           "s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) AND s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE) " +
           "ORDER BY s.grossSalary DESC")
    List<Salary> findTopEarners(@Param("user") User user, Pageable pageable);
    
    // Salary comparison by contract type
    @Query("SELECT e.contractType, AVG(s.grossSalary), COUNT(s) FROM Salary s JOIN s.employee e " +
           "WHERE e.user = :user AND s.salaryYear = :year AND s.salaryMonth = :month " + 
           "GROUP BY e.contractType")
    List<Object[]> compareSalariesByContractType(@Param("user") User user, @Param("year") Integer year, @Param("month") Integer month);
    
    // Employee without current month salary
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.status = :status AND " +
           "NOT EXISTS (SELECT 1 FROM Salary s WHERE s.employee = e AND " +
           "s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) AND s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE))")
    List<Employee> findEmployeesWithoutCurrentMonthSalary(@Param("user") User user, @Param("status") Status status);
    
    // Count salaries by department
    @Query("SELECT d.id, d.name, COUNT(s) FROM Salary s JOIN s.employee e JOIN e.department d " +
           "WHERE e.user = :user AND s.salaryYear = :year AND s.salaryMonth = :month " +
           "GROUP BY d.id, d.name")
    List<Object[]> countSalariesByDepartment(@Param("user") User user, @Param("year") Integer year, @Param("month") Integer month);
}
