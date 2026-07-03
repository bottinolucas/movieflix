package com.movieflix.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Container "singleton": iniciado uma unica vez para toda a JVM e nunca parado
// explicitamente (o Ryuk do Testcontainers derruba ao final), evitando que o
// Spring reutilize o ApplicationContext em cache apontando para um container
// ja finalizado quando outra classe de teste roda em seguida.
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void overrideDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String registerAndLogin() throws Exception {
        String email = "user-" + UUID.randomUUID() + "@teste.com";
        String password = "123456";

        Map<String, Object> registerPayload = Map.of(
                "name", "Usuario Teste",
                "email", email,
                "password", password);

        mockMvc.perform(post("/movieflix/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isCreated());

        Map<String, Object> loginPayload = Map.of("email", email, "password", password);

        String response = mockMvc.perform(post("/movieflix/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return "Bearer " + json.get("token").asText();
    }
}
