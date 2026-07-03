package com.movieflix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.movieflix.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByCategories_Id(Long categoryId);
}
