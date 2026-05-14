package com.payment.core.testclient.dto.request;

import lombok.Data;

@Data
public class MockPaymentNotifyRequestDTO {

    private String transactionId;
    private String orderId;
    private String status;
    private String timestamp;
}
