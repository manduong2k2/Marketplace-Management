package com.Marketplace_Management.Auth.Consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.Marketplace_Management.Auth.Contracts.IAuthService;
import com.Marketplace_Management.Auth.DTOs.Messages.VendorActivatedMessage;
import com.Marketplace_Management.Shared.Configuration.RabbitMqQueues.VendorQueueConfig;
import com.Marketplace_Management.Shared.Constants.UserRole;

@Component
public class VendorEventsConsumer {
    private static final Logger logger = LoggerFactory.getLogger(VendorEventsConsumer.class);
    private final IAuthService authService;

    public VendorEventsConsumer(IAuthService authService) {
        this.authService = authService;
    }

    /** The owner of an activated vendor gets the VENDOR role. */
    @RabbitListener(queues = VendorQueueConfig.VENDOR_ACTIVATED_QUEUE)
    public void handleVendorActivated(VendorActivatedMessage message) {
        try {
            authService.grantRole(message.getUserId(), UserRole.VENDOR);
            logger.info("Granted VENDOR to user {}", message.getUserId());
        } catch (Exception e) {
            logger.error("Could not grant VENDOR to user {}: {}", message.getUserId(), e.getMessage(), e);
        }
    }
}
