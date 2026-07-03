package com.movieflix.service;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.movieflix.entity.Streaming;
import com.movieflix.repository.StreamingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StreamingService {

    private final StreamingRepository repository;

    public List<Streaming> findAll() {
        return repository.findAll();
    }

    public Streaming saveStreaming(@NonNull Streaming streaming) {
        return repository.save(streaming);
    }

    public Optional<Streaming> findById(@NonNull Long id) {
        return repository.findById(id);
    }

    public void deleteStreaming(@NonNull Long id) {
        repository.deleteById(id);
    }
}
