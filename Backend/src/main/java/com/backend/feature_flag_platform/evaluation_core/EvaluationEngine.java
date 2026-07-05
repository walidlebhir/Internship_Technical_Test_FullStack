package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Core engine that evaluates a list of active strategies against a runtime context.
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
