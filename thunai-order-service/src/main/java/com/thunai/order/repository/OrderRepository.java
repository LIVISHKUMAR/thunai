package com.thunai.order.repository;

import com.thunai.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerIdAndTenantId(UUID customerId, UUID tenantId);
    List<Order> findByTenantId(UUID tenantId);
}
