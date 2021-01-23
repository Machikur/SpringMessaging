package com.messages.integration.transformers;

import com.messages.domain.FileDetails;
import com.messages.integration.data.FileData;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Component
public class ToFileDetailsTransformer implements GenericTransformer<File, FileDetails> {

    private final File operationFile;

    public ToFileDetailsTransformer(FileData fileData) {
        this.operationFile = fileData.getOperationFile();
    }

    @Override
    public FileDetails transform(File source) {
        return new FileDetails(operationFile.getName(), operationFile.getTotalSpace(), readFile(operationFile));
    }

    private String readFile(File file) {
        StringBuilder builder = new StringBuilder();
        String line = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();

    }


}
