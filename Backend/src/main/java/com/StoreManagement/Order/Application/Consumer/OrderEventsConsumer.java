package com.StoreManagement.Order.Application.Consumer;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.StoreManagement.Order.Application.Contracts.ICartService;
import com.StoreManagement.Order.Application.DTO.Messages.OrderPlacedMessage;
import com.StoreManagement.Shared.Infrastructure.Configuration.RabbitMqQueues.OrderQueueConfig;

@Component
public class OrderEventsConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ProductEventsConsumer.class);

    @Autowired
    private ICartService cartService;

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
