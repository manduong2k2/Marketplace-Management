package com.Marketplace_Management.Shared.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Shared.Contracts.IFileService;

@Service
public class FileService implements IFileService {
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Override
    public String uploadFile(byte[] fileData, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        Path filePath = uploadPath.resolve(uniqueFileName);
        
        Files.write(filePath, fileData);
        
        return filePath.toString().replace("\\", "/");
    }
    
    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {
        Path uploadPath = Paths.get(uploadDir, path);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String fileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        Path filePath = uploadPath.resolve(uniqueFileName);
        
        Files.write(filePath, file.getBytes());
        
        return filePath.toString().replace("\\", "/");
    }
    
    @Override
    public void deleteFile(String fileUrl) throws IOException {
        Path filePath = Paths.get(fileUrl);
        
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}
