package com.backend.feature_flag_platform.Controllers;

import com.backend.feature_flag_platform.DTO.AuditResponse;
import com.backend.feature_flag_platform.Entity.Enum.AuditAction;
import com.backend.feature_flag_platform.Entity.Enum.EntityType;
import com.backend.feature_flag_platform.Service.AuditService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditController.class)
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditService auditService;

    @Test
    void shouldReturnAuditEntries() throws Exception {
        AuditResponse entry = new AuditResponse(
                1L, LocalDateTime.of(2025, 1, 1, 12, 0),
                AuditAction.CREATE, EntityType.FEATURE, "42", "admin"
        );
        Page<AuditResponse> page = new PageImpl<>(List.of(entry));
        when(auditService.findByEntityType(eq(null), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].action").value("CREATE"))
                .andExpect(jsonPath("$.content[0].entityType").value("FEATURE"))
                .andExpect(jsonPath("$.content[0].entityId").value("42"))
                .andExpect(jsonPath("$.content[0].who").value("admin"));
    }

    @Test
    void shouldFilterByEntityType() throws Exception {
        AuditResponse entry = new AuditResponse(
                2L, LocalDateTime.now(),
                AuditAction.UPDATE, EntityType.DOMAIN, "uuid-abc", "system"
        );
        Page<AuditResponse> page = new PageImpl<>(List.of(entry));
        when(auditService.findByEntityType(eq(EntityType.DOMAIN), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/audit")
                        .param("entityType", "DOMAIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].entityType").value("DOMAIN"));
    }

    @Test
    void shouldReturnEmptyPageWhenNoAuditEntries() throws Exception {
        when(auditService.findByEntityType(eq(null), any(PageRequest.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
