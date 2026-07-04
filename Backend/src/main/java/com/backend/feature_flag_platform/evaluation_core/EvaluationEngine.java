package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Core engine that evaluates a list of active strategies against a runtime context.
 * <p>Rules:
 * <ul>
 *   <li>If no active strategies exist → {@code true} (feature is ON)</li>
 *   <li>All active strategies must evaluate to {@code true} (AND logic)</li>
 *   <li>If any strategy evaluates to {@code false} → {@code false} (feature is OFF)</li>
 *   <li>If no evaluator is registered for a strategy type → {@code false}</li>
 * </ul>
 * The engine never switches or branches on {@code StrategyType};
 * it always delegates to the {@link StrategyRegistry}.</p>
 */
@Component
public class EvaluationEngine {

    private static final Logger log = LoggerFactory.getLogger(EvaluationEngine.class);

    private final StrategyRegistry registry;

    public EvaluationEngine(StrategyRegistry registry) {
        this.registry = registry;
    }

    /**
     * Evaluates all active strategies using AND logic.
     *
     * @param activeStrategies list of active strategies for a feature
     * @param context          the runtime evaluation context
     * @return {@code true} if the feature should be ON, {@code false} otherwise
     */
    public boolean evaluate(List<Strategy> activeStrategies, EvaluationContext context) {
        if (activeStrategies == null || activeStrategies.isEmpty()) {
            return true;
        }

        for (Strategy strategy : activeStrategies) {
            StrategyEvaluator evaluator = registry.getEvaluator(strategy.getType());

            if (evaluator == null) {
                log.warn("No evaluator found for strategy type {} (strategy id={}) — returning false",
                        strategy.getType(), strategy.getId());
                return false;
            }

            boolean result = evaluator.evaluate(strategy.getConfig(), context);
            if (!result) {
                return false;
            }
        }

        return true;
    }
}
