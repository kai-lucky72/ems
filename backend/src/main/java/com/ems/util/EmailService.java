package com.ems.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.enabled:false}")
    private boolean mailEnabled;

    /**
     * Sends an email from the manager to the employee
     * 
     * @param fromEmail manager's email address
     * @param toEmail employee's email address
     * @param subject email subject
     * @param content email content
     * @throws RuntimeException if email sending fails
     */
    public void sendEmail(String fromEmail, String toEmail, String subject, String content) {
        if (!mailEnabled) {
            // Mail is disabled in configuration, just log and return
            System.out.println("Email sending is disabled. Would have sent email to: " + toEmail);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email to " + toEmail, e);
        }
    }
}
