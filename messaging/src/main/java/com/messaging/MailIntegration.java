package com.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mail.dsl.Mail;

import java.io.File;

public class MailIntegration {

    @Value("${admin.mail}")
    private String adminMail;

    @Value("${MAIL_USERNAME}")
    private String userName;

    @Value("${MAIL_PASSWORD}")
    private String password;

    @Value("${file.directory}")
    private String directory;

    @Bean
    IntegrationFlow sendEmailToAdmin(FileReadingMessageSource source) {
        return IntegrationFlows.from(source, spec -> spec.poller(Pollers.fixedRate(1000L)))
                .log(LoggingHandler.Level.INFO, "Info", m -> "I'm sending email to admin")
                .transform(Files.toStringTransformer(true))
                .enrichHeaders(Mail.headers()
                        .to(adminMail)
                        .subject("notification")
                        .from("messagingApp"))
                .handle(Mail.outboundAdapter("smtp.gmail.com")
                        .javaMailProperties(propertiesBuilder -> propertiesBuilder
                                .put("mail.smtp.starttls.enable", "true")
                                .put("mail.smtp.auth", "true"))
                        .credentials(userName, password)
                        .port(587)
                        .protocol("smtp"))
                .get();
    }

    @Bean
    FileReadingMessageSource source() {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(directory));
        return source;
    }

}
