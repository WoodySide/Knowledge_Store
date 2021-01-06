package com.webApp.controllers;

import com.webApp.model.Category;
import com.webApp.model.Title;
import com.webApp.service.CategoryService;
import com.webApp.service.TitleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/v1/categories/")
public class CategoryController {

    private final TitleService titleService;

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(TitleService titleService, CategoryService categoryService) {
        this.titleService = titleService;
        this.categoryService = categoryService;
    }

    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @GetMapping(path = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> getCategoryById(@PathVariable(name = "categoryId") Long categoryId) {
        Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);
        if(!optionalCategory.isPresent()) {
            log.error("Category with ID: " + categoryId + " doesn't exist");
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(optionalCategory.get());
    }

    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Category> createCategory(@RequestBody @Valid Category category) {
        Optional<Title> optionalTitle = titleService.findTitleById(category.getTitle().getId());

        if(!optionalTitle.isPresent()) {
            log.error("Title: " + optionalTitle + " doesn't exist");
            return ResponseEntity.unprocessableEntity().build();
        }

        category.setTitle(optionalTitle.get());
        Category savedCategory = categoryService.saveCategory(category);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{categoryId}")
                .buildAndExpand(savedCategory.getId()).toUri();

        return ResponseEntity.created(location).body(savedCategory);
    }

    @PutMapping(path = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> updateCategoryById(@RequestBody @Valid Category category,
                                                       @PathVariable(name = "categoryId") Long categoryId) {

        Optional<Title> optionalTitle = titleService.findTitleById(category.getTitle().getId());

        if(!optionalTitle.isPresent()) {
            log.error("Title: " + optionalTitle + " doesn't exist");
            return ResponseEntity.unprocessableEntity().build();
        }

        Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);

        if(!optionalCategory.isPresent()) {
            log.error("Category with ID: " + categoryId + " doesn't exist");
            return ResponseEntity.unprocessableEntity().build();
        }

        category.setTitle(optionalTitle.get());
        category.setId(optionalCategory.get().getId());
        categoryService.saveCategory(category);

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(path = "/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> deleteCategoryById(@PathVariable("categoryId") Long categoryId) {
        Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);

        if(!optionalCategory.isPresent()) {
            log.error("Category with ID: " + categoryId + " doesn't exist");
            return ResponseEntity.unprocessableEntity().build();
        }

        categoryService.deleteCategoryById(optionalCategory.get().getId());

        return ResponseEntity.noContent().build();
    }
}
