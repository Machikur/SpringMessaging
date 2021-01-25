package com.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.integration.store.MessageGroup;
import org.springframework.integration.transformer.support.MessageProcessingHeaderValueMessageProcessor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Objects;

@Configuration
public class FileIntegration {

    @Bean
    IntegrationFlow collectNamesOfTooBigFilesToFile(MessageChannel mailChannel, JmsTemplate template, MessageProcessingHeaderValueMessageProcessor messageProcessor) {
        return IntegrationFlows.from(
                Jms.inboundAdapter(template)
                        .destination("too-big-size-files")
                        .messageSelector("priority > 5"), spec ->
                        spec.poller(Pollers.cron("0 0 * * * *").maxMessagesPerPoll(100)))
                .log(LoggingHandler.Level.INFO, "Info", m -> "I received some file")
                .transform(messageProcessor)
                .aggregate(aggregatorSpec -> aggregatorSpec
                        .releaseStrategy(this::checkIfTheOldestMessageIsOlderThanOneMinute)
                        .correlationStrategy(ob -> ob.getPayload().getClass().equals(String.class))
                        .expireGroupsUponCompletion(true))
                .channel(mailChannel)
                .get();
    }

    @Bean
    MessageProcessingHeaderValueMessageProcessor messageProcessor() {
        return new MessageProcessingHeaderValueMessageProcessor(message -> message.getHeaders().get("file_name"));
    }

    private Comparator<? super Message<?>> timeComparator() {
        return Comparator.comparingInt((Message<?> message) -> getTimeFromHeaders(message).getHour())
                .thenComparing(message -> getTimeFromHeaders(message).getMinute());
    }

    private LocalTime getTimeFromHeaders(Message<?> message) {
        Timestamp time = new Timestamp((long) Objects.requireNonNull(message.getHeaders().get("jms_timestamp")));
        return time.toLocalDateTime().toLocalTime();
    }

    private boolean checkIfTheOldestMessageIsOlderThanOneMinute(MessageGroup group) {
        LocalTime oldestMessageTime = getTimeFromHeaders(Objects.requireNonNull(
                group.getMessages().stream()
                        .min(timeComparator())
                        .orElse(null))
        );
        return oldestMessageTime.plusMinutes(1).compareTo(LocalTime.now()) < 0;
    }

}
