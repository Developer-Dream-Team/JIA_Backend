package com.developerdreamteam.jia.commons;

public interface EmailService {

    void sendSimpleMessage(String to, String subject, String text) throws Exception;
}
