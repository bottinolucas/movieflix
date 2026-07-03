package com.movieflix.integration;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class StreamingControllerIT extends AbstractIntegrationTest {

    private String authorizationHeader;

    @BeforeEach
    void setUp() throws Exception {
        authorizationHeader = registerAndLogin();
    }

    @Test
    void shouldRejectAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/movieflix/streaming"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreateFindListAndDeleteStreaming() throws Exception {
        Map<String, Object> payload = Map.of("name", "Netflix");

        String response = mockMvc.perform(post("/movieflix/streaming")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Netflix"))
                .andReturn().getResponse().getContentAsString();

        Long streamingId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/movieflix/streaming/" + streamingId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Netflix"));

        mockMvc.perform(get("/movieflix/streaming")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(delete("/movieflix/streaming/" + streamingId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/movieflix/streaming/" + streamingId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isNotFound());
    }
}
