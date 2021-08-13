package com.webApp.controller;

import com.webApp.exception_handling.NoSuchEntityException;
import com.webApp.model.Category;
import com.webApp.model.Link;
import com.webApp.service.CategoryService;
import com.webApp.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@Slf4j
@RequestMapping("/api/user/titles/{titleId}")
@Tag(name = "Link", description = "Links REST Api")
public class LinkController {

    private final CategoryService categoryService;

    private final LinkService linkService;

    @Autowired
    public LinkController(CategoryService categoryService,
                          LinkService linkService) {
        this.categoryService = categoryService;
        this.linkService = linkService;
    }

    @Operation(summary = "Find all user's links",
            tags = {"Link"},
            method = "GET",
            description =  "Method returns a list of links",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of links"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")

            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping(path = "categories/{categoryId}/links", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Link>> getAllLinksByCategoryId(@Parameter(description = "Category ID from which all links will be retrieved",required = true)
                                                              @PathVariable(value = "categoryId") Long categoryId) {
        return ResponseEntity.ok(linkService.findByCategoryId(categoryId));
    }

    @Operation(summary = "Find links by it's ID",
            tags = {"Link"},
            method = "GET",
            description = "Method returns link",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved link"),
                    @ApiResponse(responseCode = "404", description = "The link you were trying to reach is not found"),
                    @ApiResponse(responseCode = "400", description = "Id can't consists of symbols")
            })
    @GetMapping(path = "categories/{categoryId}/links/{linkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Link> getLinkById(@Parameter(description = "Category ID from which all links will be retrieved",required = true)
                                            @PathVariable(value = "categoryId") Long categoryId,
                                            @Parameter(description = "Link ID by which link will be found")
                                            @PathVariable(value = "linkId") Long linkId) {

        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NoSuchEntityException("Category id not found: " + categoryId));

        Link link = linkService.findLinkById(linkId)
                .orElseThrow(() -> new NoSuchEntityException("Link id not found: " + linkId));

        link.setCategory(category);

        return ResponseEntity.ok().body(link);

    }

    @Operation(summary = "Create a new link",
            tags = {"Link"},
            method = "POST",
            description = "Method creates a new link",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created a new link"),
                    @ApiResponse(responseCode = "400", description = "Link name can't be null"),
            })
    @PostMapping(path = "categories/{categoryId}/links")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Link createLink(@Parameter(description = "Category ID from which all links will be retrieved", required = true)
                           @PathVariable(value = "categoryId") Long categoryId,
                           @Parameter(description = "Link which will be saves as a new one")
                           @RequestBody @Valid Link createdLink) {

        return categoryService.findCategoryById(categoryId)
                .map(category -> {
                    createdLink.setCategory(category);
                    return linkService.saveLink(createdLink);
                }).orElseThrow(() -> new NoSuchEntityException("Link not found"));
    }

    @Operation(summary = "Update link",
            tags = {"Link"},
            method = "PUT",
            description = "Method updates link by it's ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated link"),
                    @ApiResponse(responseCode = "404", description = "The link you were trying to update is not found"),
            })
    @PutMapping(path = "categories/{categoryId}/links/{linkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Link> updateLinkById(@Parameter(description = "Update link object", required = true)
                                               @RequestBody @Valid Link link,
                                               @Parameter(description = "Category ID to update category object", required = true)
                                               @PathVariable(value = "categoryId") Long categoryId,
                                               @Parameter(description = "Link ID to update link object", required = true)
                                               @PathVariable(value = "linkId") Long linkId) {

        Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);

        if(!optionalCategory.isPresent()) {
            log.error("Category: " + optionalCategory + " doesn't exist");
            throw new NoSuchEntityException("There is no category: " +
                    optionalCategory + " in database");
        }

        Optional<Link> optionalLink = linkService.findLinkById(linkId);

        if(!optionalLink.isPresent()) {
            log.error("Link: " + optionalLink + " doesn't exist");
            throw new NoSuchEntityException("There is no link: " +
                    optionalLink + " in database");
        }

        link.setCategory(optionalCategory.get());

        link.setId(linkId);

        Link linkToBeUpdated = linkService.saveLink(link);

        return ResponseEntity.ok(linkToBeUpdated);
    }

    @Operation(summary = "Delete link",
               tags = {"Link"},
               method = "DELETE",
               description = "Method deletes link by it's ID",
               responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted link"),
                    @ApiResponse(responseCode = "404", description = "The link you were trying to delete is not found"),
            })
    @DeleteMapping(path = "categories/{categoryId}/links/{linkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteLinkById(@Parameter(description = "Category ID from which all links will be retrieved", required = true)
                                            @PathVariable(value = "categoryId") Long categoryId,
                                            @Parameter(description = "Link ID by which link object will be deleted from database", required = true)
                                            @PathVariable(value = "linkId") Long linkId) {

        return linkService.findByCategoryIdAndLinkId(linkId, categoryId)
                .map(link -> {
                    linkService.deleteLinkById(link.getId());
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new NoSuchEntityException("Link not found with ID " + linkId
                                                                + " and category with ID " + categoryId));
    }
}
