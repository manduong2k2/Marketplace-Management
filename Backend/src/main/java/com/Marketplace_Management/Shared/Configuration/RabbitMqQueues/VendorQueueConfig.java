package com.Marketplace_Management.Shared.Configuration.RabbitMqQueues;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VendorQueueConfig {
    /** An admin activated a vendor: its owner becomes a VENDOR (consumed by Auth). */
    public static final String VENDOR_ACTIVATED_QUEUE = "vendor.activated.queue";

    @Bean
    public Queue vendorActivatedQueue() {
        return QueueBuilder.durable(VENDOR_ACTIVATED_QUEUE).build();
    }
}
