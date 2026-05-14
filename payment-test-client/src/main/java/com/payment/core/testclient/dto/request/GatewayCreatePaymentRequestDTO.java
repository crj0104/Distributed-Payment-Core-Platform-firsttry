package com.payment.core.testclient.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayCreatePaymentRequestDTO {

    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String idempotentKey;
    private String description;
    private String notifyUrl;
}
