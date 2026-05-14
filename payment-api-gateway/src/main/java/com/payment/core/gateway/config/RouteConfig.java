package com.payment.core.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("payment-channel-service", r -> r
                        .path("/api/v1/callbacks/provider/**")
                        .uri("lb://PAYMENT-CHANNEL-SERVICE"))
                .route("payment-transaction-service", r -> r
                        .path("/api/v1/transactions/**",
                                "/api/v1/payments/**",
                                "/api/v1/refunds/**",
                                "/api/v1/callbacks/**")
                        .uri("lb://PAYMENT-TRANSACTION-SERVICE"))
                .route("payment-reconciliation-service", r -> r
                        .path("/api/v1/reconciliations/**")
                        .uri("lb://PAYMENT-RECONCILIATION-SERVICE"))
                .route("payment-notification-service", r -> r
                        .path("/api/v1/notifications/**")
                        .uri("lb://PAYMENT-NOTIFICATION-SERVICE"))
                .route("payment-admin-service", r -> r
                        .path("/api/v1/admin/**")
                        .uri("lb://PAYMENT-ADMIN-SERVICE"))
                .build();
    }
}
