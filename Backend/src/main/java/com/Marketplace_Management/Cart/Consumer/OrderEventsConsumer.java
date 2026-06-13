package com.Marketplace_Management.Cart.Consumer;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.Marketplace_Management.Cart.Contracts.ICartService;
import com.Marketplace_Management.Cart.DTO.Messages.OrderPlacedMessage;
import com.Marketplace_Management.Shared.Infrastructure.Configuration.RabbitMqQueues.OrderQueueConfig;

@Component
public class OrderEventsConsumer {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventsConsumer.class);
    private final ICartService cartService;

    public OrderEventsConsumer(ICartService cartService) {
        this.cartService = cartService;
    }

    @RabbitListener(queues = OrderQueueConfig.ORDER_PLACED_QUEUE)
    public void handleOrderPlacedEvent(OrderPlacedMessage message) {
        System.out.println("OrderPlacedMessage received: " + message);
        try {
            UUID userId = message.getUserId();
            logger.info("Received OrderPlacedMessage for userId: {}", userId);
            cartService.clearCart(userId);
        } catch (Exception e) {
            logger.error("Error handling OrderPlacedMessage: {}", e.getMessage(), e);
        }
    }
}
