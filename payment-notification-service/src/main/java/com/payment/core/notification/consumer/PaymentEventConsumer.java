package com.payment.core.notification.consumer;

import com.payment.core.common.dto.event.PaymentStatusUpdatedEventDTO;
import com.payment.core.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final NotificationService notificationService;

    public PaymentEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "payment.notification.queue")
    public void handlePaymentEvent(PaymentStatusUpdatedEventDTO event) {
        log.info("========================================");
        log.info("Notification Service: Received MQ message");
        log.info("TransactionId: {}", event.getTransactionId());
        log.info("========================================");

        try {
            if (event.getTransactionId() == null) {
                log.warn("Notification: missing transactionId in event, skip");
                return;
            }

            if (event.getNotifyUrl() == null || event.getNotifyUrl().isEmpty()) {
                log.info("Notification: no notifyUrl configured for transactionId={}, skip", event.getTransactionId());
                return;
            }

            if (!"SUCCESS".equals(event.getStatus()) && !"FAILED".equals(event.getStatus())) {
                log.info("Notification: status={} is not terminal, skip notification", event.getStatus());
                return;
            }

            String notifyContent = buildNotifyContent(
                    event.getTransactionId(),
                    event.getOrderId(),
                    event.getStatus()
            );
            notificationService.sendNotification(event.getTransactionId(), event.getNotifyUrl(), notifyContent);

            log.info("Notification: sent for transactionId={} status={} url={}",
                    event.getTransactionId(), event.getStatus(), event.getNotifyUrl());

        } catch (Exception e) {
            log.error("Notification: failed to process MQ message", e);
        }
    }

    private String buildNotifyContent(String transactionId, String orderId, String status) {
        return String.format(
                "{\"transactionId\":\"%s\",\"orderId\":\"%s\",\"status\":\"%s\",\"timestamp\":\"%s\"}",
                transactionId,
                orderId != null ? orderId : "",
                status,
                java.time.Instant.now().toString()
        );
    }
}
