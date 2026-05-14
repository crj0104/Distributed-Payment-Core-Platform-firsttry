package com.payment.core.admin.dto.request;

import lombok.Data;

@Data
public class ReconciliationTriggerRequestDTO {

    private String provider;

    private String date;
}
