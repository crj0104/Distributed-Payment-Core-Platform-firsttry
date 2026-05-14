package com.payment.core.gateway.filter;

import com.payment.core.common.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 允许外部调用方传入 TraceId，如果没有则网关作为全链路起点生成
        String traceId = request.getHeaders().getFirst(TraceContext.TRACE_ID_HEADER);
        if (!StringUtils.hasText(traceId)) {
            traceId = TraceContext.generateTraceId();
        }
        
        exchange.getAttributes().put(TraceContext.TRACE_ID_KEY, traceId);

        log.info("[{}] {} {} from {}",
                traceId,
                request.getMethod(),
                request.getURI().getPath(),
                request.getRemoteAddress());

        // 将 TraceId 透传给下游微服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(TraceContext.TRACE_ID_HEADER, traceId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
