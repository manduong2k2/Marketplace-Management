package com.Marketplace_Management.Shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;
import com.Marketplace_Management.Shared.Services.FileService;
import com.Marketplace_Management.Shared.Utils.Helpers.UrlHelper;

class SharedUtilsTest {

    @Test
    void toPublicUrl_prefixesStoredPaths_only() {
        assertEquals("http://api/uploads/a.png", UrlHelper.toPublicUrl("http://api", "uploads/a.png"));
        assertEquals("http://api/uploads/a.png", UrlHelper.toPublicUrl("http://api/", "/uploads/a.png"));
        // Absolute URLs (Google / Facebook pictures) and empty values are left alone, so it is idempotent
        assertEquals("https://lh3.googleusercontent.com/x", UrlHelper.toPublicUrl("http://api", "https://lh3.googleusercontent.com/x"));
        assertEquals("http://api/uploads/a.png", UrlHelper.toPublicUrl("http://api", UrlHelper.toPublicUrl("http://api", "uploads/a.png")));
        assertNull(UrlHelper.toPublicUrl("http://api", null));
    }

    @Test
    void paginatedResponse_map_keepsPaging() {
        PaginatedResponse<Integer> page = PaginatedResponse.of(List.of(1, 2), 1, 2, 5);

        PaginatedResponse<String> mapped = page.map(i -> "#" + i);

        assertEquals(List.of("#1", "#2"), mapped.getData());
        assertEquals(1, mapped.getCurrentPage());
        assertEquals(3, mapped.getTotalPages());
        assertTrue(mapped.isHasNext());
        assertTrue(mapped.isHasPrevious());
    }

    @Test
    void fileService_neverUsesTheClientFileNameAsAPath(@TempDir Path uploads) throws Exception {
        FileService fileService = new FileService();
        ReflectionTestUtils.setField(fileService, "uploadDir", uploads.toString());

        MockMultipartFile evil = new MockMultipartFile("file", "../../evil.PNG", "image/png", new byte[] { 1, 2, 3 });
        String stored = fileService.uploadFile(evil, "users/avatars");

        Path storedPath = Path.of(stored).toAbsolutePath().normalize();
        assertTrue(storedPath.startsWith(uploads.resolve("users/avatars").toAbsolutePath().normalize()));
        assertTrue(stored.endsWith(".png"));
        assertFalse(stored.contains(".."));
        assertTrue(Files.exists(storedPath));
    }

    @Test
    void fileService_deletesOnlyInsideTheUploadDirectory(@TempDir Path root) throws Exception {
        Path uploads = Files.createDirectory(root.resolve("uploads"));
        Path outside = Files.writeString(root.resolve("secret.txt"), "keep me");
        FileService fileService = new FileService();
        ReflectionTestUtils.setField(fileService, "uploadDir", uploads.toString());

        fileService.deleteFile(uploads.resolve("../secret.txt").toString());

        assertTrue(Files.exists(outside));
    }
}
