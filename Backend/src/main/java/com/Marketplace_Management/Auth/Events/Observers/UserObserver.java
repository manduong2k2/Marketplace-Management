package com.Marketplace_Management.Auth.Events.Observers;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.Marketplace_Management.Auth.Entities.UserEntity;
import com.Marketplace_Management.Auth.Services.EmailVerificationTokenService;
import com.Marketplace_Management.Shared.Events.EntityEvent;
import com.Marketplace_Management.Shared.Utils.Helpers.Helper;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserObserver {

    private final EmailVerificationTokenService tokenService;

    @Async
    // Skip users created already active (Google sign-in): their email is verified by Google
    @TransactionalEventListener(
        phase = TransactionPhase.AFTER_COMMIT,
        condition = "#event.type == T(com.Marketplace_Management.Shared.Events.EntityEvent.Type).CREATED"
                + " && #event.entity.status != T(com.Marketplace_Management.Auth.Constants.UserStatus).ACTIVE")
    public void onUserRegistered(EntityEvent<UserEntity> event) throws MessagingException {
        String token = Helper.randomString(16);
        tokenService.createToken(event.getEntity().getEmail(), token);  
        tokenService.sendVerifyEmail(event.getEntity().getEmail(), token);
        System.out.println("here");
    }
}