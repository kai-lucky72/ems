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
    List<Message> findByEmployee(Employee employee);
    
    @Query("SELECT m FROM Message m WHERE m.employee.user = ?1 ORDER BY m.sentAt DESC")
    List<Message> findByUser(User user);
}
