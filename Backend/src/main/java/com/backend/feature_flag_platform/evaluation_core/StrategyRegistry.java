package com.backend.feature_flag_platform.evaluation_core;

import com.backend.feature_flag_platform.Entity.Enum.StrategyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Registry that maps each {@link StrategyType} to its corresponding {@link StrategyEvaluator}.
 * <p>Automatically collects all Spring-managed {@code StrategyEvaluator} beans at startup
 * and organises them by their {@link StrategyEvaluator#supportedType()}.
 * This is the only place where StrategyType is mapped — the engine never switches on type.</p>
 */
@Component
public class StrategyRegistry {

    private static final Logger log = LoggerFactory.getLogger(StrategyRegistry.class);

    private final Map<StrategyType, StrategyEvaluator> evaluators;

    public StrategyRegistry(List<StrategyEvaluator> evaluatorList) {
        Map<StrategyType, StrategyEvaluator> map = new EnumMap<>(StrategyType.class);
        for (StrategyEvaluator evaluator : evaluatorList) {
            StrategyType type = evaluator.supportedType();
            if (map.put(type, evaluator) != null) {
                log.warn("Duplicate evaluator registered for type {} — overwriting", type);
            }
            log.info("Registered {} for {}", evaluator.getClass().getSimpleName(), type);
        }
        this.evaluators = Collections.unmodifiableMap(map);
    }

    /**
     * Returns the evaluator registered for the given strategy type,
     * or {@code null} if no evaluator handles that type.
     */
    public StrategyEvaluator getEvaluator(StrategyType type) {
        return evaluators.get(type);
    }
}
