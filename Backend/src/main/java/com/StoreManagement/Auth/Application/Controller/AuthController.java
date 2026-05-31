package com.StoreManagement.Auth.Application.Controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.StoreManagement.Auth.Application.DTO.Commands.ActivateUserCommand;
import com.StoreManagement.Auth.Application.DTO.Commands.ForgotPasswordCommand;
import com.StoreManagement.Auth.Application.DTO.Commands.LoginCommand;
import com.StoreManagement.Auth.Application.DTO.Commands.RefreshTokenCommand;
import com.StoreManagement.Auth.Application.DTO.Commands.RegisterCommand;
import com.StoreManagement.Auth.Application.DTO.Commands.ResetPasswordCommand;
import com.StoreManagement.Auth.Application.DTO.Commands.UpdateProfileCommand;
import com.StoreManagement.Auth.Application.DTO.Request.ActivateUserRequest;
import com.StoreManagement.Auth.Application.DTO.Request.ForgotPasswordRequest;
import com.StoreManagement.Auth.Application.DTO.Request.LoginRequest;
import com.StoreManagement.Auth.Application.DTO.Request.RefreshTokenRequest;
import com.StoreManagement.Auth.Application.DTO.Request.RegisterRequest;
import com.StoreManagement.Auth.Application.DTO.Request.ResetPasswordRequest;
import com.StoreManagement.Auth.Application.DTO.Request.UpdateProfileRequest;
import com.StoreManagement.Auth.Application.DTO.Response.AuthResponse;
import com.StoreManagement.Auth.Application.DTO.Response.ProfileResponse;
import com.StoreManagement.Auth.Application.DTO.Response.RegisterResponse;
import com.StoreManagement.Auth.Domain.Constants.Http;
import com.StoreManagement.Auth.Domain.Constants.Message;
import com.StoreManagement.Auth.Domain.Contract.IAuthService;
import com.StoreManagement.Auth.Domain.Contract.ICookieService;
import com.StoreManagement.Auth.Domain.Models.User;
import com.StoreManagement.Shared.Application.Annotation.Auth.Authenticated;
import com.StoreManagement.Shared.Infrastructure.Security.JwtService;
import com.StoreManagement.Shared.Infrastructure.Security.SecurityUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IAuthService auth;

    @Autowired
    private ICookieService cookieService;

    @Value("${spring.application.auth-domain}")
    private String authDomain;
    
    @Value("${application.frontend.base-url}")
    private String frontendBaseUrl;

    @PostMapping("/register")
    public RegisterResponse register(@Valid @ModelAttribute RegisterRequest req)
            throws MessagingException {
        RegisterCommand command = RegisterCommand.fromRequest(req);
        return auth.register(command);
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> login(@Valid @RequestBody(required = true) LoginRequest req) {
        LoginCommand command = LoginCommand.fromRequest(req);
        AuthResponse authRes = auth.login(command);
        HttpHeaders cookies = cookieService.createAuthCookies(authRes.getAccessToken(), authRes.getRefreshToken());

        HashMap<String, Object> response = new HashMap<>();
        response.put("message", authRes.getMessage());

        return ResponseEntity.ok()
                .headers(cookies)
                .body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<HashMap<String, Object>> refreshToken(
            @Valid @RequestBody(required = true) RefreshTokenRequest req) {
        RefreshTokenCommand command = RefreshTokenCommand.fromRequest(req);
        var authRes = auth.refreshToken(command);
        HttpHeaders cookies = cookieService.createAuthCookies(authRes.getAccessToken(), authRes.getRefreshToken());

        HashMap<String, Object> response = new HashMap<>();
        response.put("message", authRes.getMessage());

        return ResponseEntity.ok()
                .headers(cookies)
                .body(response);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<HashMap<String, Object>> activeUser(@Valid @ModelAttribute ActivateUserRequest request) {
        ActivateUserCommand command = ActivateUserCommand.fromRequest(request);
        var authRes = auth.activeUser(command);
        HttpHeaders headers = cookieService.createAuthCookies(authRes.getAccessToken(), authRes.getRefreshToken());

        headers.setLocation(
                URI.create(frontendBaseUrl + "/home"));

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .headers(headers)
                .build();
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @RequestBody(required = true) ForgotPasswordRequest req)
            throws MessagingException {
        ForgotPasswordCommand command = ForgotPasswordCommand.fromRequest(req);
        boolean sent = auth.sendResetPasswordEmail(command);
        if (sent) {
            return Message.RECOVERY_MAIL_SENT;
        } else {
            return Message.RECOVERY_MAIL_FAILED;
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@Valid @RequestBody(required = true) ResetPasswordRequest req) {
        ResetPasswordCommand command = ResetPasswordCommand.fromRequest(req);
        boolean reset = auth.resetPassword(command);
        if (reset) {
            return Message.PASSWORD_UPDATED;
        } else {
            return Message.PASSWORD_RESET_FAILED;
        }
    }

    @Authenticated
    @GetMapping("/profile")
    public ResponseEntity<HashMap<String, Object>> profile() {
        UUID userId = SecurityUtils.currentUserId();
        User user = auth.getUserById(userId);
        HashMap<String, Object> response = new HashMap<>();
        response.put("data", new ProfileResponse(user));
        return ResponseEntity.ok(response);
    }

    @Authenticated
    @PutMapping("/profile")
    public ResponseEntity<HashMap<String, Object>> updateProfile(@Valid @RequestBody UpdateProfileRequest req) {
        UUID userId = SecurityUtils.currentUserId();
        UpdateProfileCommand command = UpdateProfileCommand.fromRequest(req);
        auth.updateProfile(userId, command);

        HashMap<String, Object> response = new HashMap<>();
        response.put("message", Message.PROFILE_UPDATED);
        return ResponseEntity.ok(response);
    }

    @Authenticated
    @PostMapping("/logout")
    public ResponseEntity<HashMap<String, Object>> logout(HttpServletRequest request) {
        HashMap<String, String> cookieMap = new HashMap<>();

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                cookieMap.put(cookie.getName(), cookie.getValue());
            }
        }

        String accessToken = cookieMap.get(Http.ACCESS_TOKEN_COOKIE);
        String refreshToken = cookieMap.get(Http.REFRESH_TOKEN_COOKIE);

        jwtService.invalidateToken(accessToken);
        jwtService.invalidateToken(refreshToken);

        HttpHeaders headers = cookieService.createClearCookies();

        HashMap<String, Object> response = new HashMap<>();
        response.put("message", Message.LOGOUT_SUCCESS);

        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }
}
