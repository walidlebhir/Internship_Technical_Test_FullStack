package com.backend.feature_flag_platform.aspect;

import com.backend.feature_flag_platform.Entity.Enum.AuditAction;
import com.backend.feature_flag_platform.Entity.Enum.EntityType;
import com.backend.feature_flag_platform.Service.AuditService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditAspectTest {

    @Mock
    private AuditService auditService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private AuditAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new AuditAspect(auditService);
    }

    @Test
    void shouldAuditCreateWithIdFromResult() throws Throwable {
        Object result = new Object() {
            public Long id() { return 42L; }
        };
        when(joinPoint.proceed()).thenReturn(result);

        Object returned = aspect.auditDomainCreate(joinPoint);

        assert returned == result;
        verify(auditService).record(AuditAction.CREATE, EntityType.DOMAIN, "42", "SYSTEM");
    }

    @Test
    void shouldAuditUpdateWithIdFromResult() throws Throwable {
        Object result = new Object() {
            public Long id() { return 99L; }
        };
        when(joinPoint.proceed()).thenReturn(result);

        aspect.auditFeatureUpdate(joinPoint);

        verify(auditService).record(AuditAction.UPDATE, EntityType.FEATURE, "99", "SYSTEM");
    }

    @Test
    void shouldAuditDeleteWithIdFromParams() throws Throwable {
        when(joinPoint.getArgs()).thenReturn(new Object[]{123L});
        when(joinPoint.proceed()).thenReturn(null);

        aspect.auditStrategyDelete(joinPoint);

        verify(auditService).record(AuditAction.DELETE, EntityType.STRATEGY, "123", "SYSTEM");
    }

    @Test
    void shouldHandleUuidIdFromParams() throws Throwable {
        UUID uuid = UUID.randomUUID();
        when(joinPoint.getArgs()).thenReturn(new Object[]{uuid});
        when(joinPoint.proceed()).thenReturn(null);

        aspect.auditDomainDelete(joinPoint);

        verify(auditService).record(AuditAction.DELETE, EntityType.DOMAIN, uuid.toString(), "SYSTEM");
    }

    @Test
    void shouldNotRecordAuditWhenEntityIdCannotBeExtracted() throws Throwable {
        when(joinPoint.getArgs()).thenReturn(new Object[]{"some-string"});
        when(joinPoint.proceed()).thenReturn(null);

        aspect.auditDomainDelete(joinPoint);

        verify(auditService, never()).record(any(), any(), any(), any());
    }

    @Test
    void shouldNotRecordAuditWhenResultIdIsNull() throws Throwable {
        Object result = new Object() {
            public Long id() { return null; }
        };
        when(joinPoint.proceed()).thenReturn(result);

        aspect.auditDomainCreate(joinPoint);

        verify(auditService, never()).record(any(), any(), any(), any());
    }
}
