package com.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.integration.transformer.support.MessageProcessingHeaderValueMessageProcessor;
import org.springframework.jms.core.JmsTemplate;

import java.io.File;

@Configuration
public class FileIntegration {

    @Value("${file.directory}")
    private String directory;

    @Bean
    IntegrationFlow collectNamesOfTooBigFilesToFile(JmsTemplate template, MessageProcessingHeaderValueMessageProcessor messageProcessor) {
        return IntegrationFlows.from(
                Jms.inboundGateway(template.getConnectionFactory())
                        .destination("too-big-size-files")
                        .configureListenerContainer(config -> config.messageSelector("priority > 5")))
                .log(LoggingHandler.Level.INFO, "Info", m -> "I received some file")
                .transform(messageProcessor)
                .handle(Files.outboundAdapter(new File(directory))
                        .appendNewLine(true)
                        .autoCreateDirectory(true)
                        .fileExistsMode(FileExistsMode.APPEND)
                        .charset("UTF-8")
                        .fileNameGenerator(message -> "names.txt"))
                .get();
    }

    @Bean
    MessageProcessingHeaderValueMessageProcessor messageProcessor() {
        return new MessageProcessingHeaderValueMessageProcessor(message -> message.getHeaders().get("file_name"));
    }

}
