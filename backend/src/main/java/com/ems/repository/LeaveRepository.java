package com.ems.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ems.model.Employee;
import com.ems.model.Leave;
import com.ems.model.User;
import com.ems.model.Leave.Status;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByEmployee(Employee employee);
    
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user ORDER BY l.requestDate DESC")
    List<Leave> findByUser(@Param("user") User user);
    
    @Query("SELECT l FROM Leave l WHERE l.id = :id AND l.employee.user = :user")
    Optional<Leave> findByIdAndUser(@Param("id") Long id, @Param("user") User user);
    
    @Query("SELECT l.status AS status, COUNT(l) AS count FROM Leave l WHERE l.employee.user = :user GROUP BY l.status")
    List<Object[]> countLeavesByStatus(@Param("user") User user);
    
    @Query("SELECT COUNT(l) FROM Leave l WHERE l.employee.user = :user AND l.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") Status status);
    
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user AND l.status = :status " +
           "AND ((l.startDate BETWEEN :startDate AND :endDate) OR " +
           "(l.endDate BETWEEN :startDate AND :endDate) OR " +
           "(:startDate BETWEEN l.startDate AND l.endDate))")
    List<Leave> findOverlappingLeaves(
            @Param("user") User user, 
            @Param("status") Status status, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT l FROM Leave l WHERE l.employee = :employee AND l.status = :status " +
           "AND CURRENT_DATE BETWEEN l.startDate AND l.endDate")
    List<Leave> findCurrentLeaves(@Param("employee") Employee employee, @Param("status") Status status);
    
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user AND l.status = :status " +
           "AND CURRENT_DATE BETWEEN l.startDate AND l.endDate")
    List<Leave> findCurrentLeavesByUser(@Param("user") User user, @Param("status") Status status);
}
