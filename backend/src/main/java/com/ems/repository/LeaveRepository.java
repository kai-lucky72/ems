package com.ems.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ems.model.Employee;
import com.ems.model.Leave;
import com.ems.model.User;
import com.ems.model.Leave.Status;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByEmployee(Employee employee);
    
    @Query("SELECT l FROM Leave l WHERE l.employee.user = ?1 ORDER BY l.createdAt DESC")
    List<Leave> findByUser(User user);
    
    @Query("SELECT l FROM Leave l WHERE l.id = ?1 AND l.employee.user = ?2")
    Optional<Leave> findByIdAndUser(Long id, User user);
    
    @Query("SELECT l.status AS status, COUNT(l) AS count FROM Leave l WHERE l.employee.user = ?1 GROUP BY l.status")
    List<Object[]> countLeavesByStatus(User user);
    
    @Query("SELECT COUNT(l) FROM Leave l WHERE l.employee.user = ?1 AND l.status = ?2")
    long countByUserAndStatus(User user, Status status);
}
