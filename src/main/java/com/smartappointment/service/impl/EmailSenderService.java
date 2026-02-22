package com.smartappointment.service.impl;

import com.smartappointment.service.interfaces.ISender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class EmailSenderService implements ISender {

    private final JavaMailSender mailSender;

    /** Maps to MAIL_USERNAME env variable via Spring relaxed binding. */
    @Value("${spring.mail.username}")
    private String fromAddress;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a plain-text email to the given recipient.
     *
     * @param to      recipient email address
     * @param subject email subject line
     * @param body    email body (plain text)
     * @throws RuntimeException if the underlying mail transport reports an error
     */
    @Override
    public void send(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false); // false = plain text; switch to true for HTML

            mailSender.send(message);
            log.info("Email sent: to={}, subject={}", to, subject);

        } catch (MessagingException | MailException e) {
            log.error("Failed to send email: to={}, subject={}, error={}", to, subject, e.getMessage(), e);
            throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
        }
    }
}
