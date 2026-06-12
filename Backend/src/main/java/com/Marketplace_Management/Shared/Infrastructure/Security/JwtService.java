package com.Marketplace_Management.Shared.Infrastructure.Security;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import com.Marketplace_Management.Auth.Domain.Constants.Message;
import com.Marketplace_Management.Auth.Domain.Models.User;

import org.springframework.security.core.AuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

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

    @Value("${jwt.expiration}")
    private long expiration;

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

    public String generateRefreshToken(User user) {
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .setId(jti)
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(expiration)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateAccessToken(User user) {
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .setId(jti)
                .setSubject(String.valueOf(user.getId()))
                .claim("roles", user.getRoles().stream().map(role -> role.getCode()).toArray())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(refreshTokenExpiration)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Claims verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);

            String jti = claims.getBody().getId();

            try {
                if (Boolean.TRUE.equals(
                        redisTemplate.hasKey(BLACKLIST_PREFIX + jti))) {
                    throw new AuthenticationException(
                            Message.TOKEN_BLACKLISTED) {
                    };
                }
            } catch (Exception e) {
                System.out.println("Redis unavailable: " + e.getMessage());
                return null;
            }

            return claims.getBody();

        } catch (JwtException e) {
            return null;
        }
    }

    public void invalidateToken(String token) {
        Claims claims = verifyToken(token);

        try {
            String jti = claims.getId();
            long ttl = claims.getExpiration().getTime()
                    - System.currentTimeMillis();

            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        BLACKLIST_PREFIX + jti,
                        "1",
                        Expiration.milliseconds(ttl));
            }
        } catch (Exception e) {
            System.out.println("Redis unavailable: " + e.getMessage());
        }
    }
}
