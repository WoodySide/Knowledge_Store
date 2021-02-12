package com.webApp.controller;

import com.webApp.exception_handling.NoSuchEntityException;
import com.webApp.model.Category;
import com.webApp.model.Title;
import com.webApp.service.CategoryService;
import com.webApp.service.TitleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(value = "View a list of available categories", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of categories"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource yoy were trying to reach is not found")
    })
    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @ApiOperation(value = "Get a category by ID")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> getCategoryById(@ApiParam(value = "Category ID from which object category will be retrieved", required = true)
                                                    @PathVariable(name = "id") Long categoryId) {
        Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);
        if(!optionalCategory.isPresent()) {
            log.error("Category with ID: " + categoryId + " doesn't exist");
            throw new NoSuchEntityException("There is no category with ID: " +
                    categoryId + " in database");
        }

        return ResponseEntity.ok(optionalCategory.get());
    }

    @ApiOperation(value = "Create a new category")
    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> createCategory(@ApiParam(value = "Category object store in database", required = true)
                                                   @RequestBody @Valid Category category) {

        Optional<Title> optionalTitle = titleService.findTitleById(category.getTitle().getId());

        if(!optionalTitle.isPresent()) {
            log.error("Title: " + optionalTitle + " doesn't exist");
            throw new NoSuchEntityException("There is no title: " +
                    optionalTitle + " in database");
        }

        category.setTitle(optionalTitle.get());
        Category savedCategory = categoryService.saveCategory(category);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedCategory.getId()).toUri();

        return ResponseEntity.created(location).body(savedCategory);
    }

    @ApiOperation(value = "Update a category by ID")
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> updateCategoryById(@ApiParam(value = "Update title object", required = true)
                                                       @RequestBody @Valid Category category,
                                                       @ApiParam(value = "Title ID to update title object", required = true)
                                                       @PathVariable(name = "id") Long categoryId) {

        Optional<Title> optionalTitle = titleService.findTitleById(category.getTitle().getId());

        if(!optionalTitle.isPresent()) {
            log.error("Title: " + optionalTitle + " doesn't exist");
            throw new NoSuchEntityException("There is no title: " +
                    optionalTitle + " in database");
        }

        Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);

        if(!optionalCategory.isPresent()) {
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
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> deleteCategoryById(@ApiParam(value = "Category ID from which title object will be deleted from database", required = true)
                                                       @PathVariable("id") Long categoryId) {
        Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);

        if(!optionalCategory.isPresent()) {
            log.error("Category with ID: " + categoryId + " doesn't exist");
            throw new NoSuchEntityException("There is no category with ID: " +
                    categoryId + " to be deleted in database");
        }

        categoryService.deleteCategoryById(optionalCategory.get().getId());

        return ResponseEntity.noContent().build();
    }
}
