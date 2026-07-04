package com.backend.feature_flag_platform.evaluation_core;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link ContextProvider}.
 * <p>
 * Builds an {@link EvaluationContext} with {@link LocalDateTime#now()} as the
 * evaluation timestamp and copies any extra attributes into the context.
 * This implementation is replaceable by providing another {@code @Component}
 * that implements {@link ContextProvider}.
 */
@Component
public class DefaultContextProvider implements ContextProvider {

    @Override
    public EvaluationContext provide(
            String featureKey,
            String userId,
            String environment,
            Map<String, String> extraAttributes
    ) {
        return new EvaluationContext(featureKey, userId, environment, LocalDateTime.now(), copy(extraAttributes));
    }

    @Override
    public EvaluationContext provide(String featureKey, String userId, String environment) {
        return provide(featureKey, userId, environment, Map.of());
    }

    private Map<String, String> copy(Map<String, String> source) {
        return source == null ? Map.of() : new HashMap<>(source);
    }
}
