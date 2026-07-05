package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Evaluates an ENVIRONMENT strategy.
 * Config format: {@code {"environments": ["production", "staging"]}}
 * Logic: the feature is ON if the current environment matches one of the listed values.
 */
@Component
public class EnvironmentStrategyEvaluator implements StrategyEvaluator {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentStrategyEvaluator.class);

    private final ObjectMapper objectMapper;

    public EnvironmentStrategyEvaluator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public StrategyType supportedType() {
        return StrategyType.ENVIRONMENT;
    }

    @Override
    public boolean evaluate(String config, EvaluationContext context) {
        try {
            JsonNode root = objectMapper.readTree(config);
            JsonNode environmentsNode = root.get("environments");

            if (context.environment() == null || context.environment().isBlank()) {
                log.warn("ENVIRONMENT strategy evaluated without an environment — returning false");
                return false;
            }

            for (JsonNode element : environmentsNode) {
                if (context.environment().equals(element.asText())) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            log.error("Failed to evaluate ENVIRONMENT strategy: {}", e.getMessage(), e);
            return false;
        }
    }
}
