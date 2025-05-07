package com.ems.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ems.model.Employee;
import com.ems.model.Message;
import com.ems.model.User;
import com.ems.model.Message.Status;

/**
 * Repository for message operations
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * Find messages by employee
     */
    List<Message> findByEmployee(Employee employee);
    
    /**
     * Find messages by employee sorted by send date
     */
    List<Message> findByEmployeeOrderBySentAtDesc(Employee employee);
    
    /**
     * Find message by ID and user
     */
    @Query("SELECT m FROM Message m WHERE m.id = :id AND m.sender = :user")
    Optional<Message> findByIdAndSender(@Param("id") Long id, @Param("user") User user);
    
    /**
     * Find messages sent by user
     */
    @Query("SELECT m FROM Message m WHERE m.sender = :user ORDER BY m.sentAt DESC")
    List<Message> findBySender(@Param("user") User user);
    
    /**
     * Find messages sent by user (paginated)
     */
    @Query("SELECT m FROM Message m WHERE m.sender = :user")
    Page<Message> findBySenderPaginated(@Param("user") User user, Pageable pageable);
    
    /**
     * Find unread messages for employee
     */
    @Query("SELECT m FROM Message m WHERE m.employee = :employee AND m.isRead = false ORDER BY m.sentAt DESC")
    List<Message> findUnreadByEmployee(@Param("employee") Employee employee);
    
    /**
     * Count unread messages for employee
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.employee = :employee AND m.isRead = false")
    List<Long> countUnreadByEmployee(@Param("employee") Employee employee);
    
    /**
     * Find messages by status
     */
    @Query("SELECT m FROM Message m WHERE m.sender = :user AND m.status = :status ORDER BY m.sentAt DESC")
    List<Message> findBySenderAndStatus(@Param("user") User user, @Param("status") Status status);
    
    /**
     * Count messages by status
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.sender = :user AND m.status = :status")
    List<Long> countBySenderAndStatus(@Param("user") User user, @Param("status") Status status);
    
    /**
     * Search messages by subject or content
     */
    @Query("SELECT m FROM Message m WHERE m.sender = :user AND " +
           "(LOWER(m.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY m.sentAt DESC")
    List<Message> searchMessages(@Param("user") User user, @Param("searchTerm") String searchTerm);
    
    /**
     * Search messages by employee name
     */
    @Query("SELECT m FROM Message m WHERE m.sender = :user AND " +
           "LOWER(m.employee.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY m.sentAt DESC")
    List<Message> searchMessagesByEmployeeName(@Param("user") User user, @Param("searchTerm") String searchTerm);
    
    /**
     * Find messages sent in date range
     */
    @Query("SELECT m FROM Message m WHERE m.sender = :user AND " +
           "m.sentAt BETWEEN :startDate AND :endDate " +
           "ORDER BY m.sentAt DESC")
    List<Message> findByDateRange(
            @Param("user") User user, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find messages by department
     */
    @Query("SELECT m FROM Message m WHERE m.sender = :user AND m.employee.department.id = :departmentId " +
           "ORDER BY m.sentAt DESC")
    List<Message> findByDepartment(@Param("user") User user, @Param("departmentId") Long departmentId);
    
    /**
     * Get message statistics by month
     */
    @Query("SELECT FUNCTION('YEAR', m.sentAt) as year, FUNCTION('MONTH', m.sentAt) as month, COUNT(m) as count " +
           "FROM Message m WHERE m.sender = :user " +
           "GROUP BY FUNCTION('YEAR', m.sentAt), FUNCTION('MONTH', m.sentAt) " +
           "ORDER BY FUNCTION('YEAR', m.sentAt) DESC, FUNCTION('MONTH', m.sentAt) DESC")
    List<Object[]> getMessageStatsByMonth(@Param("user") User user);
    
    /**
     * Get message counts by recipient department
     */
    @Query("SELECT e.department.name as department, COUNT(m) as count " +
           "FROM Message m JOIN m.employee e WHERE m.sender = :user " +
           "GROUP BY e.department.name")
    List<Object[]> getMessageCountsByDepartment(@Param("user") User user);
    
    /**
     * Get messages read rate
     */
    @Query("SELECT " +
           "SUM(CASE WHEN m.isRead = true THEN 1 ELSE 0 END) as readCount, " +
           "COUNT(m) as totalCount " +
           "FROM Message m WHERE m.sender = :user")
    List<Object[]> getMessagesReadRate(@Param("user") User user);
    
    /**
     * Find recent messages
     */
    @Query("SELECT m FROM Message m WHERE m.sender = :user " +
           "ORDER BY m.sentAt DESC")
    List<Message> findRecentMessages(@Param("user") User user, Pageable pageable);
}