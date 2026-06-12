package com.StoreManagement.Auth.Application.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.StoreManagement.Auth.Infrastructure.Persistence.Entity.EmailVerifyToken;
import com.StoreManagement.Auth.Infrastructure.Persistence.Repository.IEmailVerifyTokenRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerificationTokenService {
    private final IEmailVerifyTokenRepository repo;
    private final PasswordEncoder encoder;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.application.base-url}")
    private String baseUrl;

    @Transactional
    public void createToken(String email, String token) {
        EmailVerifyToken entity = repo.findByEmail(email);
        if (entity != null) {
            repo.delete(entity);
        }

        String hashed = encoder.encode(token);
        entity = new EmailVerifyToken();
        entity.setEmail(email);
        entity.setToken(hashed);
        entity.setExpiresAt(LocalDateTime.now().plusHours(24));
        repo.save(entity);
    }

    public boolean verify(String email, String token) {
        EmailVerifyToken entity = repo.findByEmail(email);
        if (entity == null) {
            return false;
        }

        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            repo.delete(entity);
            return false;
        }

        if (!encoder.matches(token, entity.getToken())) {
            return false;
        }

        repo.delete(entity);
        return true;
    }

    @Async
    public void sendVerifyEmail(String toEmail, String token) throws MessagingException {
        String link = baseUrl + "/api/auth/verify-email?email=" + toEmail + "&token=" + token;

        Context context = new Context();
        context.setVariable("link", link);

        String htmlContent = templateEngine.process("mails/activate_account.html", context);
        
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("Activate your account");
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }

    @Async
    public void sendResetPasswordEmail(String toEmail, String token) throws MessagingException {
        String link = baseUrl + "/api/auth/reset-password?email=" + toEmail + "&token=" + token;

        Context context = new Context();
        context.setVariable("link", link);

        String htmlContent = templateEngine.process("mails/reset_password.html", context);
        
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("Reset your password");
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }
}
