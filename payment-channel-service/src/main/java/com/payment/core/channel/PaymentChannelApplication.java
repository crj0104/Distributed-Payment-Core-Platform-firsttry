package com.payment.core.channel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.payment.core.common", "com.payment.core.channel"})
public class PaymentChannelApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentChannelApplication.class, args);
    }
}
