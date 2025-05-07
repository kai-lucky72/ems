package com.ems.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ems.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByEmail(String email);
    
    List<Boolean> existsByEmail(String email);
    
    List<User> findByResetToken(String resetToken);
    
    List<Boolean> existsByResetToken(String resetToken);
    
    @Query("SELECT u FROM User u WHERE u.roles LIKE %:role%")
    List<User> findByRole(@Param("role") String role);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActive();
    
    @Query("SELECT u FROM User u WHERE u.isActive = false")
    List<User> findAllInactive();
    
    @Query("SELECT u FROM User u WHERE u.companyName = :companyName")
    List<User> findByCompanyName(@Param("companyName") String companyName);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.roles LIKE %:role%")
    List<Long> countByRole(@Param("role") String role);
}
