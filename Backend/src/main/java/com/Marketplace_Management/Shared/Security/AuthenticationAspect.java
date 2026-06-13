package com.Marketplace_Management.Shared.Security;

import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Annotation.Auth.Authenticated;

@Aspect
@Component
public class AuthenticationAspect {
    @Around("@annotation(authenticated)")
    public Object checkAuthentication(ProceedingJoinPoint joinPoint, Authenticated authenticated) throws Throwable {
        UUID userId = SecurityUtils.currentUserId();
        if (userId == null) {
            throw new AuthenticationException("Unauthenticated") {};
        }
        return joinPoint.proceed();
    }
}
