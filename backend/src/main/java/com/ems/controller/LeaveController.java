package com.ems.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.dto.LeaveDto;
import com.ems.model.Leave;
import com.ems.service.LeaveService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @GetMapping
    public ResponseEntity<List<LeaveDto>> getAllLeaves() {
        List<LeaveDto> leaves = leaveService.getAllLeavesForCurrentUser();
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveDto> getLeaveById(@PathVariable Long id) {
        LeaveDto leave = leaveService.getLeaveById(id);
        return ResponseEntity.ok(leave);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveDto>> getLeavesByEmployeeId(@PathVariable Long employeeId) {
        List<LeaveDto> leaves = leaveService.getLeavesByEmployeeId(employeeId);
        return ResponseEntity.ok(leaves);
    }

    @PostMapping
    public ResponseEntity<LeaveDto> createLeave(@Valid @RequestBody LeaveDto leaveDto) {
        LeaveDto createdLeave = leaveService.createLeave(leaveDto);
        return ResponseEntity.ok(createdLeave);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LeaveDto> updateLeaveStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> status) {
        
        Leave.Status leaveStatus = Leave.Status.valueOf(status.get("status"));
        LeaveDto updatedLeave = leaveService.updateLeaveStatus(id, leaveStatus);
        return ResponseEntity.ok(updatedLeave);
    }
}
