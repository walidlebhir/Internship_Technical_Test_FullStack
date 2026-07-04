package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;
import com.backend.feature_flag_platform.Entity.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationEngineTest {

    @Mock
    private StrategyRegistry registry;

    @Mock
    private StrategyEvaluator evaluator;

    private EvaluationEngine engine;

    private EvaluationContext context;

    @BeforeEach
    void setUp() {
        engine = new EvaluationEngine(registry);
        context = new EvaluationContext("feature-x", "user-1", "production", LocalDateTime.now());
    }

    @Test
    void shouldReturnTrueWhenNoActiveStrategies() {
        boolean result = engine.evaluate(null, context);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenEmptyActiveStrategies() {
        boolean result = engine.evaluate(List.of(), context);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenAllEvaluatorsReturnTrue() {
        Strategy s1 = Strategy.builder().id(1L).type(StrategyType.ALLOWLIST).config("{}").build();
        Strategy s2 = Strategy.builder().id(2L).type(StrategyType.ENVIRONMENT).config("{}").build();

        when(registry.getEvaluator(StrategyType.ALLOWLIST)).thenReturn(evaluator);
        when(registry.getEvaluator(StrategyType.ENVIRONMENT)).thenReturn(evaluator);
        when(evaluator.evaluate(anyString(), any(EvaluationContext.class))).thenReturn(true);

        boolean result = engine.evaluate(List.of(s1, s2), context);

        assertThat(result).isTrue();
        verify(evaluator, times(2)).evaluate(anyString(), any(EvaluationContext.class));
    }

    @Test
    void shouldReturnFalseWhenAnyEvaluatorReturnsFalse() {
        Strategy s1 = Strategy.builder().id(1L).type(StrategyType.ALLOWLIST).config("{}").build();
        Strategy s2 = Strategy.builder().id(2L).type(StrategyType.ENVIRONMENT).config("{}").build();

        when(registry.getEvaluator(StrategyType.ALLOWLIST)).thenReturn(evaluator);
        when(registry.getEvaluator(StrategyType.ENVIRONMENT)).thenReturn(evaluator);
        when(evaluator.evaluate(anyString(), any(EvaluationContext.class)))
                .thenReturn(true)
                .thenReturn(false);

        boolean result = engine.evaluate(List.of(s1, s2), context);

        assertThat(result).isFalse();
        verify(evaluator, times(2)).evaluate(anyString(), any(EvaluationContext.class));
    }

    @Test
    void shouldReturnFalseWhenNoEvaluatorRegisteredForType() {
        Strategy s1 = Strategy.builder().id(1L).type(StrategyType.DATE).config("{}").build();

        when(registry.getEvaluator(StrategyType.DATE)).thenReturn(null);

        boolean result = engine.evaluate(List.of(s1), context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenFirstStrategyFails() {
        Strategy s1 = Strategy.builder().id(1L).type(StrategyType.ALLOWLIST).config("{}").build();
        Strategy s2 = Strategy.builder().id(2L).type(StrategyType.ENVIRONMENT).config("{}").build();

        when(registry.getEvaluator(StrategyType.ALLOWLIST)).thenReturn(evaluator);
        when(evaluator.evaluate(anyString(), any(EvaluationContext.class))).thenReturn(false);

        boolean result = engine.evaluate(List.of(s1, s2), context);

        assertThat(result).isFalse();
        verify(registry, never()).getEvaluator(StrategyType.ENVIRONMENT);
    }
}
