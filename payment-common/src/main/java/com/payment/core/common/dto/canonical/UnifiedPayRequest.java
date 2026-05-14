package com.payment.core.common.dto.canonical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 统一支付请求模型 (Canonical Model)
 * 无论底层是微信、支付宝还是 PayPal，交易服务只构造此对象。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedPayRequest {
    private String transactionId;
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String channelCode; // WECHAT, ALIPAY, etc.
    private String clientIp;
    private String description;
    
    // 附加扩展参数，供特定渠道使用的特殊字段
    private Map<String, Object> extraParams;
}