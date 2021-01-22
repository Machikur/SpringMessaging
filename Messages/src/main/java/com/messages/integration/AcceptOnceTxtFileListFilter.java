package com.messages.integration;

import org.apache.commons.io.FilenameUtils;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AcceptOnceTxtFileListFilter extends AcceptOnceFileListFilter<File> {

    @Override
    public boolean accept(File file) {
        return super.accept(file) && FilenameUtils.getExtension(file.getName()).equals("txt");
    }

}
