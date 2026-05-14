package com.payment.core.testclient.config;

import com.payment.core.testclient.util.TestSignatureUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * 模拟商户端：Feign 请求拦截器，自动为请求添加签名头
 */
@Slf4j
@Configuration
public class FeignAuthInterceptorConfig {

    @Value("${payment.client.api-key}")
    private String apiKey;

    @Value("${payment.client.secret}")
    private String secret;

    @Bean
    public RequestInterceptor signatureInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                String method = template.method();
                String path = template.path();
                String query = template.queryLine() != null ? template.queryLine().replace("?", "") : "";
                
                // 获取请求体字节数组
                byte[] bodyBytes = template.body() != null ? template.body() : new byte[0];

                String timestamp = String.valueOf(System.currentTimeMillis());
                String nonce = UUID.randomUUID().toString().replace("-", "");

                // 生成签名
                String signature = TestSignatureUtil.generateSignature(
                        method, path, query, bodyBytes, timestamp, nonce, secret
                );

                // 添加鉴权请求头
                template.header("X-Api-Key", apiKey);
                template.header("X-Timestamp", timestamp);
                template.header("X-Nonce", nonce);
                template.header("X-Signature", signature);

                log.info("发起支付网关请求: method={}, path={}, nonce={}", method, path, nonce);
            }
        };
    }
}
