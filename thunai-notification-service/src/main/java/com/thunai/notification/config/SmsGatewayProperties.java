package com.thunai.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the capcom6 "SMS Gateway for Android" provider
 * (https://github.com/capcom6/android-sms-gateway).
 *
 * <p>This is a free, open-source way to send real SMS: an Android phone with a
 * SIM runs the gateway app and exposes an HTTP API. We send SMS by POSTing to
 * that API. Works against the public cloud server or a self-hosted/local server
 * — only {@code base-url} changes.
 *
 * <p>Values bind from environment variables via Spring relaxed binding, e.g.
 * {@code SMS_GATEWAY_USERNAME}, {@code SMS_GATEWAY_PASSWORD}.
 */
@ConfigurationProperties(prefix = "sms.gateway")
public class SmsGatewayProperties {

    /** When false, SMS are logged but not actually dispatched (safe default for dev). */
    private boolean enabled = false;

    /** Cloud root by default; for local mode use http://PHONE_IP:8080 */
    private String baseUrl = "https://api.sms-gate.app/3rdparty/v1";

    /** Send path relative to base-url. Cloud = "/messages", local server = "/message". */
    private String sendPath = "/messages";

    /** Basic-auth username configured in the Android gateway app. */
    private String username = "";

    /** Basic-auth password configured in the Android gateway app. */
    private String password = "";

    /** Optional SIM slot (1-based). Null = let the device pick the default SIM. */
    private Integer simNumber;

    /**
     * Tenant assigned to inbound SMS until a proper recipient-number → tenant
     * lookup is built. Required to accept webhook messages in single-tenant MVP.
     */
    private java.util.UUID defaultTenantId;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getSendPath() { return sendPath; }
    public void setSendPath(String sendPath) { this.sendPath = sendPath; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getSimNumber() { return simNumber; }
    public void setSimNumber(Integer simNumber) { this.simNumber = simNumber; }

    public java.util.UUID getDefaultTenantId() { return defaultTenantId; }
    public void setDefaultTenantId(java.util.UUID defaultTenantId) { this.defaultTenantId = defaultTenantId; }
}
