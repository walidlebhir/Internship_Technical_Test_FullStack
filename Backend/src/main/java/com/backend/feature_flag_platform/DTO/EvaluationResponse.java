package com.backend.feature_flag_platform.DTO;

/**
 * DTO returned by the feature evaluation endpoint.
 * <p>
 * Contains the evaluated feature key, the requesting userId,
 * and the boolean result (ON/OFF) after applying all active strategies.
 */
public record EvaluationResponse(
        String featureKey,
        String userId,
        boolean enabled
) {
}
