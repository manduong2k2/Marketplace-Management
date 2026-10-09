package com.Marketplace_Management.Auth.Services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.Marketplace_Management.Auth.Constants.Message;
import com.Marketplace_Management.Auth.Contracts.IUserSessionRepository;
import com.Marketplace_Management.Auth.DTOs.Response.AuthResponse;
import com.Marketplace_Management.Auth.Models.User;
import com.Marketplace_Management.Auth.Models.UserSession;
import com.Marketplace_Management.Shared.Security.JwtService;
import com.Marketplace_Management.Shared.Security.JwtService.IssuedToken;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Issues token pairs and keeps user_sessions in sync. Every place that hands out tokens goes
 * through {@link #issue}, so every live access token can be found (by user) and revoked.
 * Stateless verification is untouched: the table is only read to revoke tokens.
 */
@Service
public class UserSessionService {
    private static final Logger logger = LoggerFactory.getLogger(UserSessionService.class);

    private final JwtService jwtService;
    private final IUserSessionRepository sessionRepo;

    public UserSessionService(JwtService jwtService, IUserSessionRepository sessionRepo) {
        this.jwtService = jwtService;
        this.sessionRepo = sessionRepo;
    }

    /** New token pair + a session row for it. */
    public AuthResponse issue(User user, String message) {
        IssuedToken access = jwtService.generateAccessToken(user);
        IssuedToken refresh = jwtService.generateRefreshToken(user);

        sessionRepo.save(UserSession.builder()
                .userId(user.getId())
                .ipAddress(currentIp())
                .loginAt(Instant.now())
                .accessJti(access.jti())
                .accessExpiresAt(access.expiresAt())
                .refreshJti(refresh.jti())
                .refreshExpiresAt(refresh.expiresAt())
                .build());

        return new AuthResponse(access.token(), refresh.token(), message);
    }

    /**
     * Refresh: the old session is replaced by the one created by {@link #issue}. The old refresh
     * token stays usable for a short grace period (other tabs refreshing at the same time).
     * A missing session is fine (e.g. removed by an access change): the table never gates a refresh.
     */
    public void rotate(Claims refreshClaims) {
        sessionRepo.deleteByRefreshJti(refreshClaims.getId());
        jwtService.blacklistWithGrace(refreshClaims.getId(), refreshClaims.getExpiration().toInstant());
    }

    /** Logout: revoke both tokens and drop their session. Works with expired/invalid cookies too. */
    public void logout(String accessToken, String refreshToken) {
        jwtService.invalidateToken(accessToken);
        jwtService.invalidateToken(refreshToken);

        Claims refresh = jwtService.decode(refreshToken);
        if (refresh != null) {
            sessionRepo.deleteByRefreshJti(refresh.getId());
            return;
        }
        Claims access = jwtService.decode(accessToken);
        if (access != null && access.getSubject() != null) {
            sessionRepo.deleteByUserIdAndAccessJti(UUID.fromString(access.getSubject()), access.getId());
        }
    }

    /**
     * Access changed (roles, status, deleted): revoke every live access token of these users so their
     * next request gets 401 and the client refreshes (new token = current roles). Uses the stored
     * jti/expiry, no token decoding. A session is deleted only once its token is blacklisted, so a
     * Redis failure leaves it in place to be revoked again later.
     *
     * REQUIRES_NEW: called from an AFTER_COMMIT listener, where the caller's transaction is finished.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int revokeAll(Collection<UUID> userIds) {
        List<UserSession> sessions = sessionRepo.findByUserIds(userIds);
        List<UUID> revoked = blacklistAndDelete(sessions);

        if (revoked.size() < sessions.size()) {
            logger.error("Revoked {}/{} sessions for users {}: blacklist failed for the rest (kept for retry)",
                    revoked.size(), sessions.size(), userIds);
        }
        return revoked.size();
    }

    /**
     * Revokes every session of a user who is about to be deleted. Must run BEFORE the delete:
     * user_sessions.user_id cascades on delete, so an AFTER_COMMIT revokeAll would find no rows and the
     * user's access tokens would stay valid until they expire. Unlike revokeAll there is no later retry
     * (the rows are gone with the user), so a blacklist failure throws and aborts the delete.
     *
     * REQUIRES_NEW: the revocation stands even if the delete then fails (the user just has to log in again).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeAllBeforeDelete(UUID userId) {
        List<UserSession> sessions = sessionRepo.findByUserIds(List.of(userId));
        List<UUID> revoked = blacklistAndDelete(sessions);

        if (revoked.size() < sessions.size()) {
            logger.error("Revoked {}/{} sessions of user {} before delete: blacklist failed, delete aborted",
                    revoked.size(), sessions.size(), userId);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, Message.SESSION_REVOKE_FAILED);
        }
    }

    /** Blacklists each access token; deletes (and returns) only the sessions whose token was blacklisted. */
    private List<UUID> blacklistAndDelete(List<UserSession> sessions) {
        List<UUID> revoked = new ArrayList<>();
        for (UserSession session : sessions) {
            if (jwtService.blacklist(session.getAccessJti(), session.getAccessExpiresAt())) {
                revoked.add(session.getId());
            }
        }
        sessionRepo.deleteByIds(revoked);
        return revoked;
    }

    /** Daily at 03:00: sessions whose access token has expired have nothing left to revoke. */
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteExpiredSessions() {
        int deleted = sessionRepo.deleteExpired(Instant.now());
        logger.info("Deleted {} expired user sessions", deleted);
    }

    /**
     * Client IP. Behind Nginx, X-Real-IP is set from $remote_addr (overwritten by Nginx, so the client
     * cannot forge it). X-Forwarded-For is NOT trusted for its first entry: with
     * $proxy_add_x_forwarded_for Nginx appends to whatever the client sent, so the first value is
     * client-controlled. Fallback: the direct peer address (local dev without a proxy).
     * Null outside an HTTP request (e.g. message consumers).
     */
    private String currentIp() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String realIp = request.getHeader("X-Real-IP");
        String ip = realIp != null && !realIp.isBlank() ? realIp.trim() : request.getRemoteAddr();
        return ip != null && ip.length() > 45 ? ip.substring(0, 45) : ip;
    }
}
