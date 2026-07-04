package com.backend.feature_flag_platform.aspect;

import com.backend.feature_flag_platform.Entity.Enum.AuditAction;
import com.backend.feature_flag_platform.Entity.Enum.EntityType;
import com.backend.feature_flag_platform.Service.AuditService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * AOP aspect that automatically records audit entries for all CRUD operations
 * on Domain, Feature, and Strategy services.
 * <p>
 * The aspect determines:
 * <ul>
 *   <li><b>Entity type</b> — from the service class name (e.g. {@code DomainService} → {@code DOMAIN})</li>
 *   <li><b>Action</b> — from the method name prefix (e.g. {@code create*} → {@code CREATE}, {@code enable*} → {@code UPDATE})</li>
 *   <li><b>Entity ID</b> — from the return value's {@code id()} component for CREATE/UPDATE,
 *       or from the method arguments for DELETE</li>
 * </ul>
 * This approach requires zero modifications to existing service classes.
 * </p>
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private static final String DEFAULT_WHO = "SYSTEM";

    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    /* =================================================================
     * Pointcuts — one per action family, across all CRUD services
     * ================================================================= */

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.DomainService.create*(..))")
    void domainCreate() {}

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.DomainService.update*(..))")
    void domainUpdate() {}

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.DomainService.delete*(..))")
    void domainDelete() {}

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.FeatureService.create*(..))")
    void featureCreate() {}

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.FeatureService.update*(..))")
    void featureUpdate() {}

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.FeatureService.delete*(..))")
    void featureDelete() {}

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.FeatureService.enable*(..))")
    void featureEnable() {}

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.FeatureService.disable*(..))")
    void featureDisable() {}

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.StrategyService.create*(..))")
    void strategyCreate() {}

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.StrategyService.update*(..))")
    void strategyUpdate() {}

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.StrategyService.delete*(..))")
    void strategyDelete() {}

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.StrategyService.enable*(..))")
    void strategyEnable() {}

    @Pointcut("execution(* com.backend.feature_flag_platform.Service.StrategyService.disable*(..))")
    void strategyDisable() {}

    /* =================================================================
     * CREATE advice
     * ================================================================= */

    @Around("domainCreate()")
    public Object auditDomainCreate(ProceedingJoinPoint pjp) throws Throwable {
        return audit(pjp, EntityType.DOMAIN, AuditAction.CREATE);
    }

    @Around("featureCreate()")
    public Object auditFeatureCreate(ProceedingJoinPoint pjp) throws Throwable {
        return audit(pjp, EntityType.FEATURE, AuditAction.CREATE);
    }

    @Around("strategyCreate()")
    public Object auditStrategyCreate(ProceedingJoinPoint pjp) throws Throwable {
        return audit(pjp, EntityType.STRATEGY, AuditAction.CREATE);
    }

    /* =================================================================
     * UPDATE advice
     * ================================================================= */

    @Around("domainUpdate()")
    public Object auditDomainUpdate(ProceedingJoinPoint pjp) throws Throwable {
        return audit(pjp, EntityType.DOMAIN, AuditAction.UPDATE);
    }

    @Around("featureUpdate() || featureEnable() || featureDisable()")
    public Object auditFeatureUpdate(ProceedingJoinPoint pjp) throws Throwable {
        return audit(pjp, EntityType.FEATURE, AuditAction.UPDATE);
    }

    @Around("strategyUpdate() || strategyEnable() || strategyDisable()")
    public Object auditStrategyUpdate(ProceedingJoinPoint pjp) throws Throwable {
        return audit(pjp, EntityType.STRATEGY, AuditAction.UPDATE);
    }

    /* =================================================================
     * DELETE advice
     * ================================================================= */

    @Around("domainDelete()")
    public Object auditDomainDelete(ProceedingJoinPoint pjp) throws Throwable {
        return audit(pjp, EntityType.DOMAIN, AuditAction.DELETE);
    }

    @Around("featureDelete()")
    public Object auditFeatureDelete(ProceedingJoinPoint pjp) throws Throwable {
        return audit(pjp, EntityType.FEATURE, AuditAction.DELETE);
    }

    @Around("strategyDelete()")
    public Object auditStrategyDelete(ProceedingJoinPoint pjp) throws Throwable {
        return audit(pjp, EntityType.STRATEGY, AuditAction.DELETE);
    }

    /* =================================================================
     * Core audit logic
     * ================================================================= */

    /**
     * Executes the target method and records an audit entry.
     * <p>
     * For CREATE and UPDATE: the entity ID is extracted from the returned DTO.
     * For DELETE: the entity ID is extracted from the method arguments before execution
     * (since the entity no longer exists afterward).
     */
    private Object audit(ProceedingJoinPoint pjp, EntityType entityType, AuditAction action) throws Throwable {
        String entityId = null;

        // For DELETE, extract the entity ID from method parameters before the entity is removed
        if (action == AuditAction.DELETE) {
            entityId = extractIdFromParams(pjp.getArgs());
        }

        // Execute the target method
        Object result = pjp.proceed();

        // For CREATE and UPDATE, extract the entity ID from the returned DTO
        if (action != AuditAction.DELETE) {
            entityId = extractIdFromResult(result);
        }

        // Record the audit entry if we successfully obtained an entity ID
        if (entityId != null) {
            auditService.record(action, entityType, entityId, DEFAULT_WHO);
            log.debug("Audit recorded: {} {} #{}", action, entityType, entityId);
        } else {
            log.warn("Could not extract entity ID for audit: {} {} — entry skipped", action, entityType);
        }

        return result;
    }

    /**
     * Searches method arguments for a {@link Long} or {@link UUID} identifier.
     * Used for DELETE methods where the entity ID is passed as a parameter.
     */
    private String extractIdFromParams(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Long longId) {
                return String.valueOf(longId);
            }
            if (arg instanceof UUID uuidId) {
                return uuidId.toString();
            }
        }
        return null;
    }

    /**
     * Extracts the entity ID from a method return value.
     * <p>
     * Works with:
     * <ul>
     *   <li>Java records that expose an {@code id()} component</li>
     *   <li>POJOs that expose a {@code getId()} getter</li>
     * </ul>
     */
    private String extractIdFromResult(Object result) {
        if (result == null) {
            return null;
        }
        try {
            java.lang.reflect.Method idMethod = result.getClass().getMethod("id");
            Object idValue = idMethod.invoke(result);
            return idValue == null ? null : idValue.toString();
        } catch (NoSuchMethodException e) {
            // Fallback: try getId() for non-record beans
            try {
                java.lang.reflect.Method getter = result.getClass().getMethod("getId");
                Object idValue = getter.invoke(result);
                return idValue == null ? null : idValue.toString();
            } catch (Exception ignored) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
