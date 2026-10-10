package com.Marketplace_Management.Shared.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Shared.Contracts.IFileService;

/**
 * Stores uploads under file.upload-dir. Client-supplied names are never used as paths:
 * a stored file is named <random UUID>.<extension>, and only files inside the upload
 * directory can be deleted.
 */
@Service
public class FileService implements IFileService {
    private static final Pattern SAFE_EXTENSION = Pattern.compile("[a-z0-9]{1,10}");

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public String uploadFile(byte[] fileData, String fileName) throws IOException {
        return write(fileData, "", fileName);
    }

    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {
        return write(file.getBytes(), path, file.getOriginalFilename());
    }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }
        Path filePath = Paths.get(fileUrl).toAbsolutePath().normalize();
        if (filePath.startsWith(root()) && Files.isRegularFile(filePath)) {
            Files.delete(filePath);
        }
    }

    private String write(byte[] data, String folder, String originalName) throws IOException {
        // Same stored format as before: <upload-dir>/<folder>/<file>, e.g. "uploads/vendors/logo/<uuid>.png"
        // v4 on purpose: the name is part of a public URL and must not reveal when the file was uploaded
        Path filePath = Paths.get(uploadDir, folder == null ? "" : folder)
                .resolve(UUID.randomUUID() + extensionOf(originalName));
        if (!filePath.toAbsolutePath().normalize().startsWith(root())) {
            throw new IOException("Invalid upload folder: " + folder);
        }
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, data);
        return filePath.toString().replace("\\", "/");
    }

    private Path root() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /** ".png" for "photo.PNG"; nothing when the name has no (safe) extension. */
    private static String extensionOf(String fileName) {
        if (fileName == null) {
            return "";
        }
        String name = Paths.get(fileName.replace("\\", "/")).getFileName().toString();
        int dot = name.lastIndexOf('.');
        String extension = dot >= 0 ? name.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
        return SAFE_EXTENSION.matcher(extension).matches() ? "." + extension : "";
    }
}
