package com.backend.feature_flag_platform.annotation;

import com.backend.feature_flag_platform.Entity.Enum.AuditAction;
import com.backend.feature_flag_platform.Entity.Enum.EntityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a service method as auditable.
 * <p>
 * When placed on a method, the {@code AuditAspect} automatically records
 * an audit entry after (or before) the method executes. The aspect extracts
 * the entity ID from the return value (for CREATE/UPDATE) or from the
 * method arguments (for DELETE).
 * </p>
 *
 * <pre>{@code
 * @Audited(entityType = EntityType.FEATURE, action = AuditAction.CREATE)
 * public FeatureResponse createFeature(FeatureRequest request) { ... }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {

    /**
     * The type of entity being modified (DOMAIN, FEATURE, STRATEGY).
     */
    EntityType entityType();

    /**
     * The operation being performed (CREATE, UPDATE, DELETE).
     */
    AuditAction action();
}
