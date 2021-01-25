package com.messaging.mail;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AdminDetails {

    @Value("${admin.mail}")
    private String adminMail;

    @Value("${MAIL_USERNAME}")
    private String userName;

    @Value("${MAIL_PASSWORD}")
    private String password;

}
