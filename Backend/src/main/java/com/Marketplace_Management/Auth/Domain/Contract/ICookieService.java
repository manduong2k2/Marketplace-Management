package com.Marketplace_Management.Auth.Domain.Contract;

import org.springframework.http.HttpHeaders;

public interface ICookieService {
    HttpHeaders createAuthCookies(String accessToken, String refreshToken);
    
    HttpHeaders createClearCookies();
}
