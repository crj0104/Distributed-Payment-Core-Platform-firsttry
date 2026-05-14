package com.payment.core.common.dto.canonical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一回调解析模型 (Canonical Model)
 * 渠道服务解析第三方回调后，转化为这个标准模型给交易服务。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedCallbackDTO {
    private String transactionId;
    private String status; // SUCCESS, FAILED
    private String providerTransactionId;
    private String rawData;
}