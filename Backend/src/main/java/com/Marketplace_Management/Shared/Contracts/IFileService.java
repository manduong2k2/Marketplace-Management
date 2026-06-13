package com.Marketplace_Management.Shared.Contracts;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    String uploadFile(byte[] fileData, String fileName) throws IOException;
    String uploadFile(MultipartFile file, String path) throws IOException;
    void deleteFile(String fileUrl) throws IOException;
}
