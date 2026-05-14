package com.payment.core.channel.strategy;

import com.payment.core.common.dto.canonical.UnifiedPayRequest;
import com.payment.core.common.dto.canonical.UnifiedPayResponse;
import com.payment.core.common.dto.canonical.UnifiedRefundRequest;
import com.payment.core.common.dto.canonical.UnifiedRefundResponse;
import com.payment.core.common.dto.canonical.UnifiedCallbackDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AlipayStrategy implements BasePayChannelStrategy {

    private static final Logger log = LoggerFactory.getLogger(AlipayStrategy.class);

    @Override
    public String getChannelCode() {
        return "ALIPAY";
    }

    @Override
    public UnifiedPayResponse charge(UnifiedPayRequest request) {
        log.info("Alipay charge logic: {}", request);
        return UnifiedPayResponse.builder()
                .status("SUCCESS")
                .providerTransactionId("ALI_" + System.currentTimeMillis())
                .redirectUrl("https://openapi.alipay.com/gateway.do?mock=1")
                .build();
    }

    @Override
    public UnifiedRefundResponse refund(UnifiedRefundRequest request) {
        log.info("Alipay refund logic: {}", request);
        return UnifiedRefundResponse.builder()
                .status("SUCCESS")
                .providerRefundId("ALI_REF_" + System.currentTimeMillis())
                .build();
    }

    @Override
    public UnifiedCallbackDTO parseAndVerifyCallback(HttpServletRequest request) {
        // 模拟验签与解析
        log.info("Alipay callback parse and verify");
        return UnifiedCallbackDTO.builder()
                .transactionId(request.getParameter("out_trade_no"))
                .status("TRADE_SUCCESS".equals(request.getParameter("trade_status")) ? "SUCCESS" : "FAILED")
                .providerTransactionId(request.getParameter("trade_no"))
                .rawData("mock_alipay_raw_data")
                .build();
    }
}
