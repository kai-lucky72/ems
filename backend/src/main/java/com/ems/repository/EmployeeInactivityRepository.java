package com.ems.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ems.model.Employee;
import com.ems.model.EmployeeInactivity;
import com.ems.model.User;

@Repository
public interface EmployeeInactivityRepository extends JpaRepository<EmployeeInactivity, Long> {
    List<EmployeeInactivity> findByEmployee(Employee employee);
    
    @Query("SELECT ei FROM EmployeeInactivity ei WHERE ei.employee.user = :user ORDER BY ei.startDate DESC")
    List<EmployeeInactivity> findByUser(@Param("user") User user);
    
    @Query("SELECT ei FROM EmployeeInactivity ei WHERE ei.employee.id = :employeeId ORDER BY ei.startDate DESC")
    List<EmployeeInactivity> findByEmployeeId(@Param("employeeId") Long employeeId);
    
    @Query("SELECT ei FROM EmployeeInactivity ei WHERE ei.employee.id = :employeeId AND " +
           "ei.endDate IS NULL")
    Optional<EmployeeInactivity> findCurrentInactivityByEmployeeId(@Param("employeeId") Long employeeId);
    
    @Query("SELECT ei FROM EmployeeInactivity ei WHERE ei.employee.user = :user AND " +
           "((ei.startDate <= :startDate AND (ei.endDate IS NULL OR ei.endDate >= :startDate)) OR " +
           "(ei.startDate <= :endDate AND (ei.endDate IS NULL OR ei.endDate >= :endDate)) OR " +
           "(ei.startDate >= :startDate AND (ei.endDate IS NULL OR ei.endDate <= :endDate)))")
    List<EmployeeInactivity> findActiveInactivitiesInDateRange(
            @Param("user") User user, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(ei) FROM EmployeeInactivity ei WHERE ei.employee.user = :user AND " +
           "(ei.endDate IS NULL OR ei.endDate >= CURRENT_DATE)")
    long countCurrentInactivities(@Param("user") User user);
    
    @Query("SELECT ei FROM EmployeeInactivity ei WHERE ei.employee.user = :user AND " +
           "(ei.endDate IS NULL OR ei.endDate >= CURRENT_DATE)")
    List<EmployeeInactivity> findCurrentInactivities(@Param("user") User user);
    
    @Query("SELECT FUNCTION('YEAR', ei.startDate) AS year, FUNCTION('MONTH', ei.startDate) AS month, " +
           "COUNT(ei) AS count FROM EmployeeInactivity ei WHERE ei.employee.user = :user " +
           "GROUP BY FUNCTION('YEAR', ei.startDate), FUNCTION('MONTH', ei.startDate) " +
           "ORDER BY FUNCTION('YEAR', ei.startDate) DESC, FUNCTION('MONTH', ei.startDate) DESC")
    List<Object[]> countInactivitiesByMonth(@Param("user") User user);
    
    @Query("SELECT COUNT(DISTINCT ei.employee) FROM EmployeeInactivity ei WHERE ei.employee.user = :user " +
           "AND ei.startDate <= CURRENT_DATE AND (ei.endDate IS NULL OR ei.endDate >= CURRENT_DATE)")
    long countCurrentlyInactiveEmployees(@Param("user") User user);
    
    @Query("SELECT ei FROM EmployeeInactivity ei WHERE ei.employee.user = :user " +
           "AND :date BETWEEN ei.startDate AND COALESCE(ei.endDate, :date)")
    List<EmployeeInactivity> findInactivitiesByDate(@Param("user") User user, @Param("date") LocalDate date);
}