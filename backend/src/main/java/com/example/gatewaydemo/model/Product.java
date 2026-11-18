package com.example.gatewaydemo.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record Product(Long id, String name, BigDecimal price, String description) {
    public record Input(@NotBlank String name, @NotNull @DecimalMin("0.0") BigDecimal price, String description) {}
}
