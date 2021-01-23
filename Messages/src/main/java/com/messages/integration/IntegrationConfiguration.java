package com.messages.integration;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jms.core.JmsTemplate;

import java.io.File;
import java.io.IOException;

@Configuration
public class IntegrationConfiguration {

    @Bean
    FileReadingMessageSource source(AcceptOnceTxtFileListFilter filter) {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File("data"));
        source.setFilter(filter);
        return source;
    }

    @Bean
    IntegrationFlow fileReader(FileReadingMessageSource source, JmsTemplate template) {
        return IntegrationFlows.from(source, spec -> spec.poller(p -> p.fixedRate(1000L)))
                .<File, byte[]>transform(file -> {
                    try {
                        return FileUtils.readFileToByteArray(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    throw new RuntimeException("Transform error");
                })
                .log(LoggingHandler.Level.INFO, "Information about Files", message -> "Files are sending to server")
                .handle(Jms.outboundAdapter(template)
                        .destination("txt-files"))
                .get();
    }

}
