package com.Marketplace_Management.Shared.Utils.Http;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public final class CookieUtils {
    private CookieUtils() {
    }

    /** Value of the named cookie, or null when the request does not carry it. */
    public static String read(HttpServletRequest request, String name) {
        if (request == null || request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
