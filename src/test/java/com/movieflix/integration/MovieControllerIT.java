package com.movieflix.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class MovieControllerIT extends AbstractIntegrationTest {

    private String authorizationHeader;

    @BeforeEach
    void setUp() throws Exception {
        authorizationHeader = registerAndLogin();
    }

    private Long createCategory(String name) throws Exception {
        String response = mockMvc.perform(post("/movieflix/category")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", name))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private Long createStreaming(String name) throws Exception {
        String response = mockMvc.perform(post("/movieflix/streaming")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", name))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void shouldRejectAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/movieflix/movie"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreateMovieWithCategoriesAndStreamingsPopulated() throws Exception {
        Long categoryId = createCategory("Action");
        Long streamingId = createStreaming("Netflix");

        Map<String, Object> payload = Map.of(
                "title", "Duna",
                "description", "Ficcao cientifica",
                "releaseDate", "22/10/2021",
                "rating", 8.5,
                "categories", List.of(categoryId),
                "streamings", List.of(streamingId));

        mockMvc.perform(post("/movieflix/movie")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Duna"))
                .andExpect(jsonPath("$.categories[0].name").value("Action"))
                .andExpect(jsonPath("$.streamings[0].name").value("Netflix"));
    }

    @Test
    void shouldFindMoviesByCategory() throws Exception {
        Long categoryId = createCategory("Fiction");

        Map<String, Object> payload = Map.of(
                "title", "Interestelar",
                "description", "Viagem espacial",
                "releaseDate", "07/11/2014",
                "rating", 9.0,
                "categories", List.of(categoryId),
                "streamings", List.of());

        mockMvc.perform(post("/movieflix/movie")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/movieflix/movie/category/" + categoryId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Interestelar"));
    }

    @Test
    void shouldUpdateExistingMovie() throws Exception {
        Map<String, Object> createPayload = Map.of(
                "title", "Titulo antigo",
                "description", "Descricao antiga",
                "releaseDate", "01/01/2020",
                "rating", 5.0,
                "categories", List.of(),
                "streamings", List.of());

        String response = mockMvc.perform(post("/movieflix/movie")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long movieId = objectMapper.readTree(response).get("id").asLong();

        Map<String, Object> updatePayload = Map.of(
                "title", "Titulo novo",
                "description", "Descricao nova",
                "releaseDate", "01/01/2024",
                "rating", 9.0,
                "categories", List.of(),
                "streamings", List.of());

        mockMvc.perform(put("/movieflix/movie/" + movieId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Titulo novo"))
                .andExpect(jsonPath("$.rating").value(9.0));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentMovie() throws Exception {
        Map<String, Object> updatePayload = Map.of(
                "title", "x",
                "description", "x",
                "releaseDate", "01/01/2024",
                "rating", 1.0,
                "categories", List.of(),
                "streamings", List.of());

        mockMvc.perform(put("/movieflix/movie/999999")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteMovie() throws Exception {
        Map<String, Object> createPayload = Map.of(
                "title", "Filme a deletar",
                "description", "x",
                "releaseDate", "01/01/2020",
                "rating", 5.0,
                "categories", List.of(),
                "streamings", List.of());

        String response = mockMvc.perform(post("/movieflix/movie")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long movieId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/movieflix/movie/" + movieId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/movieflix/movie/" + movieId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isNotFound());
    }
}
