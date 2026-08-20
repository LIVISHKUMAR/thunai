package com.thunai.tenant.dto;

import com.thunai.common.enums.SubscriptionPlan;
import com.thunai.common.enums.TenantStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateTenantRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String phone;

    private UUID ownerId;

    private SubscriptionPlan plan = SubscriptionPlan.TRIAL;
    private TenantStatus status = TenantStatus.TRIAL;
}
