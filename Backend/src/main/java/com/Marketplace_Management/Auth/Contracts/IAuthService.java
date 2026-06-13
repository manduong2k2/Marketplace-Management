package com.Marketplace_Management.Auth.Contracts;
import java.util.UUID;

import com.Marketplace_Management.Auth.DTOs.Commands.ActivateUserCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.ForgotPasswordCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.LoginCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.RefreshTokenCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.RegisterCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.ResetPasswordCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.UpdateProfileCommand;
import com.Marketplace_Management.Auth.DTOs.Response.AuthResponse;
import com.Marketplace_Management.Auth.DTOs.Response.RegisterResponse;
import com.Marketplace_Management.Auth.Models.User;

import jakarta.mail.MessagingException;

public interface IAuthService {
    RegisterResponse register(RegisterCommand command) throws MessagingException;
    AuthResponse activeUser(ActivateUserCommand command);
    AuthResponse login(LoginCommand command);
    AuthResponse loginAdmin(LoginCommand command);
    AuthResponse refreshToken(RefreshTokenCommand command);
    boolean sendActivationEmail(String email) throws MessagingException;
    boolean sendResetPasswordEmail(ForgotPasswordCommand command) throws MessagingException;
    boolean resetPassword(ResetPasswordCommand command);
    void logout(String key, String token);
    User getUserById(UUID userId);
    void updateProfile(UUID userId, UpdateProfileCommand command);
}
