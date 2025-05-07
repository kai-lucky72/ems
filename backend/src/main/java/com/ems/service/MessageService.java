package com.ems.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.MessageDto;
import com.ems.exception.EmailException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Employee;
import com.ems.model.Message;
import com.ems.model.User;
import com.ems.model.Message.Status;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.MessageRepository;

/**
 * Service for message operations
 */
@Service
public class MessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Get all messages sent by the current user
     */
    @Transactional(readOnly = true)
    public List<MessageDto> getAllMessages() {
        User currentUser = authService.getCurrentUser();
        List<Message> messages = messageRepository.findBySender(currentUser);
        
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get paginated messages
     */
    @Transactional(readOnly = true)
    public Page<MessageDto> getPaginatedMessages(int page, int size, String sortBy, boolean ascending) {
        User currentUser = authService.getCurrentUser();
        Sort sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Message> messagePage = messageRepository.findBySenderPaginated(currentUser, pageable);
        
        return messagePage.map(this::convertToDto);
    }
    
    /**
     * Get message by ID
     */
    @Transactional(readOnly = true)
    public MessageDto getMessageById(Long id) {
        User currentUser = authService.getCurrentUser();
        Message message = messageRepository.findByIdAndSender(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        
        return convertToDto(message);
    }
    
    /**
     * Get messages for a specific employee
     */
    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesByEmployee(Long employeeId) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(employeeId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        List<Message> messages = messageRepository.findByEmployeeOrderBySentAtDesc(employee);
        
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get messages by status
     */
    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesByStatus(Status status) {
        User currentUser = authService.getCurrentUser();
        List<Message> messages = messageRepository.findBySenderAndStatus(currentUser, status);
        
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search messages by subject or content
     */
    @Transactional(readOnly = true)
    public List<MessageDto> searchMessages(String searchTerm) {
        User currentUser = authService.getCurrentUser();
        List<Message> messages = messageRepository.searchMessages(currentUser, searchTerm);
        
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search messages by employee name
     */
    @Transactional(readOnly = true)
    public List<MessageDto> searchMessagesByEmployeeName(String searchTerm) {
        User currentUser = authService.getCurrentUser();
        List<Message> messages = messageRepository.searchMessagesByEmployeeName(currentUser, searchTerm);
        
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get messages by department
     */
    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesByDepartment(Long departmentId) {
        User currentUser = authService.getCurrentUser();
        List<Message> messages = messageRepository.findByDepartment(currentUser, departmentId);
        
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get message statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMessageStatistics() {
        User currentUser = authService.getCurrentUser();
        Map<String, Object> statistics = new HashMap<>();
        
        // Count messages by status
        List<Long> sentCountList = messageRepository.countBySenderAndStatus(currentUser, Status.SENT);
        long sentCount = sentCountList.isEmpty() ? 0L : sentCountList.get(0);
        
        List<Long> deliveredCountList = messageRepository.countBySenderAndStatus(currentUser, Status.DELIVERED);
        long deliveredCount = deliveredCountList.isEmpty() ? 0L : deliveredCountList.get(0);
        
        List<Long> failedCountList = messageRepository.countBySenderAndStatus(currentUser, Status.FAILED);
        long failedCount = failedCountList.isEmpty() ? 0L : failedCountList.get(0);
        
        Map<String, Long> statusCounts = new HashMap<>();
        statusCounts.put("sent", sentCount);
        statusCounts.put("delivered", deliveredCount);
        statusCounts.put("failed", failedCount);
        
        statistics.put("messagesByStatus", statusCounts);
        statistics.put("totalMessages", sentCount + deliveredCount + failedCount);
        
        // Monthly statistics
        List<Object[]> monthlyStats = messageRepository.getMessageStatsByMonth(currentUser);
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        
        for (Object[] result : monthlyStats) {
            Map<String, Object> monthData = new HashMap<>();
            int year = ((Number) result[0]).intValue();
            int month = ((Number) result[1]).intValue();
            Long count = (Long) result[2];
            
            String monthName = LocalDateTime.of(year, month, 1, 0, 0).format(monthFormatter);
            
            monthData.put("month", monthName);
            monthData.put("count", count);
            monthlyData.add(monthData);
        }
        
        statistics.put("monthlyStats", monthlyData);
        
        // Department statistics
        List<Object[]> departmentStats = messageRepository.getMessageCountsByDepartment(currentUser);
        List<Map<String, Object>> departmentData = new ArrayList<>();
        
        for (Object[] result : departmentStats) {
            Map<String, Object> deptData = new HashMap<>();
            deptData.put("department", result[0]);
            deptData.put("count", result[1]);
            departmentData.add(deptData);
        }
        
        statistics.put("departmentStats", departmentData);
        
        // Read rate
        List<Object[]> readRateDataList = messageRepository.getMessagesReadRate(currentUser);
        long readCount = 0;
        long totalCount = 0;
        
        if (readRateDataList != null && !readRateDataList.isEmpty()) {
            Object[] readRateData = readRateDataList.get(0);
            if (readRateData != null && readRateData.length == 2) {
                readCount = readRateData[0] != null ? ((Number) readRateData[0]).longValue() : 0;
                totalCount = readRateData[1] != null ? ((Number) readRateData[1]).longValue() : 0;
            }
        }
        
        double readRate = totalCount > 0 ? (double) readCount / totalCount * 100 : 0;
        statistics.put("readRate", readRate);
        
        return statistics;
    }
    
    /**
     * Send a message to an employee
     */
    @Transactional
    public MessageDto sendMessage(MessageDto messageDto) {
        User currentUser = authService.getCurrentUser();
        
        // Validate employee exists
        Employee employee = employeeRepository.findByIdAndUser(messageDto.getEmployeeId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + messageDto.getEmployeeId()));
        
        // Create message entity
        Message message = new Message();
        message.setSender(currentUser);
        message.setEmployee(employee);
        message.setSubject(messageDto.getSubject());
        message.setContent(messageDto.getContent());
        message.setStatus(Status.SENT);
        
        Message savedMessage = messageRepository.save(message);
        MessageDto resultDto = convertToDto(savedMessage);
        
        // Send email if enabled
        if (messageDto.isSendEmail() && employee.getEmail() != null && !employee.getEmail().isEmpty()) {
            try {
                // Generate HTML email content if not provided
                if (messageDto.getEmailBody() == null || messageDto.getEmailBody().isEmpty()) {
                    String emailContent = emailService.generateHtmlEmailContent("message", messageDto);
                    messageDto.setEmailBody(emailContent);
                }
                
                boolean emailSent = emailService.sendEmail(messageDto, employee.getEmail());
                
                if (emailSent) {
                    // Update message status to DELIVERED
                    savedMessage.markAsDelivered();
                    messageRepository.save(savedMessage);
                    resultDto.setStatus(Status.DELIVERED);
                }
            } catch (EmailException e) {
                logger.error("Failed to send email: {}", e.getMessage());
                // Message is still saved but status remains SENT
                resultDto.setStatus(Status.SENT);
            }
        }
        
        return resultDto;
    }
    
    /**
     * Update message read status
     */
    @Transactional
    public MessageDto markMessageAsRead(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        
        if (!message.isRead()) {
            message.markAsRead();
            messageRepository.save(message);
        }
        
        return convertToDto(message);
    }
    
    /**
     * Convert Message entity to MessageDto
     */
    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        
        // Sender info
        if (message.getSender() != null) {
            dto.setSenderName(message.getSender().getFullName());
        }
        
        // Employee info
        if (message.getEmployee() != null) {
            dto.setEmployeeId(message.getEmployee().getId());
            dto.setEmployeeName(message.getEmployee().getName());
            dto.setEmployeeEmail(message.getEmployee().getEmail());
            
            // Department info if available
            if (message.getEmployee().getDepartment() != null) {
                dto.setDepartmentId(message.getEmployee().getDepartment().getId());
                dto.setDepartmentName(message.getEmployee().getDepartment().getName());
            }
        }
        
        dto.setSubject(message.getSubject());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setRead(message.isRead());
        dto.setReadAt(message.getReadAt());
        dto.setStatus(message.getStatus());
        
        return dto;
    }
}