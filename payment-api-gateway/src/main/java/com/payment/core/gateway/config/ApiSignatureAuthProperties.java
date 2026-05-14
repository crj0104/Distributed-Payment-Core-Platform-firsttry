package com.payment.core.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "payment.auth")
public class ApiSignatureAuthProperties {

    private boolean enabled = true;
    private long timestampSkewSeconds = 300;
    private long nonceTtlSeconds = 300;
    private List<String> whitelistPaths = new ArrayList<>();
    private Map<String, String> clients = new LinkedHashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getTimestampSkewSeconds() {
        return timestampSkewSeconds;
    }

    public void setTimestampSkewSeconds(long timestampSkewSeconds) {
        this.timestampSkewSeconds = timestampSkewSeconds;
    }

    public long getNonceTtlSeconds() {
        return nonceTtlSeconds;
    }

    public void setNonceTtlSeconds(long nonceTtlSeconds) {
        this.nonceTtlSeconds = nonceTtlSeconds;
    }

    public List<String> getWhitelistPaths() {
        return whitelistPaths;
    }

    public void setWhitelistPaths(List<String> whitelistPaths) {
        this.whitelistPaths = whitelistPaths;
    }

    public Map<String, String> getClients() {
        return clients;
    }

    public void setClients(Map<String, String> clients) {
        this.clients = clients;
    }
}
