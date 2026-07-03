package com.movieflix.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movieflix.controller.request.MovieRequest;
import com.movieflix.controller.response.MovieResponse;
import com.movieflix.entity.Movie;
import com.movieflix.mapper.MovieMapper;
import com.movieflix.service.MovieService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/movieflix/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping 
    public ResponseEntity<List<MovieResponse>> findAll() {
        return ResponseEntity.ok(
            movieService.findAll()
            .stream()
            .map(movie -> MovieMapper.toMovieResponse(movie))
            .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> findById(@PathVariable Long id) {
        return movieService.findById(id)
                    .map(movie -> ResponseEntity.ok(MovieMapper.toMovieResponse(movie)))
                    .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MovieResponse> save(@RequestBody MovieRequest request) {
        Movie savedMovie = movieService.saveMovie(MovieMapper.toMovie(request));

        return ResponseEntity.ok(MovieMapper.toMovieResponse(savedMovie));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MovieResponse>> findByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(
            movieService.findByCategory(categoryId)
            .stream()
            .map(movie -> MovieMapper.toMovieResponse(movie))
            .toList()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> update(@PathVariable Long id, @RequestBody MovieRequest request) {
        return movieService.updateMovie(id, MovieMapper.toMovie(request))
                    .map(movie -> ResponseEntity.ok(MovieMapper.toMovieResponse(movie)))
                    .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @NonNull Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
