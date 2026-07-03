package com.movieflix.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.movieflix.entity.Streaming;
import com.movieflix.repository.StreamingRepository;

@ExtendWith(MockitoExtension.class)
class StreamingServiceTest {

    @Mock
    private StreamingRepository repository;

    @InjectMocks
    private StreamingService streamingService;

    @Test
    void findAll_shouldReturnAllStreamings() {
        when(repository.findAll()).thenReturn(List.of(
                Streaming.builder().id(1L).name("Netflix").build(),
                Streaming.builder().id(2L).name("HBO Max").build()));

        List<Streaming> result = streamingService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void saveStreaming_shouldDelegateToRepository() {
        Streaming streaming = Streaming.builder().name("Netflix").build();
        Streaming saved = Streaming.builder().id(1L).name("Netflix").build();
        when(repository.save(streaming)).thenReturn(saved);

        Streaming result = streamingService.saveStreaming(streaming);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<Streaming> result = streamingService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteStreaming_shouldDelegateToRepository() {
        streamingService.deleteStreaming(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}
