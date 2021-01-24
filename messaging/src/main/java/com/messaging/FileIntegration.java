package com.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.JmsDestinationPollingSource;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.integration.transformer.support.MessageProcessingHeaderValueMessageProcessor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageChannel;

@Configuration
public class FileIntegration {

    @Bean
    IntegrationFlow collectNamesOfTooBigFilesToFile(MessageChannel mailChannel, JmsTemplate template, MessageProcessingHeaderValueMessageProcessor messageProcessor) {
        return IntegrationFlows.from(
                Jms.inboundAdapter(template)
                        .destination("too-big-size-files")
                        .messageSelector("priority > 5"), spec ->
                        spec.poller(Pollers.cron("0-10 * * * * *").maxMessagesPerPoll(100)))
                .log(LoggingHandler.Level.INFO, "Info", m -> "I received some file")
                .transform(messageProcessor)
                .aggregate(aggregatorSpec ->
                        aggregatorSpec
                                .releaseStrategy(ob -> ob.size() > 1)
                                .correlationStrategy(ob -> ob.getPayload().getClass().equals(String.class))
                .expireGroupsUponCompletion(true))
                .channel(mailChannel)
                .get();
    }

    @Bean
    MessageProcessingHeaderValueMessageProcessor messageProcessor() {
        return new MessageProcessingHeaderValueMessageProcessor(message -> message.getHeaders().get("file_name"));
    }

}
