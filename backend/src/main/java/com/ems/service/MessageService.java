package com.ems.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ems.dto.MessageDto;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Employee;
import com.ems.model.Message;
import com.ems.model.User;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.MessageRepository;
import com.ems.util.EmailService;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private EmailService emailService;

    @Transactional(readOnly = true)
    public List<MessageDto> getAllMessagesForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        // Get all messages sent by this user or sent to employees in this user's organization
        List<Message> sentMessages = messageRepository.findBySenderOrderBySentAtDesc(currentUser);
        
        return sentMessages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesByEmployeeId(Long employeeId) {
        User currentUser = authService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(employeeId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        
        // Get messages sent to this specific employee
        List<Message> messages = messageRepository.findBySenderAndEmployee(currentUser, employee);
        
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDto sendMessage(MessageDto messageDto) {
        User currentUser = authService.getCurrentUser();
        
        // Validate employee
        Employee employee = employeeRepository.findByIdAndUser(messageDto.getEmployeeId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + messageDto.getEmployeeId()));
        
        Message message = new Message();
        message.setSender(currentUser);
        message.setEmployee(employee);
        message.setSubject(messageDto.getSubject());
        message.setContent(messageDto.getContent());
        
        // Try to send email
        try {
            emailService.sendEmail(
                currentUser.getEmail(),
                employee.getEmail(),
                messageDto.getSubject(),
                messageDto.getContent()
            );
            message.setStatus(Message.Status.SENT);
        } catch (Exception e) {
            message.setStatus(Message.Status.FAILED);
        }
        
        Message savedMessage = messageRepository.save(message);
        return convertToDto(savedMessage);
    }

    // Helper method to convert Entity to DTO
    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setEmployeeId(message.getEmployee().getId());
        dto.setEmployeeName(message.getEmployee().getName());
        dto.setSenderName(message.getSender().getFullName());
        dto.setSubject(message.getSubject());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setStatus(message.getStatus());
        return dto;
    }
}
