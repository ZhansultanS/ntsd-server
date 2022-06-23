package com.zhans.ntsdserver.service;

import com.zhans.ntsdserver.exception.DateParseException;
import com.zhans.ntsdserver.exception.FileNotFoundException;
import com.zhans.ntsdserver.repository.FileRepository;
import com.zhans.ntsdserver.exception.FileUploadException;
import com.zhans.ntsdserver.entity.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileRepository fileRepository;
    @Value("${app.upload.path}")
    private String FILE_STORAGE_PATH;


    public File upload(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String pathToSave = this.FILE_STORAGE_PATH+filename;
        Date fileDate = parseDate(filename);

        saveFileInLocalFileStorage(file, pathToSave);

        if (fileRepository.existsByFileName(filename))
            return fileRepository.findByFileName(filename).get();

        File f = File.builder().fileName(filename).fileDate(fileDate).filePath(pathToSave).build();
        return fileRepository.save(f);
    }

    private void saveFileInLocalFileStorage(MultipartFile file, String path) {
        if (file.isEmpty()) {
            throw new FileUploadException("Failed to store empty file");
        }

        try {
            String fileName = file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();
            Files.copy(inputStream, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
            log.info("File {} saved under path {}", fileName, path);
        } catch (IOException e) {
            var msg = String.format("Failed to store file %s", file.getOriginalFilename());
            throw new FileUploadException(msg, e);
        }
    }

    private Date parseDate(String filename) {
        Pattern pattern = Pattern.compile("(?<date>\\d{8})_(?<time>\\d{6}).*");
        Matcher matcher = pattern.matcher(filename);

        if (!matcher.find()) throw new DateParseException("File name in wrong format");

        String dateGroup = matcher.group("date");
        String timeGroup = matcher.group("time");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < timeGroup.length(); i++) {
            if (i != 0 && i % 2 == 0) builder.append(":");
            builder.append(timeGroup.charAt(i));
        }
        String modifiedDateAndTime = String.join(" ", dateGroup, builder.toString());

        Date date;
        try {
            date = new SimpleDateFormat("ddMMyyyy HH:mm:ss").parse(modifiedDateAndTime);
        } catch (ParseException e) {
            throw new DateParseException("Unable to parse date from file name", e);
        }
        return date;
    }

    public File getFileByName(String filename) {
        return fileRepository
                .findByFileName(filename)
                .orElseThrow(() -> new FileNotFoundException(String.format("File with name=%s not found", filename)));
    }

}
