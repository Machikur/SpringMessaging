package com.messages.integration;

import com.messages.integration.data.FileData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class RouterConfiguration {

    private final FileData fileData;

    @Bean
    AbstractMessageRouter router(MessageChannel adminChannel, MessageChannel dataBaseChannel) {
        return new AbstractMessageRouter() {
            @Override
            protected Collection<MessageChannel> determineTargetChannels(Message<?> message) {
                File file = (File) message.getPayload();
                double fileSizeInMb = (double) file.length() / (1024 * 1024);
                if (fileSizeInMb > fileData.getMaxFileSizeInMb()) {
                    return Collections.singletonList(adminChannel);
                } else {
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
