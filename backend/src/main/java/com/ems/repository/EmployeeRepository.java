package com.ems.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.User;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByUser(User user);
    Optional<Employee> findByIdAndUser(Long id, User user);
    List<Employee> findByDepartment(Department department);
    List<Employee> findByUserAndIsActive(User user, boolean isActive);
    
    @Query("SELECT e FROM Employee e WHERE e.user = ?1 AND e.isActive = true AND EXISTS (SELECT l FROM Leave l WHERE l.employee = e AND l.status = 'APPROVED' AND ?2 BETWEEN l.startDate AND l.endDate)")
    List<Employee> findEmployeesOnLeaveByDate(User user, LocalDate date);
    
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.user = ?1 AND e.isActive = ?2 AND (?3 IS NULL OR e.createdAt <= ?3)")
    long countByUserAndActiveStatusAndDate(User user, boolean isActive, LocalDate date);
    
    @Query("SELECT e.role AS role, COUNT(e) AS count FROM Employee e WHERE e.user = ?1 GROUP BY e.role")
    List<Object[]> countEmployeesByRole(User user);
    
    @Query("SELECT e.contractType AS contractType, COUNT(e) AS count FROM Employee e WHERE e.user = ?1 GROUP BY e.contractType")
    List<Object[]> countEmployeesByContractType(User user);
}
