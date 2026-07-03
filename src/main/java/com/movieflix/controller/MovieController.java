package com.movieflix.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping
    public ResponseEntity<MovieResponse> save(@RequestBody MovieRequest request) {
        Movie savedMovie = movieService.saveMovie(MovieMapper.toMovie(request));

        return ResponseEntity.ok(MovieMapper.toMovieResponse(savedMovie));   
    }

}
