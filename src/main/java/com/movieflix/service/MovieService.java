package com.movieflix.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.movieflix.entity.Category;
import com.movieflix.entity.Movie;
import com.movieflix.entity.Streaming;
import com.movieflix.repository.MovieRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final CategoryService categoryService;
    private final StreamingService streamingService;

    public Movie saveMovie(Movie movie) {
        movie.setCategories(findCategories(movie.getCategories()));
        movie.setStreamings(findStreamings(movie.getStreamings()));
        return movieRepository.save(movie);
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    public List<Movie> findByCategory(Long categoryId) {
        return movieRepository.findByCategories_Id(categoryId);
    }

    public Optional<Movie> updateMovie(Long id, Movie movie) {
        return movieRepository.findById(id).map(existingMovie -> {
            existingMovie.setTitle(movie.getTitle());
            existingMovie.setDescription(movie.getDescription());
            existingMovie.setReleaseDate(movie.getReleaseDate());
            existingMovie.setRating(movie.getRating());
            existingMovie.setCategories(findCategories(movie.getCategories()));
            existingMovie.setStreamings(findStreamings(movie.getStreamings()));
            return movieRepository.save(existingMovie);
        });
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    private List<Category> findCategories(List<Category> categories) {
        List<Category> categoriesFound = new ArrayList<>();
        for (Category category : categories) categoryService.findById(category.getId()).ifPresent(c -> categoriesFound.add(c));
        return categoriesFound;
    }

    private List<Streaming> findStreamings(List<Streaming> streamings) {
        List<Streaming> streamingsFound = new ArrayList<>();
        for (Streaming streaming : streamings) streamingService.findById(streaming.getId()).ifPresent(s -> streamingsFound.add(s));
        return streamingsFound;
    }
}
