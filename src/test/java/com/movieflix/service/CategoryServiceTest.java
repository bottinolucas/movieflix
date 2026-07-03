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

import com.movieflix.entity.Category;
import com.movieflix.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void findAll_shouldReturnAllCategories() {
        when(repository.findAll()).thenReturn(List.of(
                Category.builder().id(1L).name("Action").build(),
                Category.builder().id(2L).name("Fiction").build()));

        List<Category> result = categoryService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void saveCategory_shouldDelegateToRepository() {
        Category category = Category.builder().name("Action").build();
        Category saved = Category.builder().id(1L).name("Action").build();
        when(repository.save(category)).thenReturn(saved);

        Category result = categoryService.saveCategory(category);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteCategory_shouldDelegateToRepository() {
        categoryService.deleteCategory(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}
