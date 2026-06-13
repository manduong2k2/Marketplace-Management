package com.Marketplace_Management.Cart.Consumer;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.Marketplace_Management.Cart.Contracts.ICartService;
import com.Marketplace_Management.Cart.DTOs.Messages.ProductDeletedMessage;
import com.Marketplace_Management.Cart.DTOs.Messages.ProductOutStockMessage;
import com.Marketplace_Management.Shared.Configuration.RabbitMqQueues.ProductQueueConfig;

@Component
public class ProductEventsConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProductEventsConsumer.class);
    private final ICartService cartService;

    public ProductEventsConsumer(ICartService cartService) {
        this.cartService = cartService;
    }

    @RabbitListener(queues = ProductQueueConfig.PRODUCT_DELETED_QUEUE)
    public void handleProductDeletedEvent(ProductDeletedMessage message) {
        System.out.println("ProductDeletedMessage received: " + message);
        try {
            UUID productVariantId = message.getProductVariantId();
            logger.info("Received ProductDeletedMessage for productVariantId: {}", productVariantId);
            cartService.removeItemsByProductVariantId(productVariantId);
            
            logger.info("Successfully removed cart items for productVariantId: {}", productVariantId);
        } catch (Exception e) {
            logger.error("Error handling ProductDeletedMessage: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = ProductQueueConfig.PRODUCT_OUT_OF_STOCK_QUEUE)
    public void handleProductOutOfStockEvent(ProductOutStockMessage message) {
        System.out.println("ProductOutStockMessage received: " + message);
        try {
            UUID productVariantId = message.getProductVariantId();
            logger.info("Received ProductOutStockMessage for productVariantId: {}", productVariantId);
            cartService.removeItemsByProductVariantId(productVariantId);
            
            logger.info("Successfully removed cart items for productVariantId: {}", productVariantId);
        } catch (Exception e) {
            logger.error("Error handling ProductOutOfStockMessage: {}", e.getMessage(), e);
        }
    }
}
