package com.payment.core.channel.controller;

import com.payment.core.channel.producer.ChannelResultProducer;
import com.payment.core.channel.strategy.BasePayChannelStrategy;
import com.payment.core.channel.strategy.ChannelStrategyFactory;
import com.payment.core.common.dto.canonical.UnifiedCallbackDTO;
import com.payment.core.common.dto.event.PaymentResultMessageDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/callbacks/provider")
public class ExternalCallbackController {

    private static final Logger log = LoggerFactory.getLogger(ExternalCallbackController.class);

    private final ChannelStrategyFactory channelStrategyFactory;
    private final ChannelResultProducer channelResultProducer;

    public ExternalCallbackController(ChannelStrategyFactory channelStrategyFactory,
                                      ChannelResultProducer channelResultProducer) {
        this.channelStrategyFactory = channelStrategyFactory;
        this.channelResultProducer = channelResultProducer;
    }

    /**
     * 接收所有第三方的原生回调
     */
    @PostMapping("/{provider}")
    public ResponseEntity<String> handleCallback(@PathVariable String provider, HttpServletRequest request) {
        log.info("Received raw external callback from provider: {}", provider);

        try {
            // 1. 获取对应的渠道策略
            BasePayChannelStrategy strategy = channelStrategyFactory.getStrategy(provider.toUpperCase());

            // 2. 策略内部进行专属的：读流、解密、验签、提取参数
            UnifiedCallbackDTO callbackDto = strategy.parseAndVerifyCallback(request);

            // 3. 转换为标准消息 DTO 并发送给交易服务（MQ 解耦）
            PaymentResultMessageDTO standardResult = PaymentResultMessageDTO.builder()
                    .transactionId(callbackDto.getTransactionId())
                    .status(callbackDto.getStatus())
                    .providerTransactionId(callbackDto.getProviderTransactionId())
                    .rawData(callbackDto.getRawData())
                    .build();
            channelResultProducer.notifyTransactionService(standardResult);

            // 4. 返回各个渠道要求的成功应答格式（此处简化统一返回 SUCCESS，实际可能要返回特定 XML/JSON）
            return ResponseEntity.ok("SUCCESS");

        } catch (Exception e) {
            log.error("Failed to process provider callback: {}", provider, e);
            // 失败时不要给第三方返回成功，让其按规则重试
            return ResponseEntity.status(500).body("FAIL");
        }
    }
}
