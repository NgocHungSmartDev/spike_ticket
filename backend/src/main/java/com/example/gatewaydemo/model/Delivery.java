package com.example.gatewaydemo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record Delivery(Long id, Long customerId, String destination, String status, String trackingCode) {
    public record Input(@NotNull @Positive Long customerId, @NotBlank String destination,
                        @NotBlank String status, String trackingCode) {}
}
