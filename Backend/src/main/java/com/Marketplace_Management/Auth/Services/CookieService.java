package com.Marketplace_Management.Auth.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.Marketplace_Management.Auth.Constants.Http;
import com.Marketplace_Management.Auth.Contracts.ICookieService;

@Service
public class CookieService implements ICookieService {

    @Value("${spring.application.auth.domain}")
    private String authDomain;

    @Value("${spring.application.auth.secure}")
    private boolean authSecure;

    @Value("${spring.application.auth.http-only}")
    private boolean authHttpOnly;

    @Value("${spring.application.auth.same-site}")
    private String sameSite;



    public HttpHeaders createAuthCookies(String accessToken, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie.from(Http.ACCESS_TOKEN_COOKIE, accessToken)
                .httpOnly(true)
                .secure(authSecure)
                .path("/")
                .maxAge(24 * 60 * 60)
                .domain(authDomain)
                .sameSite(sameSite)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(Http.REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(authSecure)
                .path("/")
                .domain(authDomain)
                .maxAge(7 * 24 * 60 * 60)
                .sameSite(sameSite)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return headers;
    }

    public HttpHeaders createClearCookies() {
        ResponseCookie clearAccessCookie = ResponseCookie.from(Http.ACCESS_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(authSecure)
                .path("/")
                .domain(authDomain)
                .maxAge(0)
                .sameSite(sameSite)
                .build();

        ResponseCookie clearRefreshCookie = ResponseCookie.from(Http.REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(authSecure)
                .path("/")
                .domain(authDomain)
                .maxAge(0)
                .sameSite(sameSite)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, clearAccessCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, clearRefreshCookie.toString());
        return headers;
    }
}
