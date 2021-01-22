package com.messages.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class FileDetails {

    @Id
    @GeneratedValue
    private long id;
    private String fileName;
    private Long fileSize;
    private String message;

    public FileDetails(String fileName, Long fileSize, String message) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.message = message;
    }

    public FileDetails() {
    }

}
