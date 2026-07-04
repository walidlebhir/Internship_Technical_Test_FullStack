package com.backend.feature_flag_platform.Service;

import com.backend.feature_flag_platform.DTO.AuditResponse;
import com.backend.feature_flag_platform.Entity.AuditEntry;
import com.backend.feature_flag_platform.Entity.Enum.AuditAction;
import com.backend.feature_flag_platform.Entity.Enum.EntityType;
import com.backend.feature_flag_platform.MappedStructer.AuditMapping;
import com.backend.feature_flag_platform.Repository.AuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditRepository auditRepository;

    private AuditMapping auditMapping;
    private AuditService auditService;

    @Captor
    private ArgumentCaptor<AuditEntry> entryCaptor;

    @BeforeEach
    void setUp() {
        auditMapping = new AuditMapping();
        auditService = new AuditService(auditRepository, auditMapping);
    }

    @Test
    void shouldRecordAuditEntry() {
        auditService.record(AuditAction.CREATE, EntityType.FEATURE, "42", "admin");

        verify(auditRepository).save(entryCaptor.capture());
        AuditEntry saved = entryCaptor.getValue();

        assertThat(saved.getAction()).isEqualTo(AuditAction.CREATE);
        assertThat(saved.getEntityType()).isEqualTo(EntityType.FEATURE);
        assertThat(saved.getEntityId()).isEqualTo("42");
        assertThat(saved.getWho()).isEqualTo("admin");
    }

    @Test
    void shouldMapAuditEntryToResponseOnFindAll() {
        AuditEntry entry = AuditEntry.builder()
                .id(1L)
                .timestamp(LocalDateTime.of(2025, 1, 1, 12, 0))
                .action(AuditAction.UPDATE)
                .entityType(EntityType.DOMAIN)
                .entityId("uuid-123")
                .who("system")
                .build();
        Page<AuditEntry> page = new PageImpl<>(List.of(entry));
        Pageable pageable = PageRequest.of(0, 20);

        when(auditRepository.findAllByOrderByTimestampDesc(pageable)).thenReturn(page);

        Page<AuditResponse> result = auditService.findAll(pageable);

        assertThat(result).hasSize(1);
        AuditResponse response = result.getContent().get(0);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.action()).isEqualTo(AuditAction.UPDATE);
        assertThat(response.entityType()).isEqualTo(EntityType.DOMAIN);
        assertThat(response.entityId()).isEqualTo("uuid-123");
        assertThat(response.who()).isEqualTo("system");
    }

    @Test
    void shouldFilterByEntityType() {
        AuditEntry entry = AuditEntry.builder()
                .id(1L)
                .timestamp(LocalDateTime.now())
                .action(AuditAction.DELETE)
                .entityType(EntityType.STRATEGY)
                .entityId("99")
                .who("admin")
                .build();
        Page<AuditEntry> page = new PageImpl<>(List.of(entry));
        Pageable pageable = PageRequest.of(0, 20);

        when(auditRepository.findByEntityTypeOrderByTimestampDesc(EntityType.STRATEGY, pageable))
                .thenReturn(page);

        Page<AuditResponse> result = auditService.findByEntityType(EntityType.STRATEGY, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).entityType()).isEqualTo(EntityType.STRATEGY);
    }

    @Test
    void shouldDelegateToFindAllWhenEntityTypeIsNull() {
        Pageable pageable = PageRequest.of(0, 20);
        when(auditRepository.findAllByOrderByTimestampDesc(pageable)).thenReturn(Page.empty());

        Page<AuditResponse> result = auditService.findByEntityType(null, pageable);

        assertThat(result).isEmpty();
        verify(auditRepository).findAllByOrderByTimestampDesc(pageable);
        verify(auditRepository, never()).findByEntityTypeOrderByTimestampDesc(any(), any());
    }
}
