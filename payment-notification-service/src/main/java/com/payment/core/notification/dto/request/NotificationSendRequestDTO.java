package com.payment.core.notification.dto.request;

import lombok.Data;

@Data
public class NotificationSendRequestDTO {

    private String transactionId;
    private String notifyUrl;
    private String notifyContent;
}
