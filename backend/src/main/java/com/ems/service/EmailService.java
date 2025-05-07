package com.ems.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ems.dto.MessageDto;
import com.ems.exception.EmailException;
import com.ems.model.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service for handling email operations
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private AuthService authService;
    
    @Value("${spring.mail.host:smtp.gmail.com}")
    private String smtpHost;
    
    @Value("${spring.mail.port:587}")
    private int smtpPort;
    
    @Value("${spring.mail.username:your-email@gmail.com}")
    private String mailUsername;
    
    @Value("${spring.mail.password:your-app-password}")
    private String mailPassword;
    
    /**
     * Send an email message to an employee using a MessageDto
     */
    public boolean sendEmail(MessageDto messageDto, String recipientEmail) {
        try {
            // Configure mail sender with system settings
            JavaMailSenderImpl mailSender = createSystemMailSender();
            
            // Send the email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            // Set message properties
            helper.setFrom("noreply@ems.com");
            helper.setTo(recipientEmail);
            helper.setSubject(messageDto.getSubject());
            helper.setText(messageDto.getContent(), true);
            
            mailSender.send(mimeMessage);
            logger.info("Email sent successfully to: {}", recipientEmail);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send email: {}", e.getMessage());
            throw new EmailException("Failed to send email: " + e.getMessage());
        }
    }
    
    /**
     * Send a simple email with subject and content
     */
    public boolean sendEmail(String recipientEmail, String subject, String content) {
        try {
            // Configure mail sender with system settings
            JavaMailSenderImpl mailSender = createSystemMailSender();
            
            // Send the email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom("noreply@ems.com");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(content, true); // true = HTML content
            
            mailSender.send(mimeMessage);
            logger.info("Email sent successfully to: {}", recipientEmail);
            return true;
            
        } catch (MessagingException e) {
            logger.error("Failed to send email: {}", e.getMessage());
            throw new EmailException("Failed to send email: " + e.getMessage());
        }
    }
    
    /**
     * Create system mail sender using application properties
     */
    private JavaMailSenderImpl createSystemMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        return mailSender;
    }
    
    /**
     * Create and configure JavaMailSender with custom credentials
     */
    private JavaMailSenderImpl createMailSender(String username, String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        return mailSender;
    }
    
    /**
     * Generate HTML email content from template
     */
    public String generateHtmlEmailContent(String templateName, MessageDto messageDto) {
        // For now, using simple HTML template
        // In a more complex implementation, we could use a templating engine like Thymeleaf
        
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html><html><head><style>");
        htmlBuilder.append("body { font-family: Arial, sans-serif; line-height: 1.6; }");
        htmlBuilder.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        htmlBuilder.append(".header { background-color: #4a86e8; color: white; padding: 10px; }");
        htmlBuilder.append(".content { padding: 20px; }");
        htmlBuilder.append(".footer { font-size: 12px; color: #666; padding: 10px; text-align: center; }");
        htmlBuilder.append("</style></head><body><div class='container'>");
        
        // Header
        htmlBuilder.append("<div class='header'><h2>").append(messageDto.getSubject()).append("</h2></div>");
        
        // Content
        htmlBuilder.append("<div class='content'>");
        
        // Replace newlines with <br> tags
        String content = messageDto.getContent().replace("\n", "<br>");
        htmlBuilder.append(content);
        
        htmlBuilder.append("</div>");
        
        // Footer
        htmlBuilder.append("<div class='footer'>");
        htmlBuilder.append("<p>This message was sent from your Employee Management System</p>");
        htmlBuilder.append("</div></div></body></html>");
        
        return htmlBuilder.toString();
    }
}