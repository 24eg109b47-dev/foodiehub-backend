package com.foodiehub.service;

import com.foodiehub.entity.Category;
import com.foodiehub.exception.BusinessRuleViolationException;
import com.foodiehub.exception.ResourceNotFoundException;
import com.foodiehub.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    public Category addCategory(String name, String description) {
        if (categoryRepository.existsByName(name.trim())) {
            throw new BusinessRuleViolationException("Category with name '" + name + "' already exists.");
        }
        Category category = new Category(name.trim(), description != null ? description.trim() : null);
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, String name, String description) {
        Category category = getCategoryById(id);
        if (categoryRepository.existsByNameAndIdNot(name.trim(), id)) {
            throw new BusinessRuleViolationException("Category with name '" + name + "' already exists.");
        }
        category.setName(name.trim());
        category.setDescription(description != null ? description.trim() : null);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
