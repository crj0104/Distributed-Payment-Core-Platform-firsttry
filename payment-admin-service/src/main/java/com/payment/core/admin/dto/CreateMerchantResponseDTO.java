package com.payment.core.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMerchantResponseDTO {

    private String merchantName;
    private String apiKey;
    private String apiSecret;
    private String status;
}
