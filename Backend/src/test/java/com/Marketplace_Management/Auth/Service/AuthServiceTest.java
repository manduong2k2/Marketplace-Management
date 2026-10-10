package com.Marketplace_Management.Auth.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Marketplace_Management.Auth.Constants.Message;
import com.Marketplace_Management.Auth.Contracts.IUserRepository;
import com.Marketplace_Management.Auth.DTOs.Commands.ChangePasswordCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.LoginCommand;
import com.Marketplace_Management.Shared.Errors.Exceptions.BadRequestException;
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

    @Test
    void changePassword_success_revokesAllSessionsThenIssuesANewOne() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).password("old-hash").status("ACTIVE").build();
        AuthResponse issued = new AuthResponse("new-access", "new-refresh", Message.PASSWORD_UPDATED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "old-hash")).thenReturn(true);
        when(passwordEncoder.matches("new-secret", "old-hash")).thenReturn(false);
        when(passwordEncoder.encode("new-secret")).thenReturn("new-hash");
        when(userRepository.save(user)).thenReturn(user);
        when(sessionService.issue(user, Message.PASSWORD_UPDATED)).thenReturn(issued);

        AuthResponse response = authService.changePassword(userId, ChangePasswordCommand.builder()
                .currentPassword("old").newPassword("new-secret").build());

        assertEquals(issued, response);
        assertEquals("new-hash", user.getPassword());
        verify(sessionService).revokeAll(List.of(userId));
    }

    @Test
    void changePassword_wrongCurrentPassword_isRejected() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).password("old-hash").status("ACTIVE").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "old-hash")).thenReturn(false);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.changePassword(userId,
                ChangePasswordCommand.builder().currentPassword("wrong").newPassword("new-secret").build()));

        assertEquals(Message.CURRENT_PASSWORD_INCORRECT, ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(sessionService, never()).revokeAll(any());
    }
}
