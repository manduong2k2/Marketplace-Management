package com.StoreManagement.Order.Application.Consumer;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.StoreManagement.Order.Application.Contracts.ICartService;
import com.StoreManagement.Order.Application.DTO.Messages.ProductDeletedMessage;
import com.StoreManagement.Order.Application.DTO.Messages.ProductOutStockMessage;
import com.StoreManagement.Shared.Infrastructure.Configuration.ProductQueueConfig;

@Component
public class ProductEventsConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProductEventsConsumer.class);

    @Autowired
    private ICartService cartService;

    @RabbitListener(queues = ProductQueueConfig.PRODUCT_DELETED_QUEUE)
    public void handleProductDeletedEvent(ProductDeletedMessage message) {
        System.out.println("ProductDeletedMessage received: " + message);
        try {
            UUID productId = message.getProductId();
            logger.info("Received ProductDeletedMessage for productId: {}", productId);
            cartService.removeItemsByProductId(productId);
            
            logger.info("Successfully removed cart items for productId: {}", productId);
        } catch (Exception e) {
            logger.error("Error handling ProductDeletedMessage: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = ProductQueueConfig.PRODUCT_OUT_OF_STOCK_QUEUE)
    public void handleProductOutOfStockEvent(ProductOutStockMessage message) {
        System.out.println("ProductOutStockMessage received: " + message);
        try {
            UUID productId = message.getProductId();
            logger.info("Received ProductOutStockMessage for productId: {}", productId);
            cartService.removeItemsByProductId(productId);
            
            logger.info("Successfully removed cart items for productId: {}", productId);
        } catch (Exception e) {
            logger.error("Error handling ProductOutOfStockMessage: {}", e.getMessage(), e);
        }
    }
}
