package com.backend.feature_flag_platform.Controllers;

import com.backend.feature_flag_platform.DTO.EvaluationResponse;
import com.backend.feature_flag_platform.Service.FeatureEvaluationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluationController.class)
class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeatureEvaluationService featureEvaluationService;

    @Test
    void shouldReturnEvaluationResponse() throws Exception {
        EvaluationResponse response = new EvaluationResponse("feature-x", "user-1", true);
        when(featureEvaluationService.evaluate("feature-x", "user-1", "production"))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/features/{key}/evaluate", "feature-x")
                        .param("userId", "user-1")
                        .param("environment", "production"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.featureKey").value("feature-x"))
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void shouldWorkWithoutOptionalParams() throws Exception {
        EvaluationResponse response = new EvaluationResponse("feature-x", null, true);
        when(featureEvaluationService.evaluate("feature-x", null, null))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/features/{key}/evaluate", "feature-x"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.featureKey").value("feature-x"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void shouldReturnOffWhenFeatureIsDisabled() throws Exception {
        EvaluationResponse response = new EvaluationResponse("feature-x", "user-1", false);
        when(featureEvaluationService.evaluate("feature-x", "user-1", "production"))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/features/{key}/evaluate", "feature-x")
                        .param("userId", "user-1")
                        .param("environment", "production"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false));
    }
}
