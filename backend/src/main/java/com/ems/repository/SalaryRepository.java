package com.ems.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ems.model.Employee;
import com.ems.model.Salary;
import com.ems.model.User;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Optional<Salary> findByEmployee(Employee employee);
    
    @Query("SELECT s FROM Salary s WHERE s.employee.user = ?1")
    List<Salary> findByUser(User user);
    
    @Query("SELECT s FROM Salary s WHERE s.id = ?1 AND s.employee.user = ?2")
    Optional<Salary> findByIdAndUser(Long id, User user);
    
    @Query("SELECT SUM(s.grossSalary) FROM Salary s WHERE s.employee.user = ?1 AND s.employee.isActive = true")
    Double sumGrossSalaryByUser(User user);
    
    @Query("SELECT SUM(s.netSalary) FROM Salary s WHERE s.employee.user = ?1 AND s.employee.isActive = true")
    Double sumNetSalaryByUser(User user);
    
    @Query("SELECT AVG(s.grossSalary) FROM Salary s WHERE s.employee.user = ?1 AND s.employee.isActive = true")
    Double averageGrossSalaryByUser(User user);
    
    @Query("SELECT d.name, SUM(s.grossSalary) FROM Salary s JOIN s.employee e JOIN e.department d WHERE e.user = ?1 AND e.isActive = true GROUP BY d.name")
    List<Object[]> sumSalaryByDepartment(User user);
}
