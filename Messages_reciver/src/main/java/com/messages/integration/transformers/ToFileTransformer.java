package com.messages.integration.transformers;

import com.messages.integration.data.FileData;
import org.apache.commons.io.FileUtils;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import com.messages.integration.data.FileData;

@Component
public class ToFileTransformer implements GenericTransformer<byte[], File> {

    private final File operationFile;

    public ToFileTransformer(FileData fileData) {
        this.operationFile = fileData.getOperationFile();
    }

    @Override
    public File transform(byte[] source) {
        try {
            FileUtils.writeByteArrayToFile(operationFile, source);
            return operationFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
