package com.movieflix.repository;

import org.springframework.stereotype.Repository;

import com.movieflix.entity.Streaming;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface StreamingRepository extends JpaRepository<Streaming, Long>{

}
