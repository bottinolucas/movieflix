package com.movieflix.controller.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

@Builder
public record MovieResponse(Long id, String title, String description, LocalDate releaseDate, double rating, List<CategoryResponse> categories, List<StreamingResponse> streamings) {

}
