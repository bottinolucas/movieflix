package com.movieflix.controller.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder
public record MovieResponse(
    Long id, 
    String title, 
    String description, 
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate releaseDate, 
    double rating, 
    List<CategoryResponse> categories, 
    List<StreamingResponse> streamings) {

}
