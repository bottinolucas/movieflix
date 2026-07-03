package com.movieflix.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.movieflix.controller.request.StreamingRequest;
import com.movieflix.controller.response.StreamingResponse;
import com.movieflix.entity.Streaming;

class StreamingMapperTest {

    @Test
    void toStreaming_shouldMapNameFromRequest() {
        StreamingRequest request = new StreamingRequest("Netflix");

        Streaming streaming = StreamingMapper.toStreaming(request);

        assertThat(streaming.getId()).isNull();
        assertThat(streaming.getName()).isEqualTo("Netflix");
    }

    @Test
    void toStreamingResponse_shouldMapIdAndName() {
        Streaming streaming = Streaming.builder().id(2L).name("HBO Max").build();

        StreamingResponse response = StreamingMapper.toStreamingResponse(streaming);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.name()).isEqualTo("HBO Max");
    }
}
