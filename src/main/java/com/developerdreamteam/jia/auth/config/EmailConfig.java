package com.developerdreamteam.jia.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.mail.Session;
import jakarta.mail.Transport;


import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${email.smtp.host}")
    private String host;

    @Value("${email.smtp.port}")
    private int port;

    @Value("${email.smtp.auth}")
    private boolean auth;

    @Value("${email.smtp.starttls.enable}")
    private boolean starttlsEnable;

    @Value("${email.sender.address}")
    private String senderAddress;

    @Value("${email.sender.password}")
    private String senderPassword;

    @Bean
    public Session emailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);

        return Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication(senderAddress, senderPassword);
            }
        });
    }

    @Bean
    public Transport emailTransport(Session session) throws Exception {
        Transport transport = session.getTransport("smtp");
        transport.connect(host, senderAddress, senderPassword);
        return transport;
    }
}
