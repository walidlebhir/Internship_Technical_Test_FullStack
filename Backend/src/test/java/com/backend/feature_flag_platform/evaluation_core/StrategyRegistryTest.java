package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StrategyRegistryTest {

    @Test
    void shouldRegisterSingleEvaluator() {
        StrategyEvaluator evaluator = mock(StrategyEvaluator.class);
        when(evaluator.supportedType()).thenReturn(StrategyType.PERCENTAGE);

        StrategyRegistry registry = new StrategyRegistry(List.of(evaluator));

        assertThat(registry.getEvaluator(StrategyType.PERCENTAGE)).isSameAs(evaluator);
        assertThat(registry.getEvaluator(StrategyType.ALLOWLIST)).isNull();
        assertThat(registry.getEvaluator(StrategyType.ENVIRONMENT)).isNull();
    }

    @Test
    void shouldRegisterMultipleEvaluators() {
        StrategyEvaluator percentage = mock(StrategyEvaluator.class);
        StrategyEvaluator allowlist = mock(StrategyEvaluator.class);
        when(percentage.supportedType()).thenReturn(StrategyType.PERCENTAGE);
        when(allowlist.supportedType()).thenReturn(StrategyType.ALLOWLIST);

        StrategyRegistry registry = new StrategyRegistry(List.of(percentage, allowlist));

        assertThat(registry.getEvaluator(StrategyType.PERCENTAGE)).isSameAs(percentage);
        assertThat(registry.getEvaluator(StrategyType.ALLOWLIST)).isSameAs(allowlist);
    }

    @Test
    void shouldReturnNullWhenNoEvaluatorsRegistered() {
        StrategyRegistry registry = new StrategyRegistry(List.of());

        assertThat(registry.getEvaluator(StrategyType.PERCENTAGE)).isNull();
        assertThat(registry.getEvaluator(StrategyType.ALLOWLIST)).isNull();
        assertThat(registry.getEvaluator(StrategyType.ENVIRONMENT)).isNull();
        assertThat(registry.getEvaluator(StrategyType.DATE)).isNull();
    }

    @Test
    void shouldHandleDuplicateRegistrationByOverwriting() {
        StrategyEvaluator first = mock(StrategyEvaluator.class);
        StrategyEvaluator second = mock(StrategyEvaluator.class);
        when(first.supportedType()).thenReturn(StrategyType.PERCENTAGE);
        when(second.supportedType()).thenReturn(StrategyType.PERCENTAGE);

        StrategyRegistry registry = new StrategyRegistry(List.of(first, second));

        assertThat(registry.getEvaluator(StrategyType.PERCENTAGE)).isSameAs(second);
    }

    @Test
    void shouldReturnMapUnmodifiable() {
        StrategyEvaluator evaluator = mock(StrategyEvaluator.class);
        when(evaluator.supportedType()).thenReturn(StrategyType.PERCENTAGE);

        StrategyRegistry registry = new StrategyRegistry(List.of(evaluator));

        assertThat(registry.getEvaluator(StrategyType.PERCENTAGE)).isSameAs(evaluator);
    }
}
