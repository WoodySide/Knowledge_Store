package com.webApp.controller;

import com.webApp.exception_handling.NoSuchEntityException;
import com.webApp.model.Category;
import com.webApp.model.Link;
import com.webApp.service.CategoryService;
import com.webApp.service.LinkService;
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
@RequestMapping("/api/v1/titles/{titleId}")
public class LinkController {

    private final CategoryService categoryService;

    private final LinkService linkService;

    @Autowired
    public LinkController(CategoryService categoryService,
                          LinkService linkService) {
        this.categoryService = categoryService;
        this.linkService = linkService;
    }

    @ApiOperation(value = "View a list of available links", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of links"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource yoy were trying to reach is not found")
    })
    @GetMapping(path = "categories/{categoryId}/links", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Link>> getAllLinksByCategoryId(@PathVariable(value = "categoryId") Long categoryId,
                                                  Pageable pageable) {
        return ResponseEntity.ok(linkService.findByCategoryId(categoryId, pageable));
    }

    @ApiOperation(value = "Get link id")
    @GetMapping(path = "categories/{categoryId}/links/{linkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Link> getLinkById(@PathVariable(value = "categoryId") Long categoryId,
                                            @PathVariable(value = "linkId") Long linkId) {

        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new NoSuchEntityException("Category id not found: " + categoryId));

        Link link = linkService.findLinkById(linkId)
                .orElseThrow(() -> new NoSuchEntityException("Link id not found: " + linkId));

        link.setCategory(category);

        return ResponseEntity.ok().body(link);

    }

    @ApiOperation(value = "Create new link")
    @PostMapping(path = "categories/{categoryId}/links")
    public Link createLink(@PathVariable(value = "categoryId") Long categoryId,
                           @RequestBody @Valid Link createdLink) {

        return categoryService.findCategoryById(categoryId)
                .map(category -> {
                    createdLink.setCategory(category);
                    return linkService.saveLink(createdLink);
                }).orElseThrow(() -> new NoSuchEntityException("Link not found"));
    }

    @ApiOperation(value = "Update link by ID")
    @PutMapping(path = "categories/{categoryId}/links/{linkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Link> updateLinkById(@ApiParam(value = "Update link object", required = true)
                                               @RequestBody @Valid Link link,
                                               @ApiParam(value = "Category ID to update category object", required = true)
                                               @PathVariable(value = "categoryId") Long categoryId,
                                               @ApiParam(value = "Link ID to update link object", required = true)
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

        link.setId(optionalLink.get().getId());

        linkService.saveLink(link);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Delete link by ID")
    @DeleteMapping(path = "categories/{categoryId}/links/{linkId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteLinkById(@ApiParam(value = "Category ID from which category object will be deleted", required = true)
                                            @PathVariable(value = "categoryId") Long categoryId,
                                            @ApiParam(value = "Link ID from which link object will be deleted", required = true)
                                            @PathVariable(value = "linkId") Long linkId) {

        return linkService.findByCategoryIdAndLinkId(linkId, categoryId)
                .map(link -> {
                    linkService.deleteLinkById(link.getId());
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new NoSuchEntityException("Link not found with ID " + linkId
                                                                + " and category with ID " + categoryId));
    }
}
