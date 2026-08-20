package com.thunai.tenant.repository;

import com.thunai.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByPhone(String phone);
    Optional<Tenant> findByOwnerId(UUID ownerId);
    boolean existsByPhone(String phone);
}
