package com.messages.integration;

import com.messages.domain.FileDetails;
import com.messages.integration.data.FileData;
import com.messages.integration.data.FileSize;
import com.messages.integration.exception.TransformationException;
import com.messages.integration.transformers.ToFileDetailsTransformer;
import com.messages.integration.transformers.ToFileTransformer;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.ConsumerEndpointSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.integration.jpa.dsl.Jpa;
import org.springframework.integration.jpa.support.PersistMode;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageChannel;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;

@Configuration
public class IntegrationConfiguration {

    @Bean
    IntegrationFlow jmsListener(ToFileTransformer transformer, AbstractMessageRouter router, JmsTemplate template) {
        return IntegrationFlows.from(Jms.inboundAdapter(template)
                .destination("txt-files"), spec -> spec.poller(p -> p.fixedRate(1000L)))
                .transform(transformer)
                .log(LoggingHandler.Level.INFO, "Info", message -> "I received message from: " + message.getHeaders().get("jms_destination"))
                .route(router)
                .get();
    }

    @Bean
    IntegrationFlow adminNotificator(MessageChannel adminChannel, JmsTemplate template, FileData fileData) {
        return IntegrationFlows.from(adminChannel)
                .log(LoggingHandler.Level.INFO, "Info", message -> "I'm sending file to administrator")
                .<File, byte[]>transform(file -> {
                    try {
                        return FileUtils.readFileToByteArray(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    throw new TransformationException("Transform error");
                })
                .enrichHeaders(spec -> spec.priority(fileData.getFilePriority(),true))
                .handle(Jms.outboundAdapter(template)
                        .destination("too-big-size-files"))
                .get();
    }

    @Bean
    IntegrationFlow dataBaseSaver(MessageChannel dataBaseChannel, EntityManagerFactory factory, ToFileDetailsTransformer transformer) {
        return IntegrationFlows.from(dataBaseChannel)
                .log(LoggingHandler.Level.INFO, "Info", message -> "I'm saving file to data base")
                .transform(transformer)
                .handle(Jpa.outboundAdapter(factory)
                        .entityClass(FileDetails.class)
                        .persistMode(PersistMode.PERSIST), ConsumerEndpointSpec::transactional)
                .get();
    }

}
