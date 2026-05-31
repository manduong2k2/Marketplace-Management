package com.StoreManagement.Auth.Domain.Contract;
import java.util.UUID;

import com.StoreManagement.Auth.Application.DTO.Commands.ActivateUserCommand;
import com.StoreManagement.Auth.Application.DTO.Commands.ForgotPasswordCommand;
import com.StoreManagement.Auth.Application.DTO.Commands.LoginCommand;
import com.StoreManagement.Auth.Application.DTO.Commands.RefreshTokenCommand;
import com.StoreManagement.Auth.Application.DTO.Commands.RegisterCommand;
import com.StoreManagement.Auth.Application.DTO.Commands.ResetPasswordCommand;
import com.StoreManagement.Auth.Application.DTO.Commands.UpdateProfileCommand;
import com.StoreManagement.Auth.Application.DTO.Response.AuthResponse;
import com.StoreManagement.Auth.Application.DTO.Response.RegisterResponse;
import com.StoreManagement.Auth.Domain.Models.User;

import jakarta.mail.MessagingException;

public interface IAuthService {
    RegisterResponse register(RegisterCommand command) throws MessagingException;
    AuthResponse activeUser(ActivateUserCommand command);
    AuthResponse login(LoginCommand command);
    AuthResponse refreshToken(RefreshTokenCommand command);
    boolean sendActivationEmail(String email) throws MessagingException;
    boolean sendResetPasswordEmail(ForgotPasswordCommand command) throws MessagingException;
    boolean resetPassword(ResetPasswordCommand command);
    void logout(String key, String token);
    User getUserById(UUID userId);
    void updateProfile(UUID userId, UpdateProfileCommand command);
}
