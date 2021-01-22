package com.messages.integration.transformers;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;

@Component
public class FilePaths {

   private final File operationFile= new File("data/operationFile.txt");

    public File getOperationFile() {
        return operationFile;
    }

    @PreDestroy
    public void deleteFile() {
        operationFile.delete();
        operationFile.deleteOnExit();
    }
}
