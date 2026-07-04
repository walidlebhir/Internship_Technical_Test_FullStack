package com.backend.feature_flag_platform.evaluation_core;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * Carries the runtime information needed to evaluate a feature flag.
 * <ul>
 *   <li>{@code featureKey} — the unique key of the feature being evaluated</li>
 *   <li>{@code userId} — used by ALLOWLIST and PERCENTAGE strategies</li>
 *   <li>{@code environment} — used by ENVIRONMENT strategies</li>
 *   <li>{@code currentDateTime} — the timestamp of the evaluation, used by DATE strategies</li>
 *   <li>{@code attributes} — extensible key-value pairs for future strategy types</li>
 * </ul>
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
