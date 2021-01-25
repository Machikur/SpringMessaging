package com.messaging.mail;

import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class FileNamesToMessageTransformer implements GenericTransformer<Collection<String>,String> {

    @Override
    public String transform(Collection<String> source) {
        StringBuilder builder = new StringBuilder();
        int counter=1;
        builder.append("Oto lista plików których nie udało się zapisac w bazie danych\n");
        for (String s : source) {
            builder.append(counter++).append(". ").append(s).append("\n");
        }
        return builder.toString();
    }

}
