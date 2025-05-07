package com.ems.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ems.model.Employee;
import com.ems.model.Message;
import com.ems.model.User;
import com.ems.model.Message.Status;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Find messages sent to a specific employee (recipient)
    List<Message> findByEmployee(Employee employee);
    
    // Find messages sent by a specific sender
    @Query("SELECT m FROM Message m WHERE m.sender = :sender ORDER BY m.sentAt DESC")
    List<Message> findBySender(@Param("sender") User sender);
    
    // Find messages sent by a specific user to employees they manage
    @Query("SELECT m FROM Message m WHERE m.sender = :sender AND m.employee.user = :sender ORDER BY m.sentAt DESC")
    List<Message> findBySenderToOwnEmployees(@Param("sender") User sender);
    
    // Find messages sent to employees of a specific user (organization) - inbox
    @Query("SELECT m FROM Message m WHERE m.employee.user = :user ORDER BY m.sentAt DESC")
    List<Message> findMessagesSentToUserEmployees(@Param("user") User user);
    
    // Find messages by status
    List<Message> findByStatus(Status status);
    
    // Find messages between a specific sender and employee
    @Query("SELECT m FROM Message m WHERE (m.sender = :user AND m.employee = :employee) OR " +
           "(m.sender.id = :employee.user.id AND m.employee.id = :employee.id) ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("user") User user, @Param("employee") Employee employee);
    
    // Count unread messages for a user's employees
    @Query("SELECT COUNT(m) FROM Message m WHERE m.employee.user = :user AND m.isRead = false")
    long countUnreadMessages(@Param("user") User user);
    
    // Find latest message for each conversation
    @Query("SELECT m FROM Message m WHERE m.id IN " +
           "(SELECT MAX(m2.id) FROM Message m2 WHERE (m2.sender = :user OR m2.employee.user = :user) " +
           "GROUP BY CASE WHEN m2.sender = :user THEN m2.employee.id ELSE m2.sender.id END)")
    List<Message> findLatestMessages(@Param("user") User user);
}
