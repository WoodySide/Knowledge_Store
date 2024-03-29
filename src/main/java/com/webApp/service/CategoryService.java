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

    public List<Category> findAllCategoriesByTitleId(Long titleId) {
        log.info("In TitleService find all categories");
        return categoryRepository.findByTitleId(titleId);
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findByTitleIdAndCategoryId(Long categoryId,Long titleId) {
        return categoryRepository.findByIdAndTitleId(categoryId,titleId);
    }

    public Optional<Category> findCategoryById(Long id) {
        log.info("Found by id {}", id);
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category) {
        log.info("Save {}", category);
        return categoryRepository.save(category);
    }

    public void deleteCategoryById(Long id) {
        log.info("Delete by id {}", id);
        categoryRepository.deleteById(id);
    }
}
