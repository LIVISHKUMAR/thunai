package com.thunai.tenant.service;

import com.thunai.common.exception.NotFoundException;
import com.thunai.tenant.dto.CreateTenantRequest;
import com.thunai.tenant.entity.Tenant;
import com.thunai.tenant.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant createTenant(CreateTenantRequest request) {
        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setEmail(request.getEmail());
        tenant.setPhone(request.getPhone());
        tenant.setOwnerId(request.getOwnerId() != null ? request.getOwnerId() : UUID.randomUUID());
        tenant.setPlan(request.getPlan());
        tenant.setStatus(request.getStatus());
        return tenantRepository.save(tenant);
    }

    public Tenant getTenantById(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found: " + id));
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Transactional
    public void suspendTenant(UUID id) {
        Tenant tenant = getTenantById(id);
        tenant.setStatus(com.thunai.common.enums.TenantStatus.SUSPENDED);
        tenantRepository.save(tenant);
    }
}
