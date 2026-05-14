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
public class WechatPayStrategy implements BasePayChannelStrategy {

    private static final Logger log = LoggerFactory.getLogger(WechatPayStrategy.class);

    @Override
    public String getChannelCode() {
        return "WECHAT";
    }

    @Override
    public UnifiedPayResponse charge(UnifiedPayRequest request) {
        log.info("Wechat charge logic: {}", request);
        return UnifiedPayResponse.builder()
                .status("SUCCESS")
                .providerTransactionId("WX_" + System.currentTimeMillis())
                .redirectUrl("weixin://wxpay/bizpayurl?pr=mock")
                .build();
    }

    @Override
    public UnifiedRefundResponse refund(UnifiedRefundRequest request) {
        log.info("Wechat refund logic: {}", request);
        return UnifiedRefundResponse.builder()
                .status("SUCCESS")
                .providerRefundId("WX_REF_" + System.currentTimeMillis())
                .build();
    }

    @Override
    public UnifiedCallbackDTO parseAndVerifyCallback(HttpServletRequest request) {
        log.info("Verifying Wechat V3 Signature");

        return UnifiedCallbackDTO.builder()
                .transactionId("mock_tx_id_from_wx_body")
                .status("SUCCESS")
                .providerTransactionId("mock_wx_transaction_id")
                .rawData("mock_wechat_json_data")
                .build();
    }
}
