package com.StoreManagement.Shared.Infrastructure.Security;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.web.filter.OncePerRequestFilter;

import com.StoreManagement.Auth.Domain.Constants.Message;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

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
            throw new AuthenticationException(Message.UNAUTHENTICATED) {};
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
