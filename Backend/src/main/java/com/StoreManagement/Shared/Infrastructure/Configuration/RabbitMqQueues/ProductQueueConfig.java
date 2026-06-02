package com.StoreManagement.Shared.Infrastructure.Configuration.RabbitMqQueues;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;

@Configuration
public class ProductQueueConfig {
    public static final String PRODUCT_DELETED_QUEUE            = "product.deleted.queue";
    public static final String PRODUCT_DELETED_EXCHANGE         = "product.deleted.exchange";
    public static final String PRODUCT_DELETED_ROUTING_KEY      = "product.deleted";

    public static final String PRODUCT_OUT_OF_STOCK_QUEUE       = "product.out_of_stock.queue";
    public static final String PRODUCT_OUT_OF_STOCK_EXCHANGE    = "product.out_of_stock.exchange";
    public static final String PRODUCT_OUT_OF_STOCK_ROUTING_KEY = "product.out_of_stock";

    public static final String PRODUCT_ARCHIVED_QUEUE           = "product.archived.queue";
    public static final String PRODUCT_ARCHIVED_EXCHANGE        = "product.archived.exchange";
    public static final String PRODUCT_ARCHIVED_ROUTING_KEY     = "product.archived";

    @Bean
    public Queue productDeletedQueue() {
        return QueueBuilder.durable(PRODUCT_DELETED_QUEUE).build();
    }

    @Bean
    public Queue productOutOfStockQueue() {
        return QueueBuilder.durable(PRODUCT_OUT_OF_STOCK_QUEUE).build();
    }

    @Bean
    public Queue productArchivedQueue() {
        return QueueBuilder.durable(PRODUCT_ARCHIVED_QUEUE).build();
    }

    @Bean
    public TopicExchange productDeletedExchange() {
        return new TopicExchange(PRODUCT_DELETED_EXCHANGE);
    }

    @Bean
    public TopicExchange productOutOfStockExchange() {
        return new TopicExchange(PRODUCT_OUT_OF_STOCK_EXCHANGE);
    }

    @Bean
    public TopicExchange productArchivedExchange() {
        return new TopicExchange(PRODUCT_ARCHIVED_EXCHANGE);
    }

    @Bean
    public Binding productDeletedBinding() {
        return BindingBuilder
                .bind(productDeletedQueue())
                .to(productDeletedExchange())
                .with(PRODUCT_DELETED_ROUTING_KEY);
    }
    
    @Bean
    public Binding productOutOfStockBinding() {
        return BindingBuilder
                .bind(productOutOfStockQueue())
                .to(productOutOfStockExchange())
                .with(PRODUCT_OUT_OF_STOCK_ROUTING_KEY);
    }
    
    @Bean
    public Binding productArchivedBinding() {
        return BindingBuilder
                .bind(productArchivedQueue())
                .to(productArchivedExchange())
                .with(PRODUCT_ARCHIVED_ROUTING_KEY);
    }
}
