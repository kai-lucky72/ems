package com.ems.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ems.model.Deduction;
import com.ems.model.Deduction.DeductionType;
import com.ems.model.Salary;
import com.ems.model.User;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {
    List<Deduction> findBySalary(Salary salary);
    
    List<Deduction> findBySalaryAndType(Salary salary, DeductionType type);
    
    @Query("SELECT d FROM Deduction d WHERE d.salary.employee.user = :user")
    List<Deduction> findByUser(@Param("user") User user);
    
    @Query("SELECT d FROM Deduction d WHERE d.salary.id = :salaryId")
    List<Deduction> findBySalaryId(@Param("salaryId") Long salaryId);
    
    @Query("SELECT d FROM Deduction d WHERE d.salary.employee.id = :employeeId")
    List<Deduction> findByEmployeeId(@Param("employeeId") Long employeeId);
    
    @Query("SELECT COUNT(d) FROM Deduction d WHERE d.type = :type AND d.salary.employee.user = :user")
    long countByTypeAndUser(@Param("type") DeductionType type, @Param("user") User user);
    
    @Transactional
    void deleteBySalary(Salary salary);
    
    @Transactional
    @Query("DELETE FROM Deduction d WHERE d.salary.id = :salaryId")
    void deleteBySalaryId(@Param("salaryId") Long salaryId);
}
