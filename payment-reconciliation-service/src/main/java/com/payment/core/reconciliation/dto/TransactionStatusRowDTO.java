package com.payment.core.reconciliation.dto;

import lombok.Data;

@Data
public class TransactionStatusRowDTO {

    private String transactionId;
    private String status;
}
