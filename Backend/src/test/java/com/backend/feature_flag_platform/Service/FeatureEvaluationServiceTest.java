package com.backend.feature_flag_platform.Service;

import com.backend.feature_flag_platform.DTO.EvaluationResponse;
import com.backend.feature_flag_platform.Entity.Feature;
import com.backend.feature_flag_platform.Entity.Strategy;
import com.backend.feature_flag_platform.Repository.FeatureRepository;
import com.backend.feature_flag_platform.Repository.StrategyRepository;
import com.backend.feature_flag_platform.evaluation_core.ContextProvider;
import com.backend.feature_flag_platform.evaluation_core.EvaluationContext;
import com.backend.feature_flag_platform.evaluation_core.EvaluationEngine;
import com.backend.feature_flag_platform.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureEvaluationServiceTest {

    @Mock
    private FeatureRepository featureRepository;

    @Mock
    private StrategyRepository strategyRepository;

    @Mock
    private EvaluationEngine evaluationEngine;

    @Mock
    private ContextProvider contextProvider;

    private FeatureEvaluationService service;

    @BeforeEach
    void setUp() {
        service = new FeatureEvaluationService(
                featureRepository, strategyRepository, evaluationEngine, contextProvider
        );
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenFeatureNotFound() {
        when(featureRepository.findByKey("unknown-key")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.evaluate("unknown-key", "user-1", "production"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("unknown-key");
    }

    @Test
    void shouldReturnOffWhenFeatureIsDisabled() {
        Feature feature = Feature.builder()
                .id(1L).key("feature-x").enabled(false)
                .build();
        when(featureRepository.findByKey("feature-x")).thenReturn(Optional.of(feature));

        EvaluationResponse response = service.evaluate("feature-x", "user-1", "production");

        assertThat(response.enabled()).isFalse();
        assertThat(response.featureKey()).isEqualTo("feature-x");
        assertThat(response.userId()).isEqualTo("user-1");
    }

    @Test
    void shouldReturnOnWhenFeatureIsEnabledWithNoActiveStrategies() {
        Feature feature = Feature.builder()
                .id(1L).key("feature-x").enabled(true)
                .build();
        when(featureRepository.findByKey("feature-x")).thenReturn(Optional.of(feature));
        when(strategyRepository.findByFeatureIdAndActiveTrue(1L)).thenReturn(List.of());

        EvaluationResponse response = service.evaluate("feature-x", "user-1", "production");

        assertThat(response.enabled()).isTrue();
    }

    @Test
    void shouldDelegateToEngineWhenActiveStrategiesExist() {
        Feature feature = Feature.builder()
                .id(1L).key("feature-x").enabled(true)
                .build();
        Strategy strategy = Strategy.builder().id(1L).build();
        EvaluationContext context = new EvaluationContext("feature-x", "user-1", "production", LocalDateTime.now());

        when(featureRepository.findByKey("feature-x")).thenReturn(Optional.of(feature));
        when(strategyRepository.findByFeatureIdAndActiveTrue(1L)).thenReturn(List.of(strategy));
        when(contextProvider.provide("feature-x", "user-1", "production")).thenReturn(context);
        when(evaluationEngine.evaluate(List.of(strategy), context)).thenReturn(true);

        EvaluationResponse response = service.evaluate("feature-x", "user-1", "production");

        assertThat(response.enabled()).isTrue();
        verify(evaluationEngine).evaluate(List.of(strategy), context);
    }

    @Test
    void shouldReturnOffWhenEngineReturnsFalse() {
        Feature feature = Feature.builder()
                .id(1L).key("feature-x").enabled(true)
                .build();
        Strategy strategy = Strategy.builder().id(1L).build();
        EvaluationContext context = new EvaluationContext("feature-x", "user-1", "production", LocalDateTime.now());

        when(featureRepository.findByKey("feature-x")).thenReturn(Optional.of(feature));
        when(strategyRepository.findByFeatureIdAndActiveTrue(1L)).thenReturn(List.of(strategy));
        when(contextProvider.provide("feature-x", "user-1", "production")).thenReturn(context);
        when(evaluationEngine.evaluate(List.of(strategy), context)).thenReturn(false);

        EvaluationResponse response = service.evaluate("feature-x", "user-1", "production");

        assertThat(response.enabled()).isFalse();
    }

    @Test
    void shouldIncludeUserIdInResponse() {
        Feature feature = Feature.builder()
                .id(1L).key("feature-x").enabled(true)
                .build();
        when(featureRepository.findByKey("feature-x")).thenReturn(Optional.of(feature));
        when(strategyRepository.findByFeatureIdAndActiveTrue(1L)).thenReturn(List.of());

        EvaluationResponse response = service.evaluate("feature-x", "custom-user", "production");

        assertThat(response.userId()).isEqualTo("custom-user");
        assertThat(response.featureKey()).isEqualTo("feature-x");
    }
}
