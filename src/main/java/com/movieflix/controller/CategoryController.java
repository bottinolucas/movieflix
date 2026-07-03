package com.movieflix.controller;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movieflix.entity.Category;
import com.movieflix.service.CategoryService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/movieflix/category")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.findAll();        
    } 

    @GetMapping("/{id}")
    public Category getByCategoryId(@PathVariable Long id) {
        Optional<Category> opt = categoryService.findById(id);

        if (opt.isPresent()) return opt.get();
        
        return null;
    }

    @PostMapping
    public Category saveCategory(@RequestBody @NonNull Category category) {
        return categoryService.saveCategory(category);
    }

    @DeleteMapping("/{id}")
    public void deleteByCategoryById(@PathVariable @NonNull Long id) {
        categoryService.deleteCategory(id);
    }
}
