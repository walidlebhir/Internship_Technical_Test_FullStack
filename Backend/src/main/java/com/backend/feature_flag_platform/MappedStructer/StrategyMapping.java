package com.backend.feature_flag_platform.MappedStructer;

import com.backend.feature_flag_platform.DTO.StrategyResponse;
import com.backend.feature_flag_platform.Entity.Strategy;
import org.springframework.stereotype.Component;

@Component
public class StrategyMapping {

    /**
     * Maps a Strategy entity to a StrategyResponse DTO.
     * Only exposes the feature ID to keep the response lightweight.
     */
    public StrategyResponse mapStrategyToResponse(Strategy strategy) {
        return new StrategyResponse(
                strategy.getId(),
                strategy.getType(),
                strategy.getConfig(),
                strategy.getActive(),
                strategy.getFeature().getId()
        );
    }
}
