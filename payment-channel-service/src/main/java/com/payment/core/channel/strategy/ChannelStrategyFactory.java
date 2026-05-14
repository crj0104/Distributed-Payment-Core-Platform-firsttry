package com.payment.core.channel.strategy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelStrategyFactory implements ApplicationContextAware {

    private final Map<String, BasePayChannelStrategy> strategyMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) {
        Map<String, BasePayChannelStrategy> beans = context.getBeansOfType(BasePayChannelStrategy.class);
        beans.values().forEach(strategy -> strategyMap.put(strategy.getChannelCode(), strategy));
    }

    public BasePayChannelStrategy getStrategy(String channelCode) {
        BasePayChannelStrategy strategy = strategyMap.get(channelCode);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported channel: " + channelCode);
        }
        return strategy;
    }
}
