package com.payment.core.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.core.common.util.SignatureUtil;
import com.payment.core.gateway.config.ApiSignatureAuthProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class ApiSignatureAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ApiSignatureAuthFilter.class);
    private static final String HEADER_API_KEY = "X-Api-Key";
    private static final String HEADER_TIMESTAMP = "X-Timestamp";
    private static final String HEADER_NONCE = "X-Nonce";
    private static final String HEADER_SIGNATURE = "X-Signature";
    private static final String HEADER_CLIENT_ID = "X-Client-Id";
    private static final String HEADER_AUTH_VERIFIED = "X-Auth-Verified";
    private static final List<String> REQUIRED_HEADERS = List.of(
            HEADER_API_KEY, HEADER_TIMESTAMP, HEADER_NONCE, HEADER_SIGNATURE
    );

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final ApiSignatureAuthProperties authProperties;

    public ApiSignatureAuthFilter(ObjectMapper objectMapper,
                                  StringRedisTemplate redisTemplate,
                                  ApiSignatureAuthProperties authProperties) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
        this.authProperties = authProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (!authProperties.isEnabled() || isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        String apiKey = trimHeader(request, HEADER_API_KEY);
        String timestamp = trimHeader(request, HEADER_TIMESTAMP);
        String nonce = trimHeader(request, HEADER_NONCE);
        String signature = trimHeader(request, HEADER_SIGNATURE);

        String missingHeader = findMissingHeader(apiKey, timestamp, nonce, signature);
        if (missingHeader != null) {
            return unauthorized(exchange, "Missing required header: " + missingHeader);
        }

        String secret = authProperties.getClients().get(apiKey);
        if (!StringUtils.hasText(secret)) {
            log.warn("Unknown api key for path={}", path);
            return unauthorized(exchange, "Invalid API key");
        }

        if (!isTimestampValid(timestamp)) {
            return unauthorized(exchange, "Request timestamp is expired");
        }

        return readBody(exchange).flatMap(bodyBytes ->
                Mono.fromCallable(() -> validateSignature(request, bodyBytes, apiKey, nonce, timestamp, signature, secret))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(errorMessage -> {
                            if (errorMessage != null) {
                                log.warn("Signature auth rejected path={} apiKey={} reason={}", path, apiKey, errorMessage);
                                return unauthorized(exchange, errorMessage);
                            }

                            ServerHttpRequest decoratedRequest = decorateRequest(
                                    request,
                                    exchange.getResponse().bufferFactory(),
                                    bodyBytes,
                                    apiKey
                            );
                            return chain.filter(exchange.mutate().request(decoratedRequest).build());
                        }));
    }

    @Override
    public int getOrder() {
        return -60;
    }

    private Mono<byte[]> readBody(ServerWebExchange exchange) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .defaultIfEmpty(new byte[0]);
    }

    private String validateSignature(ServerHttpRequest request,
                                     byte[] bodyBytes,
                                     String apiKey,
                                     String nonce,
                                     String timestamp,
                                     String signature,
                                     String secret) {
        String canonicalPayload = buildCanonicalPayload(request, bodyBytes, timestamp, nonce);
        String expectedSignature = SignatureUtil.generate(canonicalPayload, secret);
        if (!safeEquals(expectedSignature, signature)) {
            return "Invalid request signature";
        }

        String nonceKey = buildNonceKey(apiKey, nonce);
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(
                        nonceKey,
                        Objects.requireNonNullElse(request.getURI().getPath(), ""),
                        authProperties.getNonceTtlSeconds(),
                        TimeUnit.SECONDS
                );
        if (!Boolean.TRUE.equals(acquired)) {
            return "Duplicate request detected";
        }

        return null;
    }

    private String buildCanonicalPayload(ServerHttpRequest request, byte[] bodyBytes, String timestamp, String nonce) {
        String method = request.getMethod() != null ? request.getMethod().name() : "GET";
        String path = request.getURI().getRawPath();
        String query = Optional.ofNullable(request.getURI().getRawQuery()).orElse("");
        String bodyHash = sha256Hex(bodyBytes);
        return String.join("\n", method, path, query, timestamp, nonce, bodyHash);
    }

    private String sha256Hex(byte[] bodyBytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bodyBytes);
            StringBuilder builder = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not supported", e);
        }
    }

    private boolean safeEquals(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8)
        );
    }

    private boolean isTimestampValid(String timestampHeader) {
        try {
            long requestTimestamp = Long.parseLong(timestampHeader);
            long currentTimestamp = Instant.now().toEpochMilli();
            long maxSkewMillis = authProperties.getTimestampSkewSeconds() * 1000;
            return Math.abs(currentTimestamp - requestTimestamp) <= maxSkewMillis;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isWhitelisted(String path) {
        return authProperties.getWhitelistPaths().stream().anyMatch(path::startsWith);
    }

    private String trimHeader(ServerHttpRequest request, String headerName) {
        String value = request.getHeaders().getFirst(Objects.requireNonNull(headerName, "headerName must not be null"));
        return value != null ? value.trim() : null;
    }

    private String findMissingHeader(String apiKey, String timestamp, String nonce, String signature) {
        String[] values = {apiKey, timestamp, nonce, signature};
        for (int i = 0; i < values.length; i++) {
            if (!StringUtils.hasText(values[i])) {
                return REQUIRED_HEADERS.get(i);
            }
        }
        return null;
    }

    private String buildNonceKey(String apiKey, String nonce) {
        return "payment:auth:nonce:" + apiKey + ":" + nonce;
    }

    private ServerHttpRequest decorateRequest(ServerHttpRequest request,
                                              DataBufferFactory bufferFactory,
                                              byte[] bodyBytes,
                                              String apiKey) {
        return new ServerHttpRequestDecorator(request) {
            @Override
            public @NonNull HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.putAll(super.getHeaders());
                headers.remove(HttpHeaders.CONTENT_LENGTH);
                headers.setContentLength(bodyBytes.length);
                headers.set(HEADER_CLIENT_ID, apiKey);
                headers.set(HEADER_AUTH_VERIFIED, "true");
                return headers;
            }

            @Override
            public @NonNull Flux<DataBuffer> getBody() {
                return Flux.just(bufferFactory.wrap(Objects.requireNonNull(bodyBytes, "bodyBytes must not be null")));
            }
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(@NonNull MediaType.APPLICATION_JSON);

        byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(Map.of(
                    "code", 401,
                    "message", message,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            body = ("{\"code\":401,\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        }

        DataBuffer buffer = response.bufferFactory().wrap(Objects.requireNonNull(body, "body must not be null"));
        return response.writeWith(Mono.just(buffer));
    }
}
