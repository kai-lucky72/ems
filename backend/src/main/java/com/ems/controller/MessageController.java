package com.ems.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.dto.MessageDto;
import com.ems.service.MessageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageDto>> getAllMessages() {
        List<MessageDto> messages = messageService.getAllMessagesForCurrentUser();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<MessageDto>> getMessagesByEmployeeId(@PathVariable Long employeeId) {
        List<MessageDto> messages = messageService.getMessagesByEmployeeId(employeeId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(@Valid @RequestBody MessageDto messageDto) {
        MessageDto sentMessage = messageService.sendMessage(messageDto);
        return ResponseEntity.ok(sentMessage);
    }
}
