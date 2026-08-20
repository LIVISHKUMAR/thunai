package com.thunai.notification.gateway;

import com.thunai.notification.config.SmsGatewayProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sends SMS through the capcom6 "SMS Gateway for Android" HTTP API.
 *
 * <p>POSTs to {@code {base-url}/messages} with HTTP Basic auth. The same call
 * works for the public cloud server and a self-hosted/local server.
 *
 * @see <a href="https://docs.sms-gate.app/features/sending-messages/">Sending messages</a>
 */
@Component
public class SmsGatewayClient {

    private static final Logger log = LoggerFactory.getLogger(SmsGatewayClient.class);

    private final SmsGatewayProperties props;
    private final RestClient restClient;

    public SmsGatewayClient(SmsGatewayProperties props) {
        this.props = props;
        this.restClient = RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .build();
    }

    /** Result of an enqueue attempt against the gateway. */
    public record SendResult(boolean success, String providerMessageId, String state, String error) {}

    /**
     * Enqueue a text SMS for delivery. The gateway returns immediately with a
     * message id and a {@code Pending} state; actual delivery happens on the device.
     */
    public SendResult send(String toPhone, String body) {
        if (!props.isEnabled()) {
            log.warn("SMS gateway disabled (sms.gateway.enabled=false) — not dispatching SMS to {}", toPhone);
            return new SendResult(false, null, "DISABLED", "SMS gateway disabled");
        }

        Map<String, Object> request = new HashMap<>();
        request.put("textMessage", Map.of("text", body));
        request.put("phoneNumbers", List.of(toPhone));
        if (props.getSimNumber() != null) {
            request.put("simNumber", props.getSimNumber());
        }

        try {
            MessageResponse response = restClient.post()
                    .uri(props.getSendPath())
                    .headers(h -> h.setBasicAuth(props.getUsername(), props.getPassword()))
                    .body(request)
                    .retrieve()
                    .body(MessageResponse.class);

            String id = response != null ? response.id() : null;
            String state = response != null ? response.state() : null;
            log.info("SMS enqueued via gateway: id={} state={} to={}", id, state, toPhone);
            return new SendResult(true, id, state, null);
        } catch (Exception e) {
            log.error("Failed to send SMS via gateway to {}: {}", toPhone, e.getMessage());
            return new SendResult(false, null, "FAILED", e.getMessage());
        }
    }

    /** Subset of the gateway's message response we care about. */
    private record MessageResponse(String id, String state) {}
}
