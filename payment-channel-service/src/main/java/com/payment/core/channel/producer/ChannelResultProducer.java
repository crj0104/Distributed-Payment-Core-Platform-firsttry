package com.payment.core.channel.producer;

import com.payment.core.common.dto.event.PaymentResultMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChannelResultProducer {

    private static final Logger log = LoggerFactory.getLogger(ChannelResultProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public ChannelResultProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void notifyTransactionService(PaymentResultMessageDTO result) {
        try {
            rabbitTemplate.convertAndSend("payment.exchange", "payment.result", result);
            log.info("Notified transaction service with payment result: {}", result.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to publish channel callback result", e);
        }
    }
}
