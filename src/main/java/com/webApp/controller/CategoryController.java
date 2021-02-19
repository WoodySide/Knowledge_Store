package com.webApp.controller;

import com.webApp.exception_handling.NoSuchEntityException;
import com.webApp.model.Category;
import com.webApp.model.Title;
import com.webApp.repository.CategoryRepository;
import com.webApp.repository.TitleRepository;
import com.webApp.service.CategoryService;
import com.webApp.service.TitleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("api/v1")
public class CategoryController {

    private final TitleService titleService;

    private final CategoryService categoryService;

    private final CategoryRepository categoryRepository;

    private final TitleRepository titleRepository;

    @Autowired
    public CategoryController(TitleService titleService, CategoryService categoryService, CategoryRepository categoryRepository, TitleRepository titleRepository) {
        this.titleService = titleService;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
        this.titleRepository = titleRepository;
    }

    @ApiOperation(value = "View a list of available categories", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of categories"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource yoy were trying to reach is not found")
    })
    @GetMapping(path = "titles/{titleId}/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Category> getAllCategoriesByTitleId(@PathVariable(value = "titleId") Long titleId, Pageable pageable) {
        return categoryService.findAllCategoriesByTitleId(titleId, pageable);
    }

    @GetMapping(path = "titles/{titleId}/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> getCategoryById(@PathVariable(value = "titleId") Long titleId,
                                                    @PathVariable(value = "categoryId") Long categoryId) {

       Title title = titleService.findTitleById(titleId)
               .orElseThrow(() -> new NoSuchEntityException("Title id not found " + titleId));

       Category category = categoryService.findCategoryById(categoryId)
               .orElseThrow(() -> new NoSuchEntityException("Category id not found " + categoryId));

       category.setTitle(title);
       return ResponseEntity.ok().body(category);
    }

    @PostMapping(path = "/titles/{titleId}/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public Category createCategory(@PathVariable(value = "titleId") Long titleId,
                                   @Valid @RequestBody Category category) {
        return titleService.findTitleById(titleId)
                .map(title -> {
                    category.setTitle(title);
                    return categoryService.saveCategory(category);
                }).orElseThrow(() -> new NoSuchEntityException("category not found"));
    }

    @ApiOperation(value = "Update a category by ID")
    @PutMapping(path = "titles/{titleId}/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> updateCategoryById(@ApiParam(value = "Update title object", required = true)
                                                       @RequestBody @Valid Category category,
                                                       @ApiParam(value = "Title ID to update title object", required = true)
                                                       @PathVariable(name = "titleId") Long titleId,
                                                       @ApiParam(value = "Category ID to update category object", required = true)
                                                       @PathVariable(name = "categoryId") Long categoryId) {

        Optional<Title> optionalTitle = titleService.findTitleById(titleId);

        if (!optionalTitle.isPresent()) {
            log.error("Title: " + optionalTitle + " doesn't exist");
            throw new NoSuchEntityException("There is no title: " +
                    optionalTitle + " in database");
        }

        Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);

        if (!optionalCategory.isPresent()) {
            log.error("Category with ID: " + categoryId + " doesn't exist");
            throw new NoSuchEntityException("There is no category with ID: " +
                    categoryId + " to be updated in database");
        }


        category.setTitle(optionalTitle.get());
        category.setId(optionalCategory.get().getId());
        categoryService.saveCategory(category);

        return ResponseEntity.noContent().build();
    }


    @ApiOperation(value = "Delete a category by ID")
    @DeleteMapping(path = "titles/{titleId}/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteCategoryById(@ApiParam(value = "Title ID from which title object will be deleted from database", required = true)
                                                @PathVariable("titleId") Long titleId,
                                                @ApiParam(value = "Category ID from which title object will be deleted from database", required = true)
                                                @PathVariable("categoryId") Long categoryId) {
        return categoryService.findByTitleIdAndCategoryId(categoryId,titleId)
                .map(category -> {
                    categoryRepository.delete(category);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new NoSuchEntityException("Category not found with id " + categoryId
                                                                + " and titleId " + titleId));
    }
}