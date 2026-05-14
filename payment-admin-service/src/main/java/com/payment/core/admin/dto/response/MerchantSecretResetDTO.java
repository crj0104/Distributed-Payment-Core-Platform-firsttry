package com.payment.core.admin.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantSecretResetDTO {

    private String merchantNo;
    private String apiKey;
    private String apiSecret;
}
