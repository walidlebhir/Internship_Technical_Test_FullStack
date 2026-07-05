package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;

/**
 * Strategy Pattern contract for evaluating a single strategy.
 * Each implementation handles exactly one {@link StrategyType}.
 */
public interface StrategyEvaluator {

    StrategyType supportedType();

    /**
     * Evaluates the strategy against the given runtime context.
     */
    boolean evaluate(String config, EvaluationContext context);
}
