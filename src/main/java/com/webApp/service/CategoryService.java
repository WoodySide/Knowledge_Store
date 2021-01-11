package com.webApp.service;

import com.webApp.model.Category;
import com.webApp.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAllCategories() {
        log.info("In CategoryService find all Categories");
        return categoryRepository.findAll();
    }

    public Optional<Category> findCategoryById(Long id) {
        log.info("In CategoryService find Category by id {}", id);
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category) {
        log.info("In CategoryService save Category {}", category);
        return categoryRepository.save(category);
    }

    public void deleteCategoryById(Long id) {
        log.info("In CategoryService delete Category by id {}", id);
        categoryRepository.deleteById(id);
    }
}
