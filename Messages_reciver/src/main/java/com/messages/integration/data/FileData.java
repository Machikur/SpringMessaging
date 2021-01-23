package com.messages.integration.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "router.file")
public class FileData {

    private double maxFileSizeInMb;

    private String filePath;

    private File operationFile;

    @PostConstruct
    private void setOperationFile() {
        this.operationFile = new File(filePath);
    }

    public int getFilePriority() {
        return FileSize.getFilePriority(operationFile, maxFileSizeInMb);
    }
}
