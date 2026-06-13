package com.Marketplace_Management.Auth.Contract;
import java.util.UUID;

import com.Marketplace_Management.Auth.DTO.Commands.ActivateUserCommand;
import com.Marketplace_Management.Auth.DTO.Commands.ForgotPasswordCommand;
import com.Marketplace_Management.Auth.DTO.Commands.LoginCommand;
import com.Marketplace_Management.Auth.DTO.Commands.RefreshTokenCommand;
import com.Marketplace_Management.Auth.DTO.Commands.RegisterCommand;
import com.Marketplace_Management.Auth.DTO.Commands.ResetPasswordCommand;
import com.Marketplace_Management.Auth.DTO.Commands.UpdateProfileCommand;
import com.Marketplace_Management.Auth.DTO.Response.AuthResponse;
import com.Marketplace_Management.Auth.DTO.Response.RegisterResponse;
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
