package com.ems.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.User;
import com.ems.model.Employee.ContractType;
import com.ems.model.Employee.Status;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByUser(User user);
    
    Optional<Employee> findByIdAndUser(Long id, User user);
    
    List<Employee> findByDepartment(Department department);
    
    List<Employee> findByUserAndStatus(User user, Status status);
    
    @Query("SELECT e FROM Employee e WHERE e.email = :email")
    Optional<Employee> findByEmail(@Param("email") String email);
    
    @Query("SELECT e FROM Employee e WHERE e.user = :user ORDER BY e.name ASC")
    List<Employee> findByUserOrderByNameAsc(@Param("user") User user);
    
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.status = :status ORDER BY e.name ASC")
    List<Employee> findByUserAndStatusOrderByNameAsc(@Param("user") User user, @Param("status") Status status);
    
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.status = 'ACTIVE' " +
           "AND EXISTS (SELECT l FROM Leave l WHERE l.employee = e AND l.status = 'APPROVED' " +
           "AND :date BETWEEN l.startDate AND l.endDate)")
    List<Employee> findEmployeesOnLeaveByDate(@Param("user") User user, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.user = :user AND e.status = :status " +
           "AND (:date IS NULL OR e.createdAt <= :date)")
    long countByUserAndStatusAndDate(@Param("user") User user, @Param("status") Status status, 
                                    @Param("date") LocalDate date);
    
    @Query("SELECT e.role AS role, COUNT(e) AS count FROM Employee e WHERE e.user = :user GROUP BY e.role")
    List<Object[]> countEmployeesByRole(@Param("user") User user);
    
    @Query("SELECT e.contractType AS contractType, COUNT(e) AS count FROM Employee e WHERE e.user = :user GROUP BY e.contractType")
    List<Object[]> countEmployeesByContractType(@Param("user") User user);
    
    @Query("SELECT FUNCTION('YEAR', e.startDate) as year, FUNCTION('MONTH', e.startDate) as month, COUNT(e) as count " +
          "FROM Employee e WHERE e.user = :user GROUP BY FUNCTION('YEAR', e.startDate), FUNCTION('MONTH', e.startDate) " +
          "ORDER BY FUNCTION('YEAR', e.startDate) ASC, FUNCTION('MONTH', e.startDate) ASC")
    List<Object[]> countEmployeesByStartDate(@Param("user") User user);
    
    @Query("SELECT e FROM Employee e WHERE e.user = :user AND e.contractType = :contractType")
    List<Employee> findByUserAndContractType(@Param("user") User user, @Param("contractType") ContractType contractType);
    
    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId AND e.status = 'ACTIVE'")
    List<Employee> findActiveEmployeesByDepartmentId(@Param("departmentId") Long departmentId);
    
    @Query("SELECT e FROM Employee e WHERE e.user.id = :userId AND " + 
          "(e.status = 'ACTIVE') AND " +
          "EXISTS (SELECT i FROM EmployeeInactivity i WHERE i.employee = e AND i.endDate IS NULL)")
    List<Employee> findCurrentlyInactiveEmployees(@Param("userId") Long userId);
}
