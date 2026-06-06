package com.StoreManagement.Auth.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.StoreManagement.Auth.Application.DTO.Commands.LoginCommand;
import com.StoreManagement.Auth.Application.DTO.Response.AuthResponse;
import com.StoreManagement.Auth.Application.Service.AuthService;
import com.StoreManagement.Auth.Domain.Contract.IUserRepository;
import com.StoreManagement.Auth.Domain.Models.User;
import com.StoreManagement.Shared.Infrastructure.Security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    IUserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @InjectMocks
    AuthService authService;

    @Test
    void testLogin_success() {

        // arrange
        String email = "test@gmail.com";
        String rawPassword = "123456";
        String encodedPassword = "encodedPassword";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setStatus("ACTIVE");

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(true);

        when(jwtService.generateAccessToken(user))
                .thenReturn("fake-token");

        // act
        AuthResponse response = authService.login(
                new LoginCommand(email, rawPassword)
        );

        // assert
        assertEquals("fake-token", response.getAccessToken());
    }
}
