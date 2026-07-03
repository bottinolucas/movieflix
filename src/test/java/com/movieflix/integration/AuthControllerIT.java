package com.movieflix.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class AuthControllerIT extends AbstractIntegrationTest {

    @Test
    void shouldRegisterUserWithoutExposingPassword() throws Exception {
        Map<String, Object> payload = Map.of(
                "name", "Lucas",
                "email", "lucas-" + System.nanoTime() + "@teste.com",
                "password", "123456");

        mockMvc.perform(post("/movieflix/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Lucas"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldLoginAndReturnUsableJwtToken() throws Exception {
        String email = "login-" + System.nanoTime() + "@teste.com";
        String password = "123456";

        mockMvc.perform(post("/movieflix/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Lucas", "email", email, "password", password))))
                .andExpect(status().isCreated());

        String response = mockMvc.perform(post("/movieflix/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email, "password", password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(get("/movieflix/category")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectLoginWithWrongPassword() throws Exception {
        String email = "wrongpass-" + System.nanoTime() + "@teste.com";

        mockMvc.perform(post("/movieflix/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Lucas", "email", email, "password", "123456"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/movieflix/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email, "password", "senha-errada"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectLoginForUnknownEmail() throws Exception {
        mockMvc.perform(post("/movieflix/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "nao-existe@teste.com", "password", "123456"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectRequestWithInvalidToken() throws Exception {
        mockMvc.perform(get("/movieflix/category")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token-invalido"))
                .andExpect(status().isForbidden());
    }
}
