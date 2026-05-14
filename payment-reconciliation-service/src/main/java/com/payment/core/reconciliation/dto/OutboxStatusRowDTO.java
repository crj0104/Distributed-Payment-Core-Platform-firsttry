package com.payment.core.reconciliation.dto;

import lombok.Data;

@Data
public class OutboxStatusRowDTO {

    private String aggregateId;
    private String status;
}
