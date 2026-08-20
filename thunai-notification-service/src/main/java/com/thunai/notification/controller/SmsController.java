package com.thunai.notification.controller;

import com.thunai.common.dto.ApiResponse;
import com.thunai.notification.config.SmsGatewayProperties;
import com.thunai.notification.entity.SmsMessage;
import com.thunai.notification.service.SmsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sms")
public class SmsController {

    private static final Logger log = LoggerFactory.getLogger(SmsController.class);

    private final SmsService smsService;
    private final SmsGatewayProperties gatewayProperties;

    public SmsController(SmsService smsService, SmsGatewayProperties gatewayProperties) {
        this.smsService = smsService;
        this.gatewayProperties = gatewayProperties;
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<SmsMessage>> sendSms(@Valid @RequestBody SmsSendRequest request) {
        SmsMessage message = smsService.sendSms(request.getToPhone(), request.getBody(), request.getTenantId());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("SMS queued for sending", message));
    }

    @GetMapping("/logs/{tenantId}")
    public ResponseEntity<ApiResponse<List<SmsMessage>>> getSmsLogs(@PathVariable UUID tenantId) {
        List<SmsMessage> logs = smsService.getLogsByTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success("SMS logs fetched", logs));
    }

    /**
     * Inbound webhook for the capcom6 SMS Gateway ("sms:received" event).
     * The Android gateway POSTs here whenever a customer SMS arrives.
     * Payload shape: https://docs.sms-gate.app/features/webhooks/
     */
    @PostMapping("/webhook/inbound")
    public ResponseEntity<Void> handleInboundSms(@RequestBody Map<String, Object> webhook) {
        Object event = webhook.get("event");
        if (!"sms:received".equals(event)) {
            log.debug("Ignoring non-received webhook event: {}", event);
            return ResponseEntity.ok().build();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) webhook.get("payload");
        if (payload == null) {
            return ResponseEntity.badRequest().build();
        }

        // "sender" is preferred; fall back to deprecated "phoneNumber".
        String from = str(payload.getOrDefault("sender", payload.get("phoneNumber")));
        String to = str(payload.get("recipient"));
        String body = str(payload.get("message"));

        UUID tenantId = gatewayProperties.getDefaultTenantId();
        if (tenantId == null) {
            // TODO: resolve tenant from the recipient phone number once a
            // tenant-number lookup exists. Until then a default tenant is required.
            log.warn("Dropping inbound SMS from {} — sms.gateway.default-tenant-id is not configured", from);
            return ResponseEntity.ok().build();
        }

        smsService.receiveSms(from, to, body, tenantId);
        return ResponseEntity.ok().build();
    }

    private static String str(Object value) {
        return value == null ? null : value.toString();
    }
}

class SmsSendRequest {
    private String toPhone;
    private String body;
    private java.util.UUID tenantId;

    public String getToPhone() { return toPhone; }
    public void setToPhone(String toPhone) { this.toPhone = toPhone; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public java.util.UUID getTenantId() { return tenantId; }
    public void setTenantId(java.util.UUID tenantId) { this.tenantId = tenantId; }
}
