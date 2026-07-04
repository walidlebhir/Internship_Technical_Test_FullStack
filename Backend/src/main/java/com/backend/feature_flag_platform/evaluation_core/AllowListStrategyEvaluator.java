package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Evaluates an ALLOWLIST strategy.
 * <p>Config format: {@code {"userIds": ["alice", "bob"]}}
 * <br>Logic: the feature is ON if the current user ID appears in the list.</p>
 */
@Component
public class AllowListStrategyEvaluator implements StrategyEvaluator {

    private static final Logger log = LoggerFactory.getLogger(AllowListStrategyEvaluator.class);

    private final ObjectMapper objectMapper;

    public AllowListStrategyEvaluator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public StrategyType supportedType() {
        return StrategyType.ALLOWLIST;
    }

    @Override
    public boolean evaluate(String config, EvaluationContext context) {
        try {
            JsonNode root = objectMapper.readTree(config);
            JsonNode userIdsNode = root.get("userIds");

            if (context.userId() == null || context.userId().isBlank()) {
                log.warn("ALLOWLIST strategy evaluated without a userId — returning false");
                return false;
            }

            for (JsonNode element : userIdsNode) {
                if (context.userId().equals(element.asText())) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            log.error("Failed to evaluate ALLOWLIST strategy: {}", e.getMessage(), e);
            return false;
        }
    }
}
