package com.movieflix.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movieflix.controller.request.StreamingRequest;
import com.movieflix.controller.response.StreamingResponse;
import com.movieflix.entity.Streaming;
import com.movieflix.mapper.StreamingMapper;
import com.movieflix.service.StreamingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/movieflix/streaming")
@RequiredArgsConstructor
public class StreamingController {

    private final StreamingService streamingService;

    @GetMapping
    public ResponseEntity<List<StreamingResponse>> getAllStreamings() {
        List<Streaming> streamings = streamingService.findAll();
        
        List<StreamingResponse> list = streamings.stream()
                        .map(streaming -> StreamingMapper.toStreamingResponse(streaming))
                        .toList();
        
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StreamingResponse> getByStreamingId(@PathVariable Long id) {
        return streamingService.findById(id)
            .map(streaming -> ResponseEntity.ok(StreamingMapper.toStreamingResponse(streaming)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StreamingResponse> saveStreaming(@RequestBody StreamingRequest request) {
        Streaming newStreaming = StreamingMapper.toStreaming(request);
        Streaming savedStreaming = streamingService.saveStreaming(newStreaming);

        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(StreamingMapper.toStreamingResponse(savedStreaming));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByStreamingById(@PathVariable @NonNull Long id) {
        streamingService.deleteStreaming(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
