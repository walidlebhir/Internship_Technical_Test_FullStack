package com.backend.feature_flag_platform.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DomainRequest(
        @NotBlank(message = "Domain name is required")
        @Size(min = 2, max = 100, message = "Domain name must be between 2 and 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description
) {
}
