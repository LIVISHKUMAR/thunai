package com.thunai.notification.repository;

import com.thunai.notification.entity.SmsMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SmsMessageRepository extends JpaRepository<SmsMessage, UUID> {
    List<SmsMessage> findByTenantId(UUID tenantId);
    List<SmsMessage> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
}
