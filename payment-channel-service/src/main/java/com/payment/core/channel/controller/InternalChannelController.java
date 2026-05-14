package com.payment.core.channel.controller;

import com.payment.core.channel.strategy.BasePayChannelStrategy;
import com.payment.core.channel.strategy.ChannelStrategyFactory;
import com.payment.core.common.dto.canonical.UnifiedPayRequest;
import com.payment.core.common.dto.canonical.UnifiedPayResponse;
import com.payment.core.common.dto.canonical.UnifiedRefundRequest;
import com.payment.core.common.dto.canonical.UnifiedRefundResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/channel")
public class InternalChannelController {

    private final ChannelStrategyFactory channelStrategyFactory;

    public InternalChannelController(ChannelStrategyFactory channelStrategyFactory) {
        this.channelStrategyFactory = channelStrategyFactory;
    }

    @PostMapping("/charge")
    public UnifiedPayResponse charge(@RequestBody UnifiedPayRequest request) {
        BasePayChannelStrategy strategy = channelStrategyFactory.getStrategy(request.getChannelCode());
        return strategy.charge(request);
    }

    @PostMapping("/refund")
    public UnifiedRefundResponse refund(@RequestBody UnifiedRefundRequest request) {
        BasePayChannelStrategy strategy = channelStrategyFactory.getStrategy(request.getChannelCode());
        return strategy.refund(request);
    }
}
