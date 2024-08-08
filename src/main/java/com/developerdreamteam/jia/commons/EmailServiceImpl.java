package com.developerdreamteam.jia.commons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private final Session emailSession;

    private final Transport emailTransport;

    @Value("${email.sender.address}")
    private String senderAddress;

    public EmailServiceImpl(Session emailSession, Transport emailTransport) {
        this.emailSession = emailSession;
        this.emailTransport = emailTransport;
    }

    public void sendSimpleMessage(String to, String subject, String text) throws Exception {
        MimeMessage message = new MimeMessage(emailSession);
        message.setFrom(new InternetAddress(senderAddress));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(text, "text/html; charset=utf-8");

        emailTransport.sendMessage(message, message.getAllRecipients());
    }
}
