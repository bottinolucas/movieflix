package com.movieflix.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.movieflix.entity.Category;
import com.movieflix.entity.Movie;
import com.movieflix.entity.Streaming;
import com.movieflix.repository.MovieRepository;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private StreamingService streamingService;

    @InjectMocks
    private MovieService movieService;

    @Test
    void saveMovie_shouldResolveCategoriesAndStreamingsBeforeSaving() {
        Category categoryStub = Category.builder().id(1L).build();
        Streaming streamingStub = Streaming.builder().id(2L).build();
        Movie movie = Movie.builder()
                .title("Duna")
                .categories(List.of(categoryStub))
                .streamings(List.of(streamingStub))
                .build();

        Category resolvedCategory = Category.builder().id(1L).name("Fiction").build();
        Streaming resolvedStreaming = Streaming.builder().id(2L).name("HBO Max").build();
        when(categoryService.findById(1L)).thenReturn(Optional.of(resolvedCategory));
        when(streamingService.findById(2L)).thenReturn(Optional.of(resolvedStreaming));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Movie result = movieService.saveMovie(movie);

        assertThat(result.getCategories()).containsExactly(resolvedCategory);
        assertThat(result.getStreamings()).containsExactly(resolvedStreaming);
    }

    @Test
    void saveMovie_shouldIgnoreCategoriesAndStreamingsThatDoNotExist() {
        Movie movie = Movie.builder()
                .title("Duna")
                .categories(List.of(Category.builder().id(99L).build()))
                .streamings(List.of(Streaming.builder().id(99L).build()))
                .build();

        when(categoryService.findById(99L)).thenReturn(Optional.empty());
        when(streamingService.findById(99L)).thenReturn(Optional.empty());
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Movie result = movieService.saveMovie(movie);

        assertThat(result.getCategories()).isEmpty();
        assertThat(result.getStreamings()).isEmpty();
    }

    @Test
    void findByCategory_shouldDelegateToRepository() {
        Movie movie = Movie.builder().id(1L).title("Duna").build();
        when(movieRepository.findByCategories_Id(1L)).thenReturn(List.of(movie));

        List<Movie> result = movieService.findByCategory(1L);

        assertThat(result).containsExactly(movie);
    }

    @Test
    void updateMovie_shouldUpdateExistingMovieFieldsAndPreserveId() {
        Movie existingMovie = Movie.builder()
                .id(1L)
                .title("Titulo antigo")
                .description("Descricao antiga")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .rating(5.0)
                .categories(List.of())
                .streamings(List.of())
                .build();
        Movie incomingChanges = Movie.builder()
                .title("Titulo novo")
                .description("Descricao nova")
                .releaseDate(LocalDate.of(2024, 1, 1))
                .rating(9.0)
                .categories(List.of())
                .streamings(List.of())
                .build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Movie> result = movieService.updateMovie(1L, incomingChanges);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getTitle()).isEqualTo("Titulo novo");
        assertThat(result.get().getRating()).isEqualTo(9.0);
    }

    @Test
    void updateMovie_shouldReturnEmptyWhenMovieDoesNotExist() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Movie> result = movieService.updateMovie(99L, Movie.builder().build());

        assertThat(result).isEmpty();
    }

    @Test
    void deleteMovie_shouldDelegateToRepository() {
        movieService.deleteMovie(1L);

        verify(movieRepository, times(1)).deleteById(1L);
    }
}
