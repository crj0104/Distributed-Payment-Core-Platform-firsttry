package com.payment.core.common.config;

import com.payment.core.common.trace.TraceContext;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 自定义 RabbitTemplate，在发送消息时自动注入 TraceId
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(Objects.requireNonNull(connectionFactory, "connectionFactory must not be null"));
        template.setMessageConverter(Objects.requireNonNull(jsonMessageConverter(), "jsonMessageConverter must not be null"));
        
        // 发送前拦截，放入 TraceId
        template.setBeforePublishPostProcessors((MessagePostProcessor) message -> {
            String traceId = TraceContext.getTraceId();
            if (traceId != null) {
                message.getMessageProperties().setHeader(TraceContext.TRACE_ID_HEADER, traceId);
            }
            return message;
        });
        return template;
    }

    /**
     * 自定义消费者容器工厂，在消费消息前将 TraceId 放入 MDC
     */
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(Objects.requireNonNull(connectionFactory, "connectionFactory must not be null"));
        factory.setMessageConverter(Objects.requireNonNull(jsonMessageConverter(), "jsonMessageConverter must not be null"));
        
        // 消费前拦截
        factory.setAfterReceivePostProcessors((MessagePostProcessor) message -> {
            String traceId = message.getMessageProperties().getHeader(TraceContext.TRACE_ID_HEADER);
            if (traceId != null) {
                TraceContext.setTraceId(traceId);
            } else {
                TraceContext.setTraceId(TraceContext.generateTraceId());
            }
            return message;
        });
        return factory;
    }
}
