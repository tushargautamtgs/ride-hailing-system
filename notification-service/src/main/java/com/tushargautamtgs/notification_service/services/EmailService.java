package com.tushargautamtgs.notification_service.services;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail,String username){

        log.info("==>> Preparing email for {} (username={})", toEmail, username);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Welcome to Ride Hailing Service");
        message.setText(
                "Hi "+ username + ",\n\n" +
                        "Welcome to our Ride Hailing Service! We're excited to have you on board.\n\n" +
                        "- The Ride Hailing Team"
        );

        mailSender.send(message);
        log.info("==>> Email sent successfully to {} (username={})", toEmail, username);
    }
}
