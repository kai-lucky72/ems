package com.ems.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ems.model.Deduction;
import com.ems.model.Deduction.DeductionType;
import com.ems.model.Salary;
import com.ems.model.User;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {
    // Basic queries
    List<Deduction> findBySalary(Salary salary);
    
    List<Deduction> findBySalaryAndType(Salary salary, DeductionType type);
    
    List<Deduction> findBySalaryOrderByTypeAsc(Salary salary);
    
    // User-based queries
    @Query("SELECT d FROM Deduction d WHERE d.salary.employee.user = :user")
    List<Deduction> findByUser(@Param("user") User user);
    
    @Query("SELECT d FROM Deduction d WHERE d.salary.employee.user = :user ORDER BY d.type")
    List<Deduction> findByUserOrderByType(@Param("user") User user);
    
    // Salary-based queries
    @Query("SELECT d FROM Deduction d WHERE d.salary.id = :salaryId ORDER BY d.type, d.name")
    List<Deduction> findBySalaryId(@Param("salaryId") Long salaryId);
    
    // Employee-based queries
    @Query("SELECT d FROM Deduction d WHERE d.salary.employee.id = :employeeId ORDER BY d.salary.salaryYear DESC, d.salary.salaryMonth DESC, d.type")
    List<Deduction> findByEmployeeId(@Param("employeeId") Long employeeId);
    
    @Query("SELECT d FROM Deduction d WHERE d.salary.employee.id = :employeeId AND d.salary.id = " +
           "(SELECT MAX(s.id) FROM Salary s WHERE s.employee.id = :employeeId)")
    List<Deduction> findByEmployeeIdForCurrentSalary(@Param("employeeId") Long employeeId);
    
    // Analytics queries
    @Query("SELECT COUNT(d) FROM Deduction d WHERE d.type = :type AND d.salary.employee.user = :user")
    List<Long> countByTypeAndUser(@Param("type") DeductionType type, @Param("user") User user);
    
    @Query("SELECT d.type, COUNT(d), AVG(d.value), SUM(CASE WHEN d.isPercentage = true THEN 1 ELSE 0 END) " +
           "FROM Deduction d WHERE d.salary.employee.user = :user GROUP BY d.type")
    List<Object[]> getDeductionStatsByUser(@Param("user") User user);
    
    @Query("SELECT d.type, COUNT(d) FROM Deduction d " + 
           "WHERE d.salary.salaryYear = :year AND d.salary.salaryMonth = :month AND d.salary.employee.user = :user " +
           "GROUP BY d.type")
    List<Object[]> countDeductionsByTypeForMonth(
            @Param("user") User user, 
            @Param("year") Integer year, 
            @Param("month") Integer month);
    
    @Query("SELECT AVG(CASE WHEN d.isPercentage = true THEN (d.value / 100) * d.salary.grossSalary ELSE d.value END) " +
           "FROM Deduction d WHERE d.type = :type AND d.salary.employee.user = :user")
    List<Double> getAverageDeductionAmountByType(@Param("type") DeductionType type, @Param("user") User user);
    
    // Common deduction templates used by a company
    @Query("SELECT d.name, d.type, d.value, d.isPercentage, COUNT(d) AS frequency " +
           "FROM Deduction d WHERE d.salary.employee.user = :user " +
           "GROUP BY d.name, d.type, d.value, d.isPercentage " +
           "ORDER BY frequency DESC")
    List<Object[]> findCommonDeductionTemplates(@Param("user") User user);
    
    // Sum of deductions by type
    @Query("SELECT d.type, SUM(CASE WHEN d.isPercentage = true THEN (d.value / 100) * d.salary.grossSalary ELSE d.value END) " +
           "FROM Deduction d WHERE d.salary.employee.user = :user AND d.salary.salaryYear = :year AND d.salary.salaryMonth = :month " +
           "GROUP BY d.type")
    List<Object[]> sumDeductionsByTypeForMonth(
            @Param("user") User user, 
            @Param("year") Integer year, 
            @Param("month") Integer month);
    
    // Percentage vs. Fixed amount deductions
    @Query("SELECT d.isPercentage, COUNT(d) FROM Deduction d WHERE d.salary.employee.user = :user GROUP BY d.isPercentage")
    List<Object[]> countDeductionsByPercentageType(@Param("user") User user);
    
    // Delete operations
    @Transactional
    void deleteBySalary(Salary salary);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Deduction d WHERE d.salary.id = :salaryId")
    void deleteBySalaryId(@Param("salaryId") Long salaryId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Deduction d WHERE d.id IN :ids AND d.salary.employee.user = :user")
    void deleteByIdsAndUser(@Param("ids") List<Long> deductionIds, @Param("user") User user);
}
