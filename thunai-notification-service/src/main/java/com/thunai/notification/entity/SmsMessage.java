package com.thunai.notification.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sms_messages", schema = "notification_service")
@Data
@NoArgsConstructor
public class SmsMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "from_phone", nullable = false)
    private String fromPhone;

    @Column(name = "to_phone", nullable = false)
    private String toPhone;

    @Column(nullable = false, length = 1600)
    private String body;

    @Column(name = "direction", nullable = false)
    private String direction;

    @Column(name = "provider_message_id")
    private String providerMessageId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
