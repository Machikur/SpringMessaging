package com.messages.integration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

@Configuration
@ConfigurationProperties(prefix = "router.file")
public class RouterConfiguration {

    private double maxFileSizeInMb;

    public void setMaxFileSizeInMb(double maxFileSizeInMb) {
        this.maxFileSizeInMb = maxFileSizeInMb;
    }

    @Bean
    AbstractMessageRouter router(MessageChannel adminChannel, MessageChannel dataBaseChannel) {
        return new AbstractMessageRouter() {
            @Override
            protected Collection<MessageChannel> determineTargetChannels(Message<?> message) {
                File file = (File) message.getPayload();
                double fileSizeInMb = (double) file.length() / (1024 * 1024);
                if (fileSizeInMb > maxFileSizeInMb)
                    return Collections.singletonList(adminChannel);
                else {
                    return Collections.singletonList(dataBaseChannel);
                }
            }
        };
    }

    @Bean
    MessageChannel adminChannel() {
        return new DirectChannel();
    }

    @Bean
    MessageChannel dataBaseChannel() {
        return new DirectChannel();
    }

}
