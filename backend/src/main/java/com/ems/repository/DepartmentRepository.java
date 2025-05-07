package com.ems.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ems.model.Department;
import com.ems.model.User;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByUser(User user);
    Optional<Department> findByIdAndUser(Long id, User user);
    boolean existsByNameAndUser(String name, User user);
}
