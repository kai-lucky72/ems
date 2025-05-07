package com.ems.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.DepartmentDto;
import com.ems.exception.BadRequestException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Department;
import com.ems.model.User;
import com.ems.repository.DepartmentRepository;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartmentsForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<Department> departments = departmentRepository.findByUser(currentUser);
        
        return departments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentById(Long id) {
        User currentUser = authService.getCurrentUser();
        Department department = departmentRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        
        return convertToDto(department);
    }

    @Transactional
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        User currentUser = authService.getCurrentUser();
        
        // Check if department with same name already exists for the user
        if (departmentRepository.existsByNameAndUser(departmentDto.getName(), currentUser)) {
            throw new BadRequestException("Department with this name already exists");
        }
        
        Department department = new Department();
        department.setName(departmentDto.getName());
        department.setBudget(departmentDto.getBudget());
        department.setBudgetType(departmentDto.getBudgetType());
        department.setUser(currentUser);
        
        Department savedDepartment = departmentRepository.save(department);
        return convertToDto(savedDepartment);
    }

    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentDto departmentDto) {
        User currentUser = authService.getCurrentUser();
        Department department = departmentRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        
        // Check if department with same name already exists (excluding this one)
        if (!department.getName().equals(departmentDto.getName()) && 
            departmentRepository.existsByNameAndUser(departmentDto.getName(), currentUser)) {
            throw new BadRequestException("Department with this name already exists");
        }
        
        department.setName(departmentDto.getName());
        department.setBudget(departmentDto.getBudget());
        department.setBudgetType(departmentDto.getBudgetType());
        
        Department updatedDepartment = departmentRepository.save(department);
        return convertToDto(updatedDepartment);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        User currentUser = authService.getCurrentUser();
        Department department = departmentRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        
        // Check if department has employees
        if (!department.getEmployees().isEmpty()) {
            throw new BadRequestException("Cannot delete department with employees");
        }
        
        departmentRepository.delete(department);
    }

    // Helper method to convert Entity to DTO
    private DepartmentDto convertToDto(Department department) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setBudget(department.getBudget());
        dto.setBudgetType(department.getBudgetType());
        dto.setCurrentExpenses(department.calculateCurrentExpenses());
        return dto;
    }
}
