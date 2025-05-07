package com.ems.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ems.model.Employee;
import com.ems.model.EmployeeInactivity;
import com.ems.model.User;

@Repository
public interface EmployeeInactivityRepository extends JpaRepository<EmployeeInactivity, Long> {
    List<EmployeeInactivity> findByEmployee(Employee employee);
    
    @Query("SELECT ei FROM EmployeeInactivity ei WHERE ei.employee.user = ?1")
    List<EmployeeInactivity> findByUser(User user);
    
    @Query("SELECT ei FROM EmployeeInactivity ei WHERE ei.employee.user = ?1 AND " +
           "((ei.startDate <= ?2 AND (ei.endDate IS NULL OR ei.endDate >= ?2)) OR " +
           "(ei.startDate <= ?3 AND (ei.endDate IS NULL OR ei.endDate >= ?3)) OR " +
           "(ei.startDate >= ?2 AND (ei.endDate IS NULL OR ei.endDate <= ?3)))")
    List<EmployeeInactivity> findActiveInactivitiesInDateRange(User user, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT COUNT(ei) FROM EmployeeInactivity ei WHERE ei.employee.user = ?1 AND " +
           "(ei.endDate IS NULL OR ei.endDate >= CURRENT_DATE)")
    long countCurrentInactivities(User user);
}