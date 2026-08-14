package com.Marketplace_Management.Auth.Consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.Marketplace_Management.Auth.Contracts.IAuthService;
import com.Marketplace_Management.Auth.DTOs.Messages.VendorCreatedMessage;
import com.Marketplace_Management.Shared.Configuration.RabbitMqQueues.OrderQueueConfig;
import com.Marketplace_Management.Shared.Constants.UserRole;

@Component
public class VendorEventsConsumer {
    private static final Logger logger = LoggerFactory.getLogger(VendorEventsConsumer.class);
    private final IAuthService authService;

    public VendorEventsConsumer(IAuthService authService) {
        this.authService = authService;
    }

    @RabbitListener(queues = OrderQueueConfig.ORDER_PLACED_QUEUE)
    public void handleVendorCreatedEvent(VendorCreatedMessage message) {
        
        System.out.println("VendorCreatedMessage received: " + message);
        try {
            authService.grantRole(message.getUserId(), UserRole.VENDOR);
        } catch (Exception e) {
            logger.error("Error handling VendorCreatedMessage: {}", e.getMessage(), e);
        }
    }
}
