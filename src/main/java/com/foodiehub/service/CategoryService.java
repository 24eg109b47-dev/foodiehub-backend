package com.foodiehub.service;

import com.foodiehub.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category addCategory(String name, String description);
    Category updateCategory(Long id, String name, String description);
    void deleteCategory(Long id);
}
