package com.backend.feature_flag_platform.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record DomainResponse(
        UUID id,
        String name,
        String description,
        LocalDateTime createdAt
) {
}
