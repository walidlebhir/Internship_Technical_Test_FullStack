package com.backend.feature_flag_platform.Service;

import org.springframework.stereotype.Service;

/**
 * Service responsible for determining whether a user falls within a given
 * percentage-based rollout for a specific feature.
 * <p>
 * Uses a consistent hash of the userId to assign the user to a bucket (0–99).
 * A user is "in the rollout" if their bucket is strictly less than the
 * configured percentage threshold.
 * </p>
 */
@Service
public class RolloutService {

    /**
     * Determines whether the given user is part of the percentage rollout
     * for the specified feature.
     *
     * @param userId     the identifier of the user being evaluated
     * @param featureKey the unique key of the feature flag
     * @param percentage the rollout percentage (0–100)
     * @return {@code true} if the user falls within the rollout, {@code false} otherwise
     */
    public boolean isInRollout(String userId, String featureKey, int percentage) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        if (percentage <= 0) {
            return false;
        }
        if (percentage >= 100) {
            return true;
        }

        // Combine userId and featureKey to create a stable, feature-specific hash.
        // This ensures the same user gets the same bucket for a given feature,
        // but may fall into a different bucket for a different feature.
        String hashInput = featureKey + "::" + userId;
        int bucket = Math.abs(hashInput.hashCode() % 100);
        return bucket < percentage;
    }
}
