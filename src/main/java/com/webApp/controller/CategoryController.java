package com.webApp.controller;

import com.webApp.exception_handling.NoSuchEntityException;
import com.webApp.model.Category;
import com.webApp.model.Title;
import com.webApp.service.CategoryService;
import com.webApp.service.TitleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/user/")
@Tag(name = "Category", description = "Categories REST Api")
public class CategoryController {

    private final TitleService titleService;

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(TitleService titleService,
                              CategoryService categoryService) {
        this.titleService = titleService;
        this.categoryService = categoryService;
    }

    @Operation(summary = "Find all user's categories",
               tags = {"Category"},
               method = "GET",
               description =  "Method returns a list of categories",
               responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of categories"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")

            })
    @GetMapping(path = "titles/{titleId}/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Category> getAllCategoriesByTitleId(@Parameter(description = "Title ID by which all categories will be found",required = true)
                                                    @PathVariable(value = "titleId") Long titleId) {
        return categoryService.findAllCategoriesByTitleId(titleId);
    }

    @Operation(summary = "Find category by it's ID",
               tags = {"Category"},
               method = "GET",
               description = "Method returns category",
               responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved title"),
                    @ApiResponse(responseCode = "404", description = "The category you were trying to reach is not found"),
                    @ApiResponse(responseCode = "400", description = "Id can't consists of symbols")
            })
    @GetMapping(path = "titles/{titleId}/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> getCategoryById(@Parameter(description = "Title ID by which category will be found")
                                                    @PathVariable(value = "titleId") Long titleId,
                                                    @Parameter(description = "Category ID from which category object will be retrieved")
                                                    @PathVariable(value = "categoryId") Long categoryId) {

       Title title = titleService.findTitleById(titleId)
               .orElseThrow(() -> new NoSuchEntityException("Title id not found " + titleId));

       Category category = categoryService.findCategoryById(categoryId)
               .orElseThrow(() -> new NoSuchEntityException("Category id not found " + categoryId));

       category.setTitle(title);
       return ResponseEntity.ok().body(category);
    }

    @Operation(summary = "Create a new category",
               tags = {"Category"},
               method = "POST",
               description = "Method creates a new category",
               responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created a new category"),
                    @ApiResponse(responseCode = "400", description = "Category name can't be null"),
            })
    @PostMapping(path = "titles/{titleId}/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public Category createCategory(@Parameter(description = "Title ID from which all categories will be retrieved")
                                   @PathVariable(value = "titleId") Long titleId,
                                   @Parameter(description = "Category which will be saved as a new one")
                                   @Valid @RequestBody Category category) {
        return titleService.findTitleById(titleId)
                .map(title -> {
                    category.setTitle(title);
                    return categoryService.saveCategory(category);
                }).orElseThrow(() -> new NoSuchEntityException("Category not found"));
    }

    @Operation(summary = "Update category",
               tags = {"Category"},
               method = "PUT",
               description = "Method updates category by it's ID",
               responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated category"),
                    @ApiResponse(responseCode = "404", description = "The category you were trying to update is not found"),
            })
    @PutMapping(path = "titles/{titleId}/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> updateCategoryById(@Parameter(description = "Update category object", required = true)
                                                       @RequestBody @Valid Category category,
                                                       @Parameter(description = "Title ID to update title object", required = true)
                                                       @PathVariable(name = "titleId") Long titleId,
                                                       @Parameter(description = "Category ID to update category object", required = true)
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

        category.setId(categoryId);

        Category categoryToBeSaved = categoryService.saveCategory(category);

        return ResponseEntity.ok(categoryToBeSaved);
    }

    @Operation(summary = "Delete category",
               tags = {"Category"},
               method = "DELETE",
               description = "Method deletes category by it's ID",
               responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted category"),
                    @ApiResponse(responseCode = "404", description = "The category you were trying to delete is not found"),
            })
    @DeleteMapping(path = "titles/{titleId}/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteCategoryById(@Parameter(description = "Title ID from which title object will be deleted from database", required = true)
                                                @PathVariable("titleId") Long titleId,
                                                @Parameter(description = "Category ID from which title object will be deleted from database", required = true)
                                                @PathVariable("categoryId") Long categoryId) {
        return categoryService.findByTitleIdAndCategoryId(categoryId,titleId)
                .map(category -> {
                    categoryService.deleteCategoryById(category.getId());
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new NoSuchEntityException("Category not found with ID " + categoryId
                                                                + " and title with ID " + titleId));
    }
}