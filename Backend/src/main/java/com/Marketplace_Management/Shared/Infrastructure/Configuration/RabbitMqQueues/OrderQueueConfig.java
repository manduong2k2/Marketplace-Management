package com.Marketplace_Management.Shared.Infrastructure.Configuration.RabbitMqQueues;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;

@Configuration
public class OrderQueueConfig {
    public static final String ORDER_PLACED_QUEUE           = "order.placed.queue";
    public static final String ORDER_PLACED_EXCHANGE        = "order.placed.exchange";
    public static final String ORDER_PLACED_ROUTING_KEY     = "order.placed";

    public static final String ORDER_CANCELED_QUEUE         = "order.canceled.queue";
    public static final String ORDER_CANCELED_EXCHANGE      = "order.canceled.exchange";
    public static final String ORDER_CANCELED_ROUTING_KEY   = "order.canceled";

    @Bean
    public Queue orderPlacedQueue() {
        return QueueBuilder.durable(ORDER_PLACED_QUEUE).build();
    }

    @Bean
    public Queue orderCanceledQueue() {
        return QueueBuilder.durable(ORDER_CANCELED_QUEUE).build();
    }

    @Bean
    public TopicExchange orderPlacedExchange() {
        return new TopicExchange(ORDER_PLACED_EXCHANGE);
    }

    @Bean
    public TopicExchange orderCanceledExchange() {
        return new TopicExchange(ORDER_CANCELED_EXCHANGE);
    }

    @Bean
    public Binding orderPlacedBinding() {
        return BindingBuilder
                .bind(orderPlacedQueue())
                .to(orderPlacedExchange())
                .with(ORDER_PLACED_ROUTING_KEY);
    }
    
    @Bean
    public Binding orderCanceledBinding() {
        return BindingBuilder
                .bind(orderCanceledQueue())
                .to(orderCanceledExchange())
                .with(ORDER_CANCELED_ROUTING_KEY);
    }
}
