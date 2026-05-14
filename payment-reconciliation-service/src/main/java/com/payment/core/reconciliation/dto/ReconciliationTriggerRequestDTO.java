package com.payment.core.reconciliation.dto;

import lombok.Data;

@Data
public class ReconciliationTriggerRequestDTO {

    private String provider;
    private String date;
}
