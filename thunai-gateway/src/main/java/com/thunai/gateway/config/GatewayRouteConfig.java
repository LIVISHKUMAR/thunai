package com.thunai.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("tenant-service", r -> r
                        .path("/api/tenants/**", "/api/stores/**", "/api/categories/**", "/api/products/**", "/api/menu-tree/**", "/api/templates/**", "/api/subscriptions/**", "/api/analytics/tenant/**")
                        .filters(f -> f.rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}"))
                        .uri("lb://thunai-tenant-service"))
                .route("order-service", r -> r
                        .path("/api/carts/**", "/api/orders/**", "/api/delivery-slots/**")
                        .filters(f -> f.rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}"))
                        .uri("lb://thunai-order-service"))
                .route("customer-service", r -> r
                        .path("/api/customers/**", "/api/sessions/**", "/api/users/**", "/api/roles/**", "/api/permissions/**", "/api/addresses/**")
                        .filters(f -> f.rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}"))
                        .uri("lb://thunai-customer-service"))
                .route("notification-service", r -> r
                        .path("/api/sms/**", "/api/webhook/**")
                        .filters(f -> f.rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}"))
                        .uri("lb://thunai-notification-service"))
                .build();
    }
}
