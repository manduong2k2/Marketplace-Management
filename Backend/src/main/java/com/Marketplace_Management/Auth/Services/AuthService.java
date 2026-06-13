package com.Marketplace_Management.Auth.Services;

import jakarta.mail.MessagingException;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.Marketplace_Management.Auth.Constants.Message;
import com.Marketplace_Management.Auth.Constants.UserStatus;
import com.Marketplace_Management.Auth.Contract.IAuthService;
import com.Marketplace_Management.Auth.Contract.IRoleRepository;
import com.Marketplace_Management.Auth.Contract.IUserRepository;
import com.Marketplace_Management.Auth.DTO.Commands.ActivateUserCommand;
import com.Marketplace_Management.Auth.DTO.Commands.ForgotPasswordCommand;
import com.Marketplace_Management.Auth.DTO.Commands.LoginCommand;
import com.Marketplace_Management.Auth.DTO.Commands.RefreshTokenCommand;
import com.Marketplace_Management.Auth.DTO.Commands.RegisterCommand;
import com.Marketplace_Management.Auth.DTO.Commands.ResetPasswordCommand;
import com.Marketplace_Management.Auth.DTO.Commands.UpdateProfileCommand;
import com.Marketplace_Management.Auth.DTO.Response.AuthResponse;
import com.Marketplace_Management.Auth.DTO.Response.RegisterResponse;
import com.Marketplace_Management.Auth.Models.Role;
import com.Marketplace_Management.Auth.Models.User;
import com.Marketplace_Management.Shared.Domain.Constants.UserRole;
import com.Marketplace_Management.Shared.Infrastructure.Security.JwtService;

@Service
public class AuthService implements IAuthService {
    private final IUserRepository repo;
    private final IRoleRepository roleRepo;
    private final JwtService tokenService;
    private final PasswordEncoder encoder;
    private final EmailVerificationTokenService emailVerificationTokenService;

    public AuthService(IUserRepository repo, IRoleRepository roleRepo, JwtService tokenService,
                      PasswordEncoder encoder, EmailVerificationTokenService emailVerificationTokenService) {
        this.repo = repo;
        this.roleRepo = roleRepo;
        this.tokenService = tokenService;
        this.encoder = encoder;
        this.emailVerificationTokenService = emailVerificationTokenService;
    }

    @Transactional
    public RegisterResponse register(RegisterCommand command) throws MessagingException {
        User user = new User();
        user.setEmail(command.getEmail());
        user.setPassword(encoder.encode(command.getPassword()));
        user.setName(command.getName());
        user.setPhone(command.getPhone());
        user.setStatus(UserStatus.DEFAULT);

        Role userRole = this.roleRepo.findByCode("USER").orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Message.ROLE_NOT_FOUND));
        Set<Role> roles = new java.util.HashSet<Role>();

        roles.add(userRole);
        user.setRoles(roles);

        this.repo.save(user);
        
        sendActivationEmail(user.getEmail());

        return new RegisterResponse(true, Message.ACTIVATION_MAIL_SENT);
    }

    @Transactional
    public AuthResponse activeUser(ActivateUserCommand command) {
        var user = repo.findByEmail(command.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Message.USER_NOT_FOUND));

        boolean isVerified = emailVerificationTokenService.verify(command.getEmail(), command.getToken());
        if (!isVerified) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Message.TOKEN_INVALID);
        }

        user.setStatus(UserStatus.ACTIVE);
        repo.save(user);
        return new AuthResponse(tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user),
                Message.ACTIVATED);
    }

    public AuthResponse login(LoginCommand command) {
        var user = repo.findByEmail(command.getEmail())
                .orElseThrow(() -> new AuthenticationException(Message.CREDENTIALS) {});

        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new AuthenticationException(Message.ACTIVATION) {};
        }

        if (!encoder.matches(command.getPassword(), user.getPassword())) {
            throw new AuthenticationException(Message.CREDENTIALS) {};
        }

        return new AuthResponse(
                tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user),
                Message.LOGIN_SUCCESS);
    }
    
    public AuthResponse loginAdmin(LoginCommand command) {
        var user = repo.findByEmail(command.getEmail())
                .orElseThrow(() -> new AuthenticationException(Message.CREDENTIALS) {});

        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new AuthenticationException(Message.ACTIVATION) {};
        }

        if (!encoder.matches(command.getPassword(), user.getPassword())) {
            throw new AuthenticationException(Message.CREDENTIALS) {};
        }
        
        if (!user.getRoles().stream().anyMatch(role -> role.getCode().equals(UserRole.ADMIN))) {
            throw new AccessDeniedException(Message.FORBIDDEN) {};
        }

        return new AuthResponse(
                tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user),
                Message.LOGIN_SUCCESS);
    }

    public AuthResponse refreshToken(RefreshTokenCommand command) {
        var claims = tokenService.verifyToken(command.getRefreshToken());
        if (claims == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Message.TOKEN_INVALID);

        var user = repo.findById(UUID.fromString(claims.getSubject()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Message.USER_NOT_FOUND));

        return new AuthResponse(tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user),
                Message.TOKEN_REFRESHED);
    }

    public boolean sendActivationEmail(String email) throws MessagingException {
        String token = UUID.randomUUID().toString(); // switch

        emailVerificationTokenService.createToken(email, token);
        emailVerificationTokenService.sendVerifyEmail(email, token);
        return true;
    }

    public boolean sendResetPasswordEmail(ForgotPasswordCommand command) throws MessagingException {
        String token = UUID.randomUUID().toString();

        emailVerificationTokenService.createToken(command.getEmail(), token);
        emailVerificationTokenService.sendResetPasswordEmail(command.getEmail(), token);
        return true;
    }

    public boolean resetPassword(ResetPasswordCommand command) {
        var user = repo.findByEmail(command.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Message.USER_NOT_FOUND));

        boolean isVerified = emailVerificationTokenService.verify(command.getEmail(), command.getToken());
        if (!isVerified) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Message.TOKEN_INVALID);
        }

        user.setPassword(encoder.encode(command.getNewPassword()));
        repo.save(user);
        return true;
    }

    public void logout(String key, String token) {
        tokenService.invalidateToken(token);
    }

    public User getUserById(UUID userId) {
        return repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Message.USER_NOT_FOUND));
    }

    @Transactional
    public void updateProfile(UUID userId, UpdateProfileCommand command) {
        var user = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Message.USER_NOT_FOUND));
        user.setName(command.getName());
        user.setPhone(command.getPhone());
        repo.save(user);
    }
}
