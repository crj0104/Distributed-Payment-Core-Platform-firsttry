package com.payment.core.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Sentinel 网关限流配置类
 * 当请求被 Sentinel 拦截（如触发 QPS 限流）时，返回自定义的 JSON 格式，而不是默认的 "Blocked by Sentinel"
 */
@Configuration
public class SentinelGatewayConfig {

    @PostConstruct
    public void initBlockHandler() {
        BlockRequestHandler blockRequestHandler = (exchange, t) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 429);
            map.put("message", "SYS_429_TOO_MANY_REQUESTS");
            map.put("detail", "网络拥挤，触发网关限流，请稍后再试");
            
            // 尝试获取 traceId
            String traceId = exchange.getAttribute("traceId");
            if (traceId != null) {
                map.put("traceId", traceId);
            }

            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                    .contentType(@NonNull MediaType.APPLICATION_JSON)
                    .bodyValue(map);
        };

        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
}
