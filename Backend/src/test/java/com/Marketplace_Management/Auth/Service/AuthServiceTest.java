package com.Marketplace_Management.Auth.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Marketplace_Management.Auth.Constants.Message;
import com.Marketplace_Management.Auth.Contracts.IUserRepository;
import com.Marketplace_Management.Auth.DTOs.Commands.LoginCommand;
import com.Marketplace_Management.Auth.DTOs.Response.AuthResponse;
import com.Marketplace_Management.Auth.Models.User;
import com.Marketplace_Management.Auth.Services.AuthService;
import com.Marketplace_Management.Auth.Services.UserSessionService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    IUserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    // Token issuing (JWT + user_sessions row) is delegated to UserSessionService
    @Mock
    UserSessionService sessionService;

    @InjectMocks
    AuthService authService;

    @Test
    void testLogin_success() {

        // arrange
        String email = "test@gmail.com";
        String rawPassword = "123456";
        String encodedPassword = "encodedPassword";

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .status("ACTIVE")
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(true);

        when(sessionService.issue(user, Message.LOGIN_SUCCESS))
                .thenReturn(new AuthResponse("fake-token", "fake-refresh-token", Message.LOGIN_SUCCESS));

        // act
        AuthResponse response = authService.login(
                new LoginCommand(email, rawPassword)
        );

        // assert
        assertEquals("fake-token", response.getAccessToken());
        assertEquals("fake-refresh-token", response.getRefreshToken());
        verify(sessionService).issue(user, Message.LOGIN_SUCCESS);
    }
}
