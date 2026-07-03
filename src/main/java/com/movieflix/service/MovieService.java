package com.movieflix.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.movieflix.entity.Movie;
import com.movieflix.repository.MovieRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }
}
