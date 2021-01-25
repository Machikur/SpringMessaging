package com.messaging.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.messaging.MessageChannel;

@Configuration
@RequiredArgsConstructor
public class MailIntegration {

    private final AdminDetails adminDetails;

    @Bean
    IntegrationFlow sendEmailToAdmin(MessageChannel mailChannel, FileNamesToMessageTransformer transformer) {
        return IntegrationFlows.from(mailChannel)
                .log(LoggingHandler.Level.INFO, "Info", m -> "I'm sending email to admin")
                .transform(transformer)
                .enrichHeaders(Mail.headers()
                        .to(adminDetails.getAdminMail())
                        .subject("notification")
                        .from("messagingApp"))
                .handle(Mail.outboundAdapter("smtp.gmail.com")
                        .javaMailProperties(propertiesBuilder -> propertiesBuilder
                                .put("mail.smtp.starttls.enable", "true")
                                .put("mail.smtp.auth", "true"))
                        .credentials(adminDetails.getUserName(), adminDetails.getPassword())
                        .port(587)
                        .protocol("smtp"))
                .get();
    }

    @Bean
    MessageChannel mailChannel() {
        return new DirectChannel();
    }

}
