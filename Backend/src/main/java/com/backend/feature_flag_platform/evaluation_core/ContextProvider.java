package com.backend.feature_flag_platform.evaluation_core;

import java.util.Map;

/**
 * Strategy interface for building an {@link EvaluationContext}.
 * <p>
 * Defining this as an interface allows different strategies for context creation
 * (e.g., extracting userId from a JWT token, adding request-scoped attributes, etc.)
 * without changing the evaluation engine or controllers.
 */
public interface ContextProvider {

    /**
     * Builds an evaluation context with the supplied feature key, user, environment, and extra attributes.
     */
    EvaluationContext provide(String featureKey, String userId, String environment, Map<String, String> extraAttributes);

    /**
     * Convenience method when no extra attributes are needed.
     */
    EvaluationContext provide(String featureKey, String userId, String environment);
}
