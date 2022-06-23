package com.zhans.ntsdserver.controller;

import com.zhans.ntsdserver.command.FileCommand;
import com.zhans.ntsdserver.command.FileInfoCommand;
import com.zhans.ntsdserver.dto.SocketRequest;
import com.zhans.ntsdserver.entity.File;
import com.zhans.ntsdserver.exception.FileUploadException;
import com.zhans.ntsdserver.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/files")
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private Map<String, FileCommand> commandResolver;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
        initCommandResolver();
    }

    @PostMapping("/upload")
    public ResponseEntity<File> upload(@RequestParam("file") MultipartFile file) {
        if (file.getOriginalFilename() == null) throw new FileUploadException("Failed to store file with empty name");
        File f = fileUploadService.upload(file);
        log.info("File saved: {}", f);
        return ResponseEntity.ok().body(f);
    }

    @MessageMapping("/ws")
    @SendTo("/topic/files")
    public File greeting(SocketRequest request) {
        return commandResolver.get(request.getCommand()).perform(request.getFilename());
    }

    private void initCommandResolver() {
        commandResolver = new HashMap<>();
        FileInfoCommand fileInfoCommand = new FileInfoCommand();
        fileInfoCommand.setFileUploadService(fileUploadService);
        commandResolver.put("fileInfo", fileInfoCommand);
    }
}
