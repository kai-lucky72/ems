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
import com.ems.model.Leave;
import com.ems.model.User;
import com.ems.model.Leave.Status;

/**
 * Repository for leave request management
 */
@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    /**
     * Find leaves by employee
     */
    List<Leave> findByEmployee(Employee employee);
    
    /**
     * Find leaves by employee ordered by request date
     */
    List<Leave> findByEmployeeOrderByRequestDateDesc(Employee employee);
    
    /**
     * Count leaves by employee and status
     */
    long countByEmployeeAndStatus(Employee employee, Status status);
    
    /**
     * Find all leaves for a user's company ordered by request date
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user ORDER BY l.requestDate DESC")
    List<Leave> findByUser(@Param("user") User user);
    
    /**
     * Find a specific leave by ID and user
     */
    @Query("SELECT l FROM Leave l WHERE l.id = :id AND l.employee.user = :user")
    Optional<Leave> findByIdAndUser(@Param("id") Long id, @Param("user") User user);
    
    /**
     * Get paginated leaves for a user's company
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user")
    Page<Leave> findByUserPaginated(@Param("user") User user, Pageable pageable);
    
    /**
     * Get leave requests grouped by status
     */
    @Query("SELECT l.status AS status, COUNT(l) AS count FROM Leave l WHERE l.employee.user = :user GROUP BY l.status")
    List<Object[]> countLeavesByStatus(@Param("user") User user);
    
    /**
     * Count leaves by user and status
     */
    @Query("SELECT COUNT(l) FROM Leave l WHERE l.employee.user = :user AND l.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") Status status);
    
    /**
     * Find overlapping leave requests
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user AND l.status = :status " +
           "AND ((l.startDate BETWEEN :startDate AND :endDate) OR " +
           "(l.endDate BETWEEN :startDate AND :endDate) OR " +
           "(:startDate BETWEEN l.startDate AND l.endDate))")
    List<Leave> findOverlappingLeaves(
            @Param("user") User user, 
            @Param("status") Status status, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    /**
     * Find overlapping leave requests for a specific employee
     */
    @Query("SELECT l FROM Leave l WHERE l.employee = :employee AND l.status = :status " +
           "AND ((l.startDate BETWEEN :startDate AND :endDate) OR " +
           "(l.endDate BETWEEN :startDate AND :endDate) OR " +
           "(:startDate BETWEEN l.startDate AND l.endDate))")
    List<Leave> findOverlappingLeavesForEmployee(
            @Param("employee") Employee employee, 
            @Param("status") Status status, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    /**
     * Find current active leave for an employee
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId AND l.status = 'APPROVED' " +
           "AND :date BETWEEN l.startDate AND l.endDate")
    Optional<Leave> findCurrentLeaveForEmployee(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);
    
    /**
     * Find current active leaves for a specific employee
     */
    @Query("SELECT l FROM Leave l WHERE l.employee = :employee AND l.status = :status " +
           "AND CURRENT_DATE BETWEEN l.startDate AND l.endDate")
    List<Leave> findCurrentLeaves(@Param("employee") Employee employee, @Param("status") Status status);
    
    /**
     * Find current active leaves for all employees in a user's company
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user AND l.status = :status " +
           "AND CURRENT_DATE BETWEEN l.startDate AND l.endDate")
    List<Leave> findCurrentLeavesByUser(@Param("user") User user, @Param("status") Status status);
    
    /**
     * Find pending leave requests
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user AND l.status = 'PENDING' ORDER BY l.requestDate ASC")
    List<Leave> findPendingLeaves(@Param("user") User user);
    
    /**
     * Find leaves by status
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user AND l.status = :status ORDER BY l.requestDate DESC")
    List<Leave> findByUserAndStatus(@Param("user") User user, @Param("status") Status status);
    
    /**
     * Find upcoming leaves (approved but not yet started)
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user AND l.status = 'APPROVED' " +
           "AND l.startDate > CURRENT_DATE ORDER BY l.startDate ASC")
    List<Leave> findUpcomingLeaves(@Param("user") User user);
    
    /**
     * Find recent leaves by date range
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user " +
           "AND l.startDate BETWEEN :startDate AND :endDate ORDER BY l.startDate DESC")
    List<Leave> findRecentLeavesByDateRange(
            @Param("user") User user, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    /**
     * Find leaves by department
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.department = :department ORDER BY l.requestDate DESC")
    List<Leave> findByDepartment(@Param("department") Department department);
    
    /**
     * Count leaves by department and status
     */
    @Query("SELECT d.name as departmentName, COUNT(l) as leaveCount " +
           "FROM Leave l JOIN l.employee e JOIN e.department d " +
           "WHERE e.user = :user AND l.status = :status GROUP BY d.name")
    List<Object[]> countLeavesByDepartmentAndStatus(
            @Param("user") User user, 
            @Param("status") Status status);
    
    /**
     * Get leave trends by month
     */
    @Query("SELECT FUNCTION('YEAR', l.startDate) as year, FUNCTION('MONTH', l.startDate) as month, COUNT(l) as count " +
           "FROM Leave l WHERE l.employee.user = :user AND l.status = :status " +
           "GROUP BY FUNCTION('YEAR', l.startDate), FUNCTION('MONTH', l.startDate) " +
           "ORDER BY FUNCTION('YEAR', l.startDate) DESC, FUNCTION('MONTH', l.startDate) DESC")
    List<Object[]> getLeaveTrendsByMonth(@Param("user") User user, @Param("status") Status status);
    
    /**
     * Find leaves that start within the next X days
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user AND l.status = 'APPROVED' " +
           "AND l.startDate BETWEEN CURRENT_DATE AND :futureDate ORDER BY l.startDate ASC")
    List<Leave> findLeavesStartingSoon(@Param("user") User user, @Param("futureDate") LocalDate futureDate);
    
    /**
     * Find leaves that end within the next X days
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user AND l.status = 'APPROVED' " +
           "AND l.endDate BETWEEN CURRENT_DATE AND :futureDate ORDER BY l.endDate ASC")
    List<Leave> findLeavesEndingSoon(@Param("user") User user, @Param("futureDate") LocalDate futureDate);
    
    /**
     * Search leaves by employee name
     */
    @Query("SELECT l FROM Leave l WHERE l.employee.user = :user " +
           "AND LOWER(l.employee.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY l.requestDate DESC")
    List<Leave> searchLeavesByEmployeeName(@Param("user") User user, @Param("searchTerm") String searchTerm);
}
