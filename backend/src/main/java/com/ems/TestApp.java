package com.ems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.ems", 
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.ems\\.repository\\..*Repository")
    }
)
@EnableJpaRepositories(basePackages = "com.ems.repository",
    includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
            com.ems.repository.UserRepository.class,
            // Add other repositories that are known to work
        })
    },
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
            com.ems.repository.EmployeeInactivityRepository.class,
            com.ems.repository.EmployeeRepository.class,
            com.ems.repository.LeaveRepository.class,
            com.ems.repository.MessageRepository.class,
            com.ems.repository.SalaryRepository.class,
            com.ems.repository.DepartmentRepository.class,
            com.ems.repository.DeductionRepository.class
        })
    }
)
@EntityScan(basePackages = "com.ems.model")
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }
}