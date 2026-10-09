package com.Marketplace_Management.Auth.Services;

import jakarta.mail.MessagingException;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
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
import com.Marketplace_Management.Auth.Contracts.IOAuthInfoRepository;
import com.Marketplace_Management.Auth.Contracts.IRoleRepository;
import com.Marketplace_Management.Auth.Contracts.IUserRepository;
import com.Marketplace_Management.Auth.DTOs.Commands.ActivateUserCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.ForgotPasswordCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.OAuthLoginCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.LoginCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.RefreshTokenCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.RegisterCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.ResetPasswordCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.UpdateProfileCommand;
import com.Marketplace_Management.Auth.DTOs.Response.AuthResponse;
import com.Marketplace_Management.Auth.DTOs.Response.RegisterResponse;
import com.Marketplace_Management.Auth.Events.UserAccessChangedEvent;
import com.Marketplace_Management.Auth.Models.Role;
import com.Marketplace_Management.Auth.Models.OAuthInfo;
import com.Marketplace_Management.Auth.Models.User;
import com.Marketplace_Management.Auth.Services.OAuth.OAuthStrategyResolver;
import com.Marketplace_Management.Auth.Services.OAuth.OAuthUserInfo;
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
    private final OAuthStrategyResolver oauthStrategies;
    private final IOAuthInfoRepository oauthInfoRepo;
    private final UserSessionService sessionService;
    private final ApplicationEventPublisher eventPublisher;

    public AuthService(IUserRepository repo, IRoleRepository roleRepo, JwtService tokenService,
                      PasswordEncoder encoder, EmailVerificationTokenService emailVerificationTokenService, IFileService fileService,
                      OAuthStrategyResolver oauthStrategies, IOAuthInfoRepository oauthInfoRepo, UserSessionService sessionService,
                      ApplicationEventPublisher eventPublisher) {
        this.repo = repo;
        this.roleRepo = roleRepo;
        this.tokenService = tokenService;
        this.encoder = encoder;
        this.emailVerificationTokenService = emailVerificationTokenService;
        this.fileService = fileService;
        this.oauthStrategies = oauthStrategies;
        this.oauthInfoRepo = oauthInfoRepo;
        this.sessionService = sessionService;
        this.eventPublisher = eventPublisher;
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

    /**
     * Sign in with a third-party provider (Google, Facebook…). The provider's strategy verifies
     * the credential, then:
     * 1. provider account already linked -> that user
     * 2. a user with the same (provider-verified) email exists -> link the provider account to it
     * 3. otherwise -> create an active user from the provider profile and link it
     */
    @Transactional
    public AuthResponse loginWithOAuth(OAuthLoginCommand command) {
        OAuthUserInfo info = oauthStrategies.resolve(command.getProvider()).verify(command.getCredential());

        User user = oauthInfoRepo.findByProviderAndSubject(info.provider(), info.subject())
                .flatMap(link -> repo.findById(link.getUserId()))
                .map(linked -> {
                    // Linked accounts were activated when linked: INACTIVE now means deactivated by an admin
                    if (!UserStatus.ACTIVE.equals(linked.getStatus())) {
                        throw new AuthenticationException(Message.ACTIVATION) {};
                    }
                    return linked;
                })
                .orElseGet(() -> linkOrCreateUser(info));

        return sessionService.issue(user, Message.LOGIN_SUCCESS);
    }

    private User linkOrCreateUser(OAuthUserInfo info) {
        // Linking/creating relies on the email: it must exist and be verified by the provider
        if (info.email() == null || info.email().isBlank() || !info.emailVerified()) {
            throw new AuthenticationException(Message.OAUTH_EMAIL_REQUIRED) {};
        }

        User user = repo.findByEmail(info.email())
                .map(existing -> activateWithProviderProfile(existing, info))
                .orElseGet(() -> createOAuthUser(info));

        oauthInfoRepo.save(OAuthInfo.builder()
                .userId(user.getId())
                .oauthProvider(info.provider())
                .oauthProviderSubject(info.subject())
                .createdAt(Instant.now())
                .build());
        return user;
    }

    private User activateWithProviderProfile(User user, OAuthUserInfo info) {
        // The provider has verified the email, so the account no longer needs email activation
        user.setStatus(UserStatus.ACTIVE);
        if ((user.getAvatar() == null || user.getAvatar().isBlank()) && info.picture() != null) {
            user.setAvatar(info.picture());
        }
        return repo.save(user);
    }

    private User createOAuthUser(OAuthUserInfo info) {
        User user = User.builder()
            .email(info.email())
            // Random unusable password: the user signs in with the provider, or sets one via "forgot password"
            .password(encoder.encode(Helper.randomString(32)))
            .name(info.name())
            .avatar(info.picture())
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
        return sessionService.issue(user, Message.ACTIVATED);
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

        return sessionService.issue(user, Message.LOGIN_SUCCESS);
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

        return sessionService.issue(user, Message.LOGIN_SUCCESS);
    }

    public AuthResponse refreshToken(RefreshTokenCommand command) {
        // Only refresh tokens (typ=refresh), not revoked (a rotated one is accepted during its grace period)
        var claims = tokenService.verifyRefreshToken(command.getRefreshToken());
        if (claims == null) {
            throw new AuthenticationException(Message.TOKEN_INVALID) {};
        }

        // Deleted or deactivated users cannot refresh: their access ends here
        var user = repo.findById(UUID.fromString(claims.getSubject()))
                .filter(u -> UserStatus.ACTIVE.equals(u.getStatus()))
                .orElseThrow(() -> new AuthenticationException(Message.TOKEN_INVALID) {});

        sessionService.rotate(claims);

        return sessionService.issue(user, Message.TOKEN_REFRESHED);
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
        eventPublisher.publishEvent(UserAccessChangedEvent.of(user.getId(), "password reset"));
        return true;
    }

    public void logout(String accessToken, String refreshToken) {
        sessionService.logout(accessToken, refreshToken);
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
        eventPublisher.publishEvent(UserAccessChangedEvent.of(userId, "role granted: " + role));
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
        eventPublisher.publishEvent(UserAccessChangedEvent.of(userId, "role revoked: " + role));
    }
}
