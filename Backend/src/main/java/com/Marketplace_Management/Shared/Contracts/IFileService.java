package com.Marketplace_Management.Shared.Contracts;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    String uploadFile(byte[] fileData, String fileName) throws IOException;
    String uploadFile(MultipartFile file, String path) throws IOException;
    void deleteFile(String fileUrl) throws IOException;

    /**
     * Uploads newFile (when one was sent) in place of currentUrl and deletes the replaced file.
     * Returns the path to store: the new one, or currentUrl when nothing was uploaded.
     */
    default String replaceFile(MultipartFile newFile, String currentUrl, String path) throws IOException {
        if (newFile == null || newFile.isEmpty()) {
            return currentUrl;
        }
        String uploaded = uploadFile(newFile, path);
        if (currentUrl != null && !currentUrl.isBlank()) {
            deleteFile(currentUrl);
        }
        return uploaded;
    }
}
