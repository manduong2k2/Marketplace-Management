package com.Marketplace_Management.Shared.Security;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.time.Duration;
import java.time.Instant;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import com.Marketplace_Management.Auth.Models.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";
    private static final String REVOKED = "1";
    // Value stored for a rotated refresh token still inside its grace period: "grace:<epochMillis>"
    private static final String GRACE_PREFIX = "grace:";
    private static final Duration REFRESH_GRACE = Duration.ofSeconds(30);

    public static final String CLAIM_TYPE = "typ";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final StringRedisTemplate redisTemplate;

    public JwtService(
            @Value("${jwt.private-key}") Resource privateKeyResource,
            @Value("${jwt.public-key}") Resource publicKeyResource,
            StringRedisTemplate redisTemplate) {
        this.privateKey = loadPrivateKey(privateKeyResource);
        this.publicKey = loadPublicKey(publicKeyResource);
        this.redisTemplate = redisTemplate;
    }

    // Access token lifetime (days)
    @Value("${jwt.expiration}")
    private long expiration;

    // Refresh token lifetime (days)
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private PrivateKey loadPrivateKey(Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            String key = new String(is.readAllBytes())
                    .replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key", e);
        }
    }

    private PublicKey loadPublicKey(Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            String key = new String(is.readAllBytes())
                    .replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            return KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load public key", e);
        }
    }

    /** A signed token plus the data needed to track/revoke it without decoding it again. */
    public record IssuedToken(String token, String jti, Instant expiresAt) {}

    /** Access token: 'jwt.expiration' days. Carries roles + name for the request filter. */
    public IssuedToken generateAccessToken(User user) {
        String jti = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(Duration.ofDays(expiration));
        String token = Jwts.builder()
                .setId(jti)
                .setSubject(String.valueOf(user.getId()))
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .claim("roles", user.getRoles().stream().map(role -> role.getCode()).toArray())
                .claim("name", user.getName())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expiresAt))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
        return new IssuedToken(token, jti, expiresAt);
    }

    /** Refresh token: 'jwt.refresh-token-expiration' days. Only accepted by /api/auth/refresh-token. */
    public IssuedToken generateRefreshToken(User user) {
        String jti = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(Duration.ofDays(refreshTokenExpiration));
        String token = Jwts.builder()
                .setId(jti)
                .setSubject(String.valueOf(user.getId()))
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expiresAt))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
        return new IssuedToken(token, jti, expiresAt);
    }

    /** Valid, not revoked, and of type 'access'. Null otherwise. */
    public Claims verifyAccessToken(String token) {
        Claims claims = verifyToken(token);
        return claims != null && TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class)) ? claims : null;
    }

    /** Valid, not revoked (a rotated token still works during its grace period), and of type 'refresh'. */
    public Claims verifyRefreshToken(String token) {
        Claims claims = verifyToken(token);
        return claims != null && TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class)) ? claims : null;
    }

    /**
     * Signature + expiry + blacklist check, any token type. Null when invalid or revoked.
     * Fails closed: if Redis cannot be reached the token is treated as revoked.
     */
    public Claims verifyToken(String token) {
        Claims claims = decode(token);
        if (claims == null) {
            return null;
        }

        try {
            String revoked = redisTemplate.opsForValue().get(BLACKLIST_PREFIX + claims.getId());
            if (revoked == null || isInGracePeriod(revoked)) {
                return claims;
            }
            return null;
        } catch (Exception e) {
            logger.error("Redis unavailable while checking the token blacklist: {}", e.getMessage());
            return null;
        }
    }

    /** Revokes a token (logout). Returns false if it could not be decoded or Redis failed. */
    public boolean invalidateToken(String token) {
        Claims claims = decode(token);
        if (claims == null) {
            return false;
        }
        return blacklist(claims.getId(), claims.getExpiration().toInstant());
    }

    /**
     * Revokes a token by jti until it expires (no need to decode it again, e.g. from user_sessions).
     * Returns false if Redis failed, so callers can keep their records and retry later.
     */
    public boolean blacklist(String jti, Instant expiresAt) {
        long ttl = expiresAt.toEpochMilli() - System.currentTimeMillis();
        if (ttl <= 0) {
            return true;   // already expired: nothing to revoke
        }
        try {
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + jti, REVOKED, Expiration.milliseconds(ttl));
            return true;
        } catch (Exception e) {
            logger.error("Could not blacklist token {}: {}", jti, e.getMessage());
            return false;
        }
    }

    /**
     * Revokes a rotated refresh token, but keeps it usable for a short grace period so other tabs
     * refreshing at the same moment with the old token are not logged out. Never overwrites an
     * existing entry (setIfAbsent), so the grace period cannot be renewed.
     */
    public boolean blacklistWithGrace(String jti, Instant expiresAt) {
        long ttl = expiresAt.toEpochMilli() - System.currentTimeMillis();
        if (ttl <= 0) {
            return true;
        }
        long graceUntil = System.currentTimeMillis() + REFRESH_GRACE.toMillis();
        try {
            redisTemplate.opsForValue().setIfAbsent(
                    BLACKLIST_PREFIX + jti, GRACE_PREFIX + graceUntil, Duration.ofMillis(ttl));
            return true;
        } catch (Exception e) {
            logger.error("Could not blacklist rotated refresh token {}: {}", jti, e.getMessage());
            return false;
        }
    }

    private boolean isInGracePeriod(String value) {
        if (!value.startsWith(GRACE_PREFIX)) {
            return false;
        }
        try {
            return Long.parseLong(value.substring(GRACE_PREFIX.length())) > System.currentTimeMillis();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Signature + expiry only, ignoring the blacklist (read jti/exp of a token being revoked). Null if invalid. */
    public Claims decode(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
