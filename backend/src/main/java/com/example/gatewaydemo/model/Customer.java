package com.example.gatewaydemo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record Customer(Long id, String name, String email, String phone, String address) {
    public record Input(@NotBlank String name, @NotBlank @Email String email,
                        @NotBlank String phone, @NotBlank String address) {}
}
