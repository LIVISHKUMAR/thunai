package com.thunai.customer.repository;

import com.thunai.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByPhoneAndTenantId(String phone, UUID tenantId);
    boolean existsByPhone(String phone);
}
