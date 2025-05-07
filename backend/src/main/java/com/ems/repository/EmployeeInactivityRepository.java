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
import com.ems.model.EmployeeInactivity.InactivityType;

@Repository
public interface EmployeeInactivityRepository extends JpaRepository<EmployeeInactivity, Long> {
    // Basic queries
    List<EmployeeInactivity> findByEmployee(Employee employee);
    List<EmployeeInactivity> findByEmployeeOrderByStartDateDesc(Employee employee);
    
    @Query("SELECT ei FROM EmployeeInactivity ei JOIN ei.employee e WHERE e.user = :user ORDER BY ei.startDate DESC")
    List<EmployeeInactivity> findByUserOrderByStartDateDesc(@Param("user") User user);
    
    @Query("SELECT ei FROM EmployeeInactivity ei WHERE ei.employee.id = :employeeId ORDER BY ei.startDate DESC")
    List<EmployeeInactivity> findByEmployeeId(@Param("employeeId") Long employeeId);
    
    // Find current inactivity record (no endDate or endDate in the future)
    @Query("SELECT ei FROM EmployeeInactivity ei WHERE ei.employee.id = :employeeId AND " +
           "(ei.endDate IS NULL OR ei.endDate >= :currentDate) " +
           "AND ei.startDate <= :currentDate " +
           "ORDER BY ei.startDate DESC")
    List<EmployeeInactivity> findCurrentInactivityByEmployeeId(
           @Param("employeeId") Long employeeId,
           @Param("currentDate") LocalDate currentDate);
    
    // Find inactivities by type
    @Query("SELECT ei FROM EmployeeInactivity ei JOIN ei.employee e WHERE ei.type = :type AND e.user = :user " +
           "ORDER BY ei.startDate DESC")
    List<EmployeeInactivity> findByTypeAndUser(@Param("type") InactivityType type, @Param("user") User user);
    
    // Count inactivities by type
    @Query("SELECT COUNT(ei) FROM EmployeeInactivity ei JOIN ei.employee e WHERE ei.type = :type AND e.user = :user")
    List<Long> countByTypeAndUser(@Param("type") InactivityType type, @Param("user") User user);
    
    // Find inactivities by date range (that overlap with the given range)
    @Query("SELECT ei FROM EmployeeInactivity ei JOIN ei.employee e WHERE e.user = :user AND " +
           "((ei.startDate <= :endDate) AND (ei.endDate IS NULL OR ei.endDate >= :startDate))")
    List<EmployeeInactivity> findByDateRange(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate,
            @Param("user") User user);
    
    // Find inactivities that are active on a specific date
    @Query("SELECT ei FROM EmployeeInactivity ei JOIN ei.employee e WHERE e.user = :user " +
           "AND (:date >= ei.startDate) AND (:date <= COALESCE(ei.endDate, :date))")
    List<EmployeeInactivity> findInactivitiesByDate(@Param("user") User user, @Param("date") LocalDate date);
    
    // Count current inactivities
    @Query("SELECT COUNT(ei) FROM EmployeeInactivity ei JOIN ei.employee e WHERE e.user = :user AND " +
           "(ei.endDate IS NULL OR ei.endDate >= :currentDate) AND ei.startDate <= :currentDate")
    List<Long> countCurrentInactivities(
           @Param("user") User user,
           @Param("currentDate") LocalDate currentDate);
    
    // Find current inactivities
    @Query("SELECT ei FROM EmployeeInactivity ei JOIN ei.employee e WHERE e.user = :user AND " +
           "(ei.endDate IS NULL OR ei.endDate >= :currentDate) AND ei.startDate <= :currentDate " +
           "ORDER BY ei.startDate DESC")
    List<EmployeeInactivity> findCurrentInactivities(
           @Param("user") User user,
           @Param("currentDate") LocalDate currentDate);
    
    // Calculate average duration of completed inactivities (in days)
    @Query("SELECT AVG(EXTRACT(EPOCH FROM (ei.endDate - ei.startDate)) / 86400.0 + 1) FROM EmployeeInactivity ei " +
           "JOIN ei.employee e WHERE e.user = :user AND ei.endDate IS NOT NULL")
    List<Double> calculateAverageDuration(@Param("user") User user);
    
    // Count inactivities by month
    @Query("SELECT EXTRACT(YEAR FROM ei.startDate) AS year, EXTRACT(MONTH FROM ei.startDate) AS month, " +
           "COUNT(ei) AS count FROM EmployeeInactivity ei JOIN ei.employee e WHERE e.user = :user " +
           "GROUP BY EXTRACT(YEAR FROM ei.startDate), EXTRACT(MONTH FROM ei.startDate) " +
           "ORDER BY EXTRACT(YEAR FROM ei.startDate) DESC, EXTRACT(MONTH FROM ei.startDate) DESC")
    List<Object[]> countInactivitiesByMonth(@Param("user") User user);
    
    // Count inactivities by department
    @Query("SELECT d.name AS department, COUNT(ei) AS count FROM EmployeeInactivity ei " +
           "JOIN ei.employee e JOIN e.department d " +
           "WHERE e.user = :user " +
           "GROUP BY d.name ORDER BY COUNT(ei) DESC")
    List<Object[]> countInactivitiesByDepartment(@Param("user") User user);
    
    // Count currently inactive employees
    @Query("SELECT COUNT(DISTINCT ei.employee) FROM EmployeeInactivity ei JOIN ei.employee e WHERE e.user = :user " +
           "AND ei.startDate <= :currentDate AND (ei.endDate IS NULL OR ei.endDate >= :currentDate)")
    List<Long> countCurrentlyInactiveEmployees(
            @Param("user") User user,
            @Param("currentDate") LocalDate currentDate);
    
    // Find employees that have been inactive for more than X days
    @Query("SELECT ei FROM EmployeeInactivity ei JOIN ei.employee e WHERE e.user = :user " +
           "AND (ei.endDate IS NULL OR ei.endDate >= :currentDate) " +
           "AND (EXTRACT(EPOCH FROM (:currentDate - ei.startDate)) / 86400.0) > :days")
    List<EmployeeInactivity> findEmployeesInactiveForMoreThanXDays(
            @Param("user") User user, 
            @Param("days") int days,
            @Param("currentDate") LocalDate currentDate);
    
    // Find employees that will return from inactivity within the next X days
    @Query("SELECT ei FROM EmployeeInactivity ei JOIN ei.employee e WHERE e.user = :user " +
           "AND ei.endDate IS NOT NULL " +
           "AND ei.endDate > :currentDate " +
           "AND (EXTRACT(EPOCH FROM (ei.endDate - :currentDate)) / 86400.0) <= :days")
    List<EmployeeInactivity> findEmployeesReturningWithinXDays(
            @Param("user") User user, 
            @Param("days") int days,
            @Param("currentDate") LocalDate currentDate);
            
    // Find open inactivities for employee
    @Query("SELECT ei FROM EmployeeInactivity ei WHERE ei.employee.id = :employeeId " +
           "AND (ei.endDate IS NULL OR ei.endDate >= :currentDate) " +
           "AND ei.startDate <= :currentDate")
    List<EmployeeInactivity> findOpenInactivitiesForEmployee(
           @Param("employeeId") Long employeeId,
           @Param("currentDate") LocalDate currentDate);
    
    // Find inactivities for employee ordered by start date descending
    @Query("SELECT ei FROM EmployeeInactivity ei WHERE ei.employee.id = :employeeId ORDER BY ei.startDate DESC")
    List<EmployeeInactivity> findByEmployeeIdOrderByStartDateDesc(@Param("employeeId") Long employeeId);
}