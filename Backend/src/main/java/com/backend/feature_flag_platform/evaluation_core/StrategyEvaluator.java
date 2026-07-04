package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;

/**
 * Strategy Pattern contract for evaluating a single strategy.
 * Each implementation handles exactly one {@link StrategyType}.
 */
public interface StrategyEvaluator {

    /**
     * Returns the strategy type this evaluator is responsible for.
     * Used by {@link StrategyRegistry} at startup to build the lookup map.
     */
    StrategyType supportedType();

    /**
     * Evaluates the strategy against the given runtime context.
     * <p>Must never throw an exception. If the config is invalid or an error
     * occurs the implementation must log the problem and return {@code false}.</p>
     *
     * @param config  the raw JSON config stored on the Strategy entity
     * @param context the runtime context (userId, environment, …)
     * @return {@code true} if the strategy matches, {@code false} otherwise
     */
    boolean evaluate(String config, EvaluationContext context);
}
