package com.thunai.tenant.controller;

import com.thunai.common.dto.ApiResponse;
import com.thunai.tenant.dto.CreateTenantRequest;
import com.thunai.tenant.entity.Tenant;
import com.thunai.tenant.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Tenant>> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        Tenant tenant = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tenant created successfully", tenant));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Tenant>> getTenant(@PathVariable UUID id) {
        Tenant tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant fetched", tenant));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Tenant>>> getAllTenants() {
        List<Tenant> tenants = tenantService.getAllTenants();
        return ResponseEntity.ok(ApiResponse.success("Tenants fetched", tenants));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> suspendTenant(@PathVariable UUID id) {
        tenantService.suspendTenant(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant suspended"));
    }
}
