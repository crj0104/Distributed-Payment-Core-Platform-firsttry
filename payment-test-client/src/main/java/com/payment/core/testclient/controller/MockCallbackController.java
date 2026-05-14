package com.payment.core.testclient.controller;

import com.payment.core.testclient.dto.request.MockPaymentNotifyRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟商户端：接收支付中台异步通知（Callback）的控制器
 */
@Slf4j
@RestController
@RequestMapping("/mock/callback")
public class MockCallbackController {

    /**
     * 接收支付中台的支付结果通知
     * 实际业务中，商户需要在这里：
     * 1. 验签（如果中台发出的通知带签名）
     * 2. 更新商户侧订单状态（如发货）
     * 3. 返回 "SUCCESS" 告诉中台已成功接收，避免中台重试
     */
    @PostMapping("/payment-notify")
    public String receivePaymentNotify(@RequestBody MockPaymentNotifyRequestDTO payload) {
        log.info("==========================================");
        log.info("电商平台(Mock)收到支付中台异步通知:");
        log.info("Payload: {}", payload);
        log.info("==========================================");

        String transactionId = payload.getTransactionId();
        String status = payload.getStatus();

        if ("SUCCESS".equals(status)) {
            log.info("支付成功！电商开始执行发货逻辑... transactionId={}", transactionId);
            // TODO: 更新电商订单状态 -> 已支付，待发货
        } else {
            log.info("支付失败或异常状态: {}，电商订单保持或取消... transactionId={}", status, transactionId);
        }

        // 返回 SUCCESS 给支付中台，表示商户已成功接收回调
        return "SUCCESS";
    }
}
