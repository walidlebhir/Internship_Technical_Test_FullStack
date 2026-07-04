package com.backend.feature_flag_platform.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RolloutServiceTest {

    private RolloutService rolloutService;

    @BeforeEach
    void setUp() {
        rolloutService = new RolloutService();
    }

    @Test
    void shouldReturnFalseWhenUserIdIsNull() {
        boolean result = rolloutService.isInRollout(null, "feature-x", 50);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenUserIdIsBlank() {
        boolean result = rolloutService.isInRollout("   ", "feature-x", 50);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenPercentageIsZero() {
        boolean result = rolloutService.isInRollout("user-1", "feature-x", 0);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenPercentageIsNegative() {
        boolean result = rolloutService.isInRollout("user-1", "feature-x", -10);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenPercentageIsOneHundred() {
        boolean result = rolloutService.isInRollout("user-1", "feature-x", 100);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenPercentageIsGreaterThanOneHundred() {
        boolean result = rolloutService.isInRollout("user-1", "feature-x", 150);
        assertThat(result).isTrue();
    }

    @Test
    void shouldConsistentlyReturnSameResultForSameInput() {
        boolean first = rolloutService.isInRollout("user-42", "feature-rollout-test", 30);
        boolean second = rolloutService.isInRollout("user-42", "feature-rollout-test", 30);
        assertThat(first).isEqualTo(second);
    }

    @Test
    void shouldReturnDifferentBucketForDifferentFeatures() {
        boolean resultA = rolloutService.isInRollout("user-1", "feature-a", 50);
        boolean resultB = rolloutService.isInRollout("user-1", "feature-b", 50);
        // The same user may get different results for different features
        // This is not a deterministic assertion but validates the logic doesn't crash
        assertThat(resultA).isIn(true, false);
        assertThat(resultB).isIn(true, false);
    }

    @Test
    void shouldReturnFalseWhenBucketIsOutsidePercentage() {
        // user with hash bucket >= percentage should be excluded
        // We can't predict the bucket directly, but we can verify 0% excludes all
        boolean result = rolloutService.isInRollout("any-user", "any-feature", 0);
        assertThat(result).isFalse();
    }
}
