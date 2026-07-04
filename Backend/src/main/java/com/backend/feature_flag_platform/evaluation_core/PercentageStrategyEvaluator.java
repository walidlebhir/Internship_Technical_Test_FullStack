package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;
import com.backend.feature_flag_platform.Service.RolloutService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Evaluates a PERCENTAGE strategy.
 * <p>Config format: {@code {"percentage": 50}}
 * <br>Logic: delegates to {@link RolloutService#isInRollout(String, String, int)}
 * which consistently maps a userId to a bucket 0‑99.</p>
 */
@Component
public class PercentageStrategyEvaluator implements StrategyEvaluator {

    private static final Logger log = LoggerFactory.getLogger(PercentageStrategyEvaluator.class);

    private final ObjectMapper objectMapper;
    private final RolloutService rolloutService;

    public PercentageStrategyEvaluator(ObjectMapper objectMapper, RolloutService rolloutService) {
        this.objectMapper = objectMapper;
        this.rolloutService = rolloutService;
    }

    @Override
    public StrategyType supportedType() {
        return StrategyType.PERCENTAGE;
    }

    @Override
    public boolean evaluate(String config, EvaluationContext context) {
        try {
            JsonNode root = objectMapper.readTree(config);
            int percentage = root.get("percentage").asInt();

            if (context.userId() == null || context.userId().isBlank()) {
                log.warn("PERCENTAGE strategy evaluated without a userId — returning false");
                return false;
            }

            return rolloutService.isInRollout(context.userId(), context.featureKey(), percentage);

        } catch (Exception e) {
            log.error("Failed to evaluate PERCENTAGE strategy: {}", e.getMessage(), e);
            return false;
        }
    }
}
