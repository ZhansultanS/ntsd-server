package com.zhans.ntsdserver.command;

import com.zhans.ntsdserver.entity.File;
import com.zhans.ntsdserver.service.FileUploadService;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FileInfoCommand implements FileCommand {

    private FileUploadService fileUploadService;

    @Override
    public File perform(String filename) {
        return fileUploadService.getFileByName(filename);
    }

    public void setFileUploadService(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }
}
