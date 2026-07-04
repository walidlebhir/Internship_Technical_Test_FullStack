package com.backend.feature_flag_platform.DTO;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;

public record StrategyResponse(
        Long id,
        StrategyType type,
        String config,
        Boolean active,
        Long featureId
) {
}
