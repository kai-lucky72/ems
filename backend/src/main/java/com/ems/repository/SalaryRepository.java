package com.ems.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ems.model.Employee;
import com.ems.model.Salary;
import com.ems.model.User;
import com.ems.model.Employee.Status;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployee(Employee employee);
    
    Optional<Salary> findByEmployeeAndSalaryMonthAndSalaryYear(Employee employee, Integer month, Integer year);
    
    @Query("SELECT s FROM Salary s WHERE s.employee.user = ?1 ORDER BY s.salaryYear DESC, s.salaryMonth DESC")
    List<Salary> findByUser(User user);
    
    @Query("SELECT s FROM Salary s WHERE s.id = ?1 AND s.employee.user = ?2")
    Optional<Salary> findByIdAndUser(Long id, User user);
    
    @Query("SELECT s FROM Salary s WHERE s.employee.id = ?1 AND s.employee.user = ?2 ORDER BY s.salaryYear DESC, s.salaryMonth DESC")
    List<Salary> findByEmployeeIdAndUser(Long employeeId, User user);
    
    @Query("SELECT SUM(s.grossSalary) FROM Salary s WHERE s.employee.user = ?1 AND s.employee.status = ?2 AND s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) AND s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE)")
    Double sumGrossSalaryByUserForCurrentMonth(User user, Status status);
    
    @Query("SELECT SUM(s.netSalary) FROM Salary s WHERE s.employee.user = ?1 AND s.employee.status = ?2 AND s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) AND s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE)")
    Double sumNetSalaryByUserForCurrentMonth(User user, Status status);
    
    @Query("SELECT AVG(s.grossSalary) FROM Salary s WHERE s.employee.user = ?1 AND s.employee.status = ?2 AND s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) AND s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE)")
    Double averageGrossSalaryByUserForCurrentMonth(User user, Status status);
    
    @Query("SELECT d.name, SUM(s.grossSalary) FROM Salary s JOIN s.employee e JOIN e.department d " +
           "WHERE e.user = ?1 AND e.status = ?2 AND s.salaryYear = EXTRACT(YEAR FROM CURRENT_DATE) " +
           "AND s.salaryMonth = EXTRACT(MONTH FROM CURRENT_DATE) GROUP BY d.name")
    List<Object[]> sumSalaryByDepartmentForCurrentMonth(User user, Status status);
    
    @Query("SELECT DISTINCT s.salaryYear, s.salaryMonth FROM Salary s WHERE s.employee.user = ?1 ORDER BY s.salaryYear DESC, s.salaryMonth DESC")
    List<Object[]> findDistinctSalaryMonthsAndYears(User user);
    
    @Query("SELECT s FROM Salary s WHERE s.employee.user = ?1 AND s.salaryYear = ?2 AND s.salaryMonth = ?3")
    List<Salary> findByUserAndYearAndMonth(User user, Integer year, Integer month);
}
