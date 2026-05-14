package com.payment.core.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class NotificationRabbitMQConfig {

    public static final String NOTIFICATION_EXCHANGE = "payment.exchange";
    public static final String NOTIFICATION_QUEUE = "payment.notification.queue";
    public static final String NOTIFICATION_ROUTING_KEY = "payment.status.updated";
    public static final String NOTIFICATION_DLX = "payment.notification.dlx";
    public static final String NOTIFICATION_DLQ = "payment.notification.dlq";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .deadLetterExchange(NOTIFICATION_DLX)
                .deadLetterRoutingKey(NOTIFICATION_DLQ)
                .build();
    }

    @Bean
    public Queue notificationDeadLetterQueue() {
        return QueueBuilder.durable(NOTIFICATION_DLQ).build();
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }
}
