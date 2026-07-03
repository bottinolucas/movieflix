package com.movieflix.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.movieflix.controller.request.MovieRequest;
import com.movieflix.controller.response.MovieResponse;
import com.movieflix.entity.Category;
import com.movieflix.entity.Movie;
import com.movieflix.entity.Streaming;

class MovieMapperTest {

    @Test
    void toMovie_shouldMapScalarFieldsAndResolveCategoryAndStreamingIds() {
        MovieRequest request = new MovieRequest(
                "Duna",
                "Ficcao cientifica",
                LocalDate.of(2021, 10, 22),
                8.5,
                List.of(1L, 3L),
                List.of(2L));

        Movie movie = MovieMapper.toMovie(request);

        assertThat(movie.getTitle()).isEqualTo("Duna");
        assertThat(movie.getDescription()).isEqualTo("Ficcao cientifica");
        assertThat(movie.getReleaseDate()).isEqualTo(LocalDate.of(2021, 10, 22));
        assertThat(movie.getRating()).isEqualTo(8.5);
        assertThat(movie.getCategories()).extracting(Category::getId).containsExactly(1L, 3L);
        assertThat(movie.getStreamings()).extracting(Streaming::getId).containsExactly(2L);
    }

    @Test
    void toMovieResponse_shouldMapNestedCategoriesAndStreamings() {
        Movie movie = Movie.builder()
                .id(10L)
                .title("Duna")
                .description("Ficcao cientifica")
                .releaseDate(LocalDate.of(2021, 10, 22))
                .rating(8.5)
                .categories(List.of(Category.builder().id(1L).name("Fiction").build()))
                .streamings(List.of(Streaming.builder().id(2L).name("HBO Max").build()))
                .build();

        MovieResponse response = MovieMapper.toMovieResponse(movie);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo("Duna");
        assertThat(response.categories()).hasSize(1);
        assertThat(response.categories().get(0).name()).isEqualTo("Fiction");
        assertThat(response.streamings()).hasSize(1);
        assertThat(response.streamings().get(0).name()).isEqualTo("HBO Max");
    }
}
