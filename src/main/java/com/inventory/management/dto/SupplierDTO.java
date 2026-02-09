package com.inventory.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String contact;

    @NotBlank
    private String address;
}
