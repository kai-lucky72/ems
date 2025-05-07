package com.ems.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ems.dto.MessageDto;
import com.ems.model.Message.Status;
import com.ems.service.MessageService;

import jakarta.validation.Valid;

/**
 * REST controller for message operations
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    /**
     * Get all messages
     */
    @GetMapping
    public ResponseEntity<List<MessageDto>> getAllMessages() {
        List<MessageDto> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }
    
    /**
     * Get paginated messages
     */
    @GetMapping("/paged")
    public ResponseEntity<Page<MessageDto>> getPaginatedMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sentAt") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending) {
        
        Page<MessageDto> messages = messageService.getPaginatedMessages(page, size, sortBy, ascending);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * Get message by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable Long id) {
        MessageDto message = messageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }
    
    /**
     * Get messages by employee
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<MessageDto>> getMessagesByEmployee(@PathVariable Long employeeId) {
        List<MessageDto> messages = messageService.getMessagesByEmployee(employeeId);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * Get messages by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MessageDto>> getMessagesByStatus(@PathVariable Status status) {
        List<MessageDto> messages = messageService.getMessagesByStatus(status);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * Search messages
     */
    @GetMapping("/search")
    public ResponseEntity<List<MessageDto>> searchMessages(@RequestParam String term) {
        List<MessageDto> messages = messageService.searchMessages(term);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * Search messages by employee name
     */
    @GetMapping("/search/employee")
    public ResponseEntity<List<MessageDto>> searchMessagesByEmployeeName(@RequestParam String name) {
        List<MessageDto> messages = messageService.searchMessagesByEmployeeName(name);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * Get messages by department
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<MessageDto>> getMessagesByDepartment(@PathVariable Long departmentId) {
        List<MessageDto> messages = messageService.getMessagesByDepartment(departmentId);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * Get message statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getMessageStatistics() {
        Map<String, Object> statistics = messageService.getMessageStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Send a message
     */
    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(@Valid @RequestBody MessageDto messageDto) {
        MessageDto sentMessage = messageService.sendMessage(messageDto);
        return new ResponseEntity<>(sentMessage, HttpStatus.CREATED);
    }
    
    /**
     * Mark message as read
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<MessageDto> markMessageAsRead(@PathVariable Long id) {
        MessageDto updatedMessage = messageService.markMessageAsRead(id);
        return ResponseEntity.ok(updatedMessage);
    }
}