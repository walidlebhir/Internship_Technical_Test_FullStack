package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;
import com.backend.feature_flag_platform.Service.RolloutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PercentageStrategyEvaluatorTest {

    @Mock
    private RolloutService rolloutService;

    private ObjectMapper objectMapper;
    private PercentageStrategyEvaluator evaluator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        evaluator = new PercentageStrategyEvaluator(objectMapper, rolloutService);
    }

    @Test
    void supportedTypeShouldBePERCENTAGE() {
        assertThat(evaluator.supportedType()).isEqualTo(StrategyType.PERCENTAGE);
    }

    @Test
    void shouldReturnTrueWhenUserIsInRollout() {
        EvaluationContext context = new EvaluationContext("feature-x", "user-1", "production", LocalDateTime.now());
        String config = "{\"percentage\": 50}";

        when(rolloutService.isInRollout(eq("user-1"), eq("feature-x"), eq(50))).thenReturn(true);

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserIsNotInRollout() {
        EvaluationContext context = new EvaluationContext("feature-x", "user-1", "production", LocalDateTime.now());
        String config = "{\"percentage\": 50}";

        when(rolloutService.isInRollout(eq("user-1"), eq("feature-x"), eq(50))).thenReturn(false);

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenUserIdIsNull() {
        EvaluationContext context = new EvaluationContext("feature-x", null, "production", LocalDateTime.now());
        String config = "{\"percentage\": 50}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenUserIdIsBlank() {
        EvaluationContext context = new EvaluationContext("feature-x", "   ", "production", LocalDateTime.now());
        String config = "{\"percentage\": 50}";

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
    void shouldReturnFalseWhenConfigIsMissingPercentage() {
        EvaluationContext context = new EvaluationContext("feature-x", "user-1", "production", LocalDateTime.now());
        String config = "{}";

        boolean result = evaluator.evaluate(config, context);

        assertThat(result).isFalse();
    }
}
