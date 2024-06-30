package com.developerdreamteam.jia.helper;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// IMPORTANT: REMOVE THIS CLASS BEFORE DEPLOYMENT
@Component
public class EmailPropertiesPrinter {

    private static final Logger logger = LoggerFactory.getLogger(EmailPropertiesPrinter.class);

    @Value("${email.sender.address}")
    private String emailSenderAddress;

    @Value("${email.sender.password}")
    private String emailSenderPassword;

    @PostConstruct
    public void printEmailProperties() {
        logger.info("Email Sender Address: " + emailSenderAddress);
        logger.info("Email Sender Password: " + emailSenderPassword);
    }
}
