package com.messages.integration.data;

import java.io.File;

public enum FileSize {
    SMALL(1), MEDIUM(5), BIG(10);

    private final int size;

    FileSize(int size) {
        this.size = size;
    }

    public static int getFilePriority(File file, double preferredFileSize) {
        double fileLength = file.length();
        if (fileLength > preferredFileSize * 2) {
            return FileSize.BIG.size;
        }
        if (preferredFileSize > preferredFileSize * 1.5) {
            return FileSize.MEDIUM.size;
        } else return FileSize.SMALL.size;
    }

}
