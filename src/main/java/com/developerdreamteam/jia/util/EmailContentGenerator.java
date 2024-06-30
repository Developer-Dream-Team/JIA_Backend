package com.developerdreamteam.jia.util;

public class EmailContentGenerator {

    public static String generateActivationEmailContent(String activationLink) {
        return "<html>"
                + "<body>"
                + "<p>Please click the button below to activate your account:</p>"
                + "<a href=\"" + activationLink + "\" style=\"display: inline-block; padding: 10px 20px; font-size: 16px; color: #ffffff; background-color: #007bff; text-align: center; text-decoration: none; border-radius: 5px;\">Click Here</a>"
                + "</body>"
                + "</html>";
    }
}
