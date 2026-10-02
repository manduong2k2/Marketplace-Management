package com.Marketplace_Management.Auth.Services;

import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.Marketplace_Management.Auth.Constants.Message;
import com.Marketplace_Management.Auth.Constants.UserStatus;
import com.Marketplace_Management.Auth.Contracts.IAuthService;
import com.Marketplace_Management.Auth.Contracts.IRoleRepository;
import com.Marketplace_Management.Auth.Contracts.IUserRepository;
import com.Marketplace_Management.Auth.DTOs.Commands.ActivateUserCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.ForgotPasswordCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.GoogleLoginCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.LoginCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.RefreshTokenCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.RegisterCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.ResetPasswordCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.UpdateProfileCommand;
import com.Marketplace_Management.Auth.DTOs.Response.AuthResponse;
import com.Marketplace_Management.Auth.DTOs.Response.RegisterResponse;
import com.Marketplace_Management.Auth.Models.Role;
import com.Marketplace_Management.Auth.Models.User;
import com.Marketplace_Management.Auth.Services.GoogleIdTokenService.GoogleUserInfo;
import com.Marketplace_Management.Shared.Constants.UserRole;
import com.Marketplace_Management.Shared.Contracts.IFileService;
import com.Marketplace_Management.Shared.Errors.Exceptions.BadRequestException;
import com.Marketplace_Management.Shared.Errors.Exceptions.ResourceNotFoundException;
import com.Marketplace_Management.Shared.Security.JwtService;
import com.Marketplace_Management.Shared.Utils.Helpers.Helper;

@Service
public class AuthService implements IAuthService {
    private final IFileService fileService;
    private final IUserRepository repo;
    private final IRoleRepository roleRepo;
    private final JwtService tokenService;
    private final PasswordEncoder encoder;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final GoogleIdTokenService googleIdTokenService;

    public AuthService(IUserRepository repo, IRoleRepository roleRepo, JwtService tokenService,
                      PasswordEncoder encoder, EmailVerificationTokenService emailVerificationTokenService, IFileService fileService,
                      GoogleIdTokenService googleIdTokenService) {
        this.repo = repo;
        this.roleRepo = roleRepo;
        this.tokenService = tokenService;
        this.encoder = encoder;
        this.emailVerificationTokenService = emailVerificationTokenService;
        this.fileService = fileService;
        this.googleIdTokenService = googleIdTokenService;
    }

    @Transactional
    @CacheEvict(value = "users", key = "#command.email")
    public RegisterResponse register(RegisterCommand command) throws MessagingException {
        User user = User.builder()
            .email(command.getEmail())
            .password(encoder.encode(command.getPassword()))
            .name(command.getName())
            .phone(command.getPhone())
            .status(UserStatus.DEFAULT)
            .build();

        createUserWithDefaultRole(user);

        return new RegisterResponse(true, Message.ACTIVATION_MAIL_SENT);
    }

    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginCommand command) {
        GoogleUserInfo google = googleIdTokenService.verify(command.getIdToken());

        if (!google.emailVerified()) {
            throw new AuthenticationException(Message.GOOGLE_EMAIL_NOT_VERIFIED) {};
        }

        User user = repo.findByGoogleId(google.googleId())
                .orElseGet(() -> repo.findByEmail(google.email())
                        .map(existing -> linkGoogleAccount(existing, google))
                        .orElseGet(() -> createGoogleUser(google)));

        return new AuthResponse(
                tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user),
                Message.LOGIN_SUCCESS);
    }

    private User linkGoogleAccount(User user, GoogleUserInfo google) {
        user.setGoogleId(google.googleId());
        // Google has verified the email, so the account no longer needs email activation
        user.setStatus(UserStatus.ACTIVE);
        if (user.getAvatar() == null || user.getAvatar().isBlank()) {
            user.setAvatar(google.picture());
        }
        return repo.save(user);
    }

    private User createGoogleUser(GoogleUserInfo google) {
        User user = User.builder()
            .email(google.email())
            .password(encoder.encode(Helper.randomString(32)))
            .name(google.name())
            .avatar(google.picture())
            .googleId(google.googleId())
            .status(UserStatus.ACTIVE)
            .build();

        return createUserWithDefaultRole(user);
    }

    private User createUserWithDefaultRole(User user) {
        Role userRole = this.roleRepo.findByCode(UserRole.USER).orElseThrow(() -> new ResourceNotFoundException(Message.ROLE_NOT_FOUND));

        Set<Role> roles = new java.util.HashSet<Role>();
        roles.add(userRole);
        user.setRoles(roles);

        return this.repo.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#command.email")
    public AuthResponse activeUser(ActivateUserCommand command) {
        var user = repo.findByEmail(command.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(Message.USER_NOT_FOUND));

        boolean isVerified = emailVerificationTokenService.verify(command.getEmail(), command.getToken());
        if (!isVerified) {
            throw new BadRequestException(Message.TOKEN_INVALID);
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
            throw new BadRequestException(Message.TOKEN_INVALID);

        var user = repo.findById(UUID.fromString(claims.getSubject()))
                .orElseThrow(() -> new ResourceNotFoundException(Message.USER_NOT_FOUND));

        return new AuthResponse(tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user),
                Message.TOKEN_REFRESHED);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendResetPasswordEmail(ForgotPasswordCommand command) throws MessagingException {
        String token = Helper.randomString(16);
        emailVerificationTokenService.createToken(command.getEmail(), token);
        emailVerificationTokenService.sendResetPasswordEmail(command.getEmail(), token);
    }

    @CacheEvict(value = "users", key = "#command.email")
    public boolean resetPassword(ResetPasswordCommand command) {
        var user = repo.findByEmail(command.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(Message.USER_NOT_FOUND));

        boolean isVerified = emailVerificationTokenService.verify(command.getEmail(), command.getToken());
        if (!isVerified) {
            throw new BadRequestException(Message.TOKEN_INVALID);
        }

        user.setPassword(encoder.encode(command.getNewPassword()));
        repo.save(user);
        return true;
    }

    public void logout(String key, String token) {
        tokenService.invalidateToken(token);
    }

    @Cacheable(value = "users", key = "#userId")
    public User getUserById(UUID userId) {
        return repo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Message.USER_NOT_FOUND));
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void updateProfile(UUID userId, UpdateProfileCommand command) throws IOException {
        var user = repo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Message.USER_NOT_FOUND));
        user.setName(command.getName());
        user.setPhone(command.getPhone().isBlank() ? user.getPhone() : command.getPhone());
        if(command.getAvatar() != null) {
            String url = fileService.uploadFile(command.getAvatar(), "users/avatars");
            user.setAvatar(url);
        }
        repo.save(user);
    }
    
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void grantRole(UUID userId, String role) {
        var user = repo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Message.USER_NOT_FOUND));

        var roleEntity = roleRepo.findByCode(role)
                .orElseThrow(() -> new ResourceNotFoundException(Message.ROLE_NOT_FOUND));
        user.getRoles().add(roleEntity);
        repo.save(user);
    }
    
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void revokeRole(UUID userId, String role) {
        var user = repo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Message.USER_NOT_FOUND));

        var roleEntity = roleRepo.findByCode(role)
                .orElseThrow(() -> new ResourceNotFoundException(Message.ROLE_NOT_FOUND));
        user.getRoles().remove(roleEntity);
        repo.save(user);
    }
}
