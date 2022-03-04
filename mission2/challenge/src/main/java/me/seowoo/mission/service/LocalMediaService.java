package me.seowoo.mission.service;

import me.seowoo.mission.model.MediaDescriptorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class LocalMediaService implements MediaService {

    private static final Logger logger = LoggerFactory.getLogger(LocalMediaService.class);
    private final String basePath = "./media";

    @Override
    public MediaDescriptorDto saveFile(MultipartFile file) {
        return this.saveToDir(file);
    }

    @Override
    public Collection<MediaDescriptorDto> saveFileBulk(MultipartFile[] files) {
        Collection<MediaDescriptorDto> resultList = new ArrayList<>();

        for (MultipartFile file : files) {
            resultList.add(this.saveToDir(file));
        }

        return resultList;
    }

    @Override
    public byte[] getFileAsBytes(String resourcePath) {
        try {
            return Files.readAllBytes(Path.of(basePath, resourcePath));
        } catch (IOException e) {
            logger.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public MediaDescriptorDto saveToDir(MultipartFile file) {
        MediaDescriptorDto dto = new MediaDescriptorDto();
        dto.setStatus(200);
        dto.setOriginalName(file.getOriginalFilename());

        try {
            LocalDateTime now = LocalDateTime.now();
            String targetDir = Path.of(basePath, now.format(DateTimeFormatter.BASIC_ISO_DATE)).toString();
            String newFileName = now.format(DateTimeFormatter.ofPattern("HHmmss"))
                    + "_"
                    + file.getOriginalFilename();
            File dirNow = new File(targetDir);

            if (!dirNow.exists()) {
                dirNow.mkdir();
            }

            file.transferTo(Path.of(targetDir, newFileName));
            dto.setResourcePath(Path.of(targetDir, newFileName).toString().substring(1));

            return dto;

        } catch (IOException e) {
            logger.error(e.getMessage());
            dto.setMessage("failed");
            dto.setStatus(500);

            return dto;
        }
    }
}
