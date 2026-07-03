package com.movieflix.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.movieflix.controller.request.CategoryRequest;
import com.movieflix.controller.response.CategoryResponse;
import com.movieflix.entity.Category;

class CategoryMapperTest {

    @Test
    void toCategory_shouldMapNameFromRequest() {
        CategoryRequest request = new CategoryRequest("Action");

        Category category = CategoryMapper.toCategory(request);

        assertThat(category.getId()).isNull();
        assertThat(category.getName()).isEqualTo("Action");
    }

    @Test
    void toCategoryResponse_shouldMapIdAndName() {
        Category category = Category.builder().id(1L).name("Fiction").build();

        CategoryResponse response = CategoryMapper.toCategoryResponse(category);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Fiction");
    }
}
