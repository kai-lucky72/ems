package com.ems.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.ems.model.Message.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for Message entities
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {

    private Long id;

    // Sender information (populated from User when retrieved)
    private String senderName;
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    // Recipient information (populated from Employee when retrieved)
    private String employeeName;
    private String employeeEmail;
    private String departmentName;
    private Long departmentId;

    @NotBlank(message = "Subject is required")
    @Size(max = 255, message = "Subject must not exceed 255 characters")
    private String subject;

    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;
    private String sentAtFormatted;
    
    private boolean isRead;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readAt;
    private String readAtFormatted;
    
    private Status status;
    private String statusDisplayName;
    
    // For email sending
    private String emailBody;
    private boolean sendEmail = true;

    /**
     * Default constructor
     */
    public MessageDto() {
    }
    
    /**
     * Constructor with essential fields
     */
    public MessageDto(Long employeeId, String subject, String content) {
        this.employeeId = employeeId;
        this.subject = subject;
        this.content = content;
    }
    
    /**
     * Copy constructor
     */
    public MessageDto(MessageDto source) {
        this.id = source.id;
        this.senderName = source.senderName;
        this.employeeId = source.employeeId;
        this.employeeName = source.employeeName;
        this.employeeEmail = source.employeeEmail;
        this.departmentName = source.departmentName;
        this.departmentId = source.departmentId;
        this.subject = source.subject;
        this.content = source.content;
        this.sentAt = source.sentAt;
        this.sentAtFormatted = source.sentAtFormatted;
        this.isRead = source.isRead;
        this.readAt = source.readAt;
        this.readAtFormatted = source.readAtFormatted;
        this.status = source.status;
        this.statusDisplayName = source.statusDisplayName;
        this.emailBody = source.emailBody;
        this.sendEmail = source.sendEmail;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
        
        if (sentAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.sentAtFormatted = sentAt.format(formatter);
        }
    }

    public String getSentAtFormatted() {
        return sentAtFormatted;
    }

    public void setSentAtFormatted(String sentAtFormatted) {
        this.sentAtFormatted = sentAtFormatted;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
        
        if (readAt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.readAtFormatted = readAt.format(formatter);
        }
    }

    public String getReadAtFormatted() {
        return readAtFormatted;
    }

    public void setReadAtFormatted(String readAtFormatted) {
        this.readAtFormatted = readAtFormatted;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        
        if (status != null) {
            this.statusDisplayName = switch (status) {
                case SENT -> "Sent";
                case FAILED -> "Failed";
                case DELIVERED -> "Delivered";
            };
        }
    }

    public String getStatusDisplayName() {
        return statusDisplayName;
    }

    public void setStatusDisplayName(String statusDisplayName) {
        this.statusDisplayName = statusDisplayName;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }
}