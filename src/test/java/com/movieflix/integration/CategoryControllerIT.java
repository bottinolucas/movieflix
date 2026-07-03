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

class CategoryControllerIT extends AbstractIntegrationTest {

    private String authorizationHeader;

    @BeforeEach
    void setUp() throws Exception {
        authorizationHeader = registerAndLogin();
    }

    @Test
    void shouldRejectAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/movieflix/category"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreateFindListAndDeleteCategory() throws Exception {
        Map<String, Object> payload = Map.of("name", "Action");

        String response = mockMvc.perform(post("/movieflix/category")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Action"))
                .andReturn().getResponse().getContentAsString();

        Long categoryId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/movieflix/category/" + categoryId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Action"));

        mockMvc.perform(get("/movieflix/category")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(delete("/movieflix/category/" + categoryId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/movieflix/category/" + categoryId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isNotFound());
    }
}
