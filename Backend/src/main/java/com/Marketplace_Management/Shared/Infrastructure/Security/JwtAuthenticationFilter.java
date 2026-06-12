package com.Marketplace_Management.Shared.Infrastructure.Security;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtService.verifyToken(token);
        if (claims == null || claims.getSubject() == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Object rolesObj = claims.get("roles");

        List<SimpleGrantedAuthority> roles = ((List<?>) rolesObj)
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .toList();

        UUID userId = UUID.fromString(claims.getSubject());

        UserPrincipal principal = new UserPrincipal(userId, roles);

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, roles));

        filterChain.doFilter(
                request,
                response);
    }

    private String resolveToken(
            HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {

            if ("ACCESS_TOKEN".equals(
                    cookie.getName())) {

                return cookie.getValue();
            }
        }

        return null;
    }
}
