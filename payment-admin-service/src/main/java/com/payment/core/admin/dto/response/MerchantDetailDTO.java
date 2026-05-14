package com.payment.core.admin.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MerchantDetailDTO {

    private String merchantNo;
    private String merchantName;
    private String apiKey;
    private String apiSecret;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
