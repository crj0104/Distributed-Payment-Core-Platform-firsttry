package com.payment.core.channel.strategy;

import com.payment.core.common.dto.canonical.UnifiedPayRequest;
import com.payment.core.common.dto.canonical.UnifiedPayResponse;
import com.payment.core.common.dto.canonical.UnifiedRefundRequest;
import com.payment.core.common.dto.canonical.UnifiedRefundResponse;
import com.payment.core.common.dto.canonical.UnifiedCallbackDTO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 底层真实支付渠道接口 (Canonical Protocol)
 */
public interface BasePayChannelStrategy {

    String getChannelCode();

    UnifiedPayResponse charge(UnifiedPayRequest request);

    UnifiedRefundResponse refund(UnifiedRefundRequest request);

    /**
     * 外部回调验签与解析，返回统一回调模型
     */
    UnifiedCallbackDTO parseAndVerifyCallback(HttpServletRequest request);
}
