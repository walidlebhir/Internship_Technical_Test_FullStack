package com.backend.feature_flag_platform.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record FeatureResponse(
        Long id,
        String key,
        String description,
        Boolean enabled,
        UUID domainId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
