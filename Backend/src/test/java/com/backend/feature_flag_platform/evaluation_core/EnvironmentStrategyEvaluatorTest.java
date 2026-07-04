package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentStrategyEvaluatorTest {

    private EnvironmentStrategyEvaluator evaluator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        evaluator = new EnvironmentStrategyEvaluator(objectMapper);
    }

    @Test
    void supportedTypeShouldBeENVIRONMENT() {
        assertThat(evaluator.supportedType()).isEqualTo(StrategyType.ENVIRONMENT);
    }

    @Test
    void shouldReturnTrueWhenEnvironmentMatches() {
        EvaluationContext context = new EvaluationContext("feature-x", "user-1", "production", LocalDateTime.now());
        String config = "{\"environments\": [\"production\", \"staging\"]}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEnvironmentDoesNotMatch() {
        EvaluationContext context = new EvaluationContext("feature-x", "user-1", "development", LocalDateTime.now());
        String config = "{\"environments\": [\"production\", \"staging\"]}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenEnvironmentIsNull() {
        EvaluationContext context = new EvaluationContext("feature-x", "user-1", null, LocalDateTime.now());
        String config = "{\"environments\": [\"production\", \"staging\"]}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenEnvironmentIsBlank() {
        EvaluationContext context = new EvaluationContext("feature-x", "user-1", "   ", LocalDateTime.now());
        String config = "{\"environments\": [\"production\", \"staging\"]}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenConfigIsInvalidJson() {
        EvaluationContext context = new EvaluationContext("feature-x", "user-1", "production", LocalDateTime.now());
        String config = "not-json";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenConfigIsMissingEnvironments() {
        EvaluationContext context = new EvaluationContext("feature-x", "user-1", "production", LocalDateTime.now());
        String config = "{}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }
}
