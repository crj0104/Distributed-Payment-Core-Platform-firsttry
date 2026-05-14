package com.payment.core.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateMerchantRequestDTO {

    @NotBlank(message = "merchantName 不能为空")
    private String merchantName;

    private String notifyUrl;
}
