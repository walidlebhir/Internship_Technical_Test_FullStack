package com.backend.feature_flag_platform.DTO;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;
import jakarta.validation.constraints.NotNull;

public record StrategyRequest(
        @NotNull(message = "Strategy type is required")
        StrategyType type,

        @NotNull(message = "Strategy config is required")
        String config,

        @NotNull(message = "Feature ID is required")
        Long id_feature
) {
}
