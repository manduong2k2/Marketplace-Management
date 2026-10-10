package com.Marketplace_Management.Shared.Utils.Helpers;

public final class UrlHelper {
    private UrlHelper() {
    }

    /**
     * Public URL of a stored file: "uploads/x.png" -> "{baseUrl}/uploads/x.png".
     * Null/blank and absolute URLs (e.g. a Google profile picture) are returned unchanged,
     * so applying it twice is harmless.
     */
    public static String toPublicUrl(String baseUrl, String path) {
        if (path == null || path.isBlank() || path.matches("(?i)^https?://.*")) {
            return path;
        }
        String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return base + "/" + (path.startsWith("/") ? path.substring(1) : path);
    }
}
