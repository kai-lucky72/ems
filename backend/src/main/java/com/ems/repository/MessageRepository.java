package com.ems.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ems.model.Employee;
import com.ems.model.Message;
import com.ems.model.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Find messages sent to a specific employee
    List<Message> findByEmployee(Employee employee);
    
    // Find messages sent by a specific user
    List<Message> findBySender(User sender);
    
    // Find messages sent by a user to any of their employees
    @Query("SELECT m FROM Message m WHERE m.sender = ?1 ORDER BY m.sentAt DESC")
    List<Message> findBySenderOrderBySentAtDesc(User sender);
    
    // Find messages sent to employees of a specific user (organization)
    @Query("SELECT m FROM Message m WHERE m.employee.user = ?1 ORDER BY m.sentAt DESC")
    List<Message> findByOrganizationOrderBySentAtDesc(User organizationUser);
    
    // Find messages between a specific sender and employee
    @Query("SELECT m FROM Message m WHERE m.sender = ?1 AND m.employee = ?2 ORDER BY m.sentAt DESC")
    List<Message> findBySenderAndEmployee(User sender, Employee employee);
}
