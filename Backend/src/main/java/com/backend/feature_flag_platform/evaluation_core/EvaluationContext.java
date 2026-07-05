package com.backend.feature_flag_platform.evaluation_core;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * Carries the runtime information needed to evaluate a feature flag.
 */
public record EvaluationContext(
        String featureKey,
        String userId,
        String environment,
        LocalDateTime currentDateTime,
        Map<String, String> attributes
) {
    public EvaluationContext(String featureKey, String userId, String environment, LocalDateTime currentDateTime) {
        this(featureKey, userId, environment, currentDateTime, Collections.emptyMap());
    }
}
