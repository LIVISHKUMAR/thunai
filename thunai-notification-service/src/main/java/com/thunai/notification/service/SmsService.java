package com.thunai.notification.service;

import com.thunai.common.exception.NotFoundException;
import com.thunai.notification.entity.SmsMessage;
import com.thunai.notification.gateway.SmsGatewayClient;
import com.thunai.notification.repository.SmsMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SmsService {

    private final SmsMessageRepository smsMessageRepository;
    private final SmsGatewayClient smsGatewayClient;

    public SmsService(SmsMessageRepository smsMessageRepository, SmsGatewayClient smsGatewayClient) {
        this.smsMessageRepository = smsMessageRepository;
        this.smsGatewayClient = smsGatewayClient;
    }

    @Transactional
    public SmsMessage sendSms(String toPhone, String body, UUID tenantId) {
        SmsMessage message = new SmsMessage();
        message.setTenantId(tenantId);
        message.setFromPhone(""); // resolved from tenant config
        message.setToPhone(toPhone);
        message.setBody(body);
        message.setDirection("OUTBOUND");
        message.setStatus("PENDING");
        smsMessageRepository.save(message);

        // Dispatch via the capcom6 Android SMS gateway (free, self-hosted).
        SmsGatewayClient.SendResult result = smsGatewayClient.send(toPhone, body);
        if (result.success()) {
            message.setStatus("SENT");
            message.setProviderMessageId(result.providerMessageId());
        } else {
            message.setStatus("FAILED");
        }
        return smsMessageRepository.save(message);
    }

    @Transactional
    public SmsMessage receiveSms(String fromPhone, String toPhone, String body, UUID tenantId) {
        SmsMessage message = new SmsMessage();
        message.setTenantId(tenantId);
        message.setFromPhone(fromPhone);
        message.setToPhone(toPhone);
        message.setBody(body);
        message.setDirection("INBOUND");
        message.setStatus("RECEIVED");
        return smsMessageRepository.save(message);
    }

    public List<SmsMessage> getLogsByTenant(UUID tenantId) {
        return smsMessageRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    public SmsMessage getById(UUID id) {
        return smsMessageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("SMS not found: " + id));
    }
}
