package com.ems.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ems.model.Deduction;
import com.ems.model.Salary;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {
    List<Deduction> findBySalary(Salary salary);
    void deleteBySalary(Salary salary);
}
