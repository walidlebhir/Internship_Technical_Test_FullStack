package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AllowListStrategyEvaluatorTest {

    private AllowListStrategyEvaluator evaluator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        evaluator = new AllowListStrategyEvaluator(objectMapper);
    }

    @Test
    void supportedTypeShouldBeALLOWLIST() {
        assertThat(evaluator.supportedType()).isEqualTo(StrategyType.ALLOWLIST);
    }

    @Test
    void shouldReturnTrueWhenUserIdIsInList() {
        EvaluationContext context = new EvaluationContext("feature-x", "alice", "production", LocalDateTime.now());
        String config = "{\"userIds\": [\"alice\", \"bob\"]}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserIdIsNotInList() {
        EvaluationContext context = new EvaluationContext("feature-x", "charlie", "production", LocalDateTime.now());
        String config = "{\"userIds\": [\"alice\", \"bob\"]}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenUserIdIsNull() {
        EvaluationContext context = new EvaluationContext("feature-x", null, "production", LocalDateTime.now());
        String config = "{\"userIds\": [\"alice\", \"bob\"]}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenUserIdIsBlank() {
        EvaluationContext context = new EvaluationContext("feature-x", "   ", "production", LocalDateTime.now());
        String config = "{\"userIds\": [\"alice\", \"bob\"]}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenConfigIsInvalidJson() {
        EvaluationContext context = new EvaluationContext("feature-x", "alice", "production", LocalDateTime.now());
        String config = "not-json";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenConfigIsMissingUserIds() {
        EvaluationContext context = new EvaluationContext("feature-x", "alice", "production", LocalDateTime.now());
        String config = "{}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenUserIdsArrayIsEmpty() {
        EvaluationContext context = new EvaluationContext("feature-x", "alice", "production", LocalDateTime.now());
        String config = "{\"userIds\": []}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }
}
