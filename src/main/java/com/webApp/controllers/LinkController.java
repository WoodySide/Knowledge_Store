package com.webApp.controllers;

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
@RequestMapping("/api/v1/links/")
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
    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Link>> getAllLinks() {
        return ResponseEntity.ok(linkService.findAllLinks());
    }

    @ApiOperation(value = "Get a link by ID")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Link> getLinkById(@ApiParam(value = "Link ID from which object category will be retrieved", required = true)
                                            @PathVariable(name = "id") Long linkId) {
        Optional<Link> optionalLink = linkService.findLinkById(linkId);

        if(!optionalLink.isPresent()) {
            log.error("Link Id: " + linkId + " doesn't exist");
            throw new NoSuchEntityException("There is no link with ID: " +
                    linkId + " in database");
        }

        return ResponseEntity.ok(optionalLink.get());
    }

    @ApiOperation(value = "Create a new link")
    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Link> createLink(@ApiParam(value = "Link object store in database", required = true)
                                           @RequestBody @Valid Link link) {
        Optional<Category> optionalCategory = categoryService.findCategoryById(link.getCategory().getId());

        if(!optionalCategory.isPresent()) {
            log.error("Category: " + optionalCategory + " doesnt' exist");
            throw new NoSuchEntityException("There is no category: " +
                    optionalCategory + " in database");
        }

        link.setCategory(optionalCategory.get());

        Link savedLink = linkService.saveLink(link);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedLink).toUri();

        return ResponseEntity.created(location).body(savedLink);
    }

    @ApiOperation(value = "Update a link by ID")
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Link> updateLinkById(@ApiParam(value = "Update link object", required = true)
                                               @RequestBody @Valid Link link,
                                               @ApiParam(value = "Link ID to update title object", required = true)
                                               @PathVariable(name = "id") Long linkId) {
        Optional<Category> optionalCategory = categoryService.findCategoryById(link.getCategory().getId());

        if(!optionalCategory.isPresent()) {
            log.error("Category  " + optionalCategory + " doesn't exist");
            throw new NoSuchEntityException("There is no category: " +
                    optionalCategory + " in database");
        }

        Optional<Link> optionalLink = linkService.findLinkById(linkId);

        if(!optionalLink.isPresent()) {
            log.error("Link Id " + linkId + " doesn't exist");
            throw new NoSuchEntityException("There is no link with ID: " +
                    linkId + " to be updated in database");
        }

        link.setCategory(optionalCategory.get());
        link.setId(optionalLink.get().getId());
        linkService.saveLink(link);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Delete a link by ID")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Link> deleteLinkById(@ApiParam(value = "Link ID from which title object will be deleted from database", required = true)
                                               @PathVariable(name = "id") Long linkId) {
        Optional<Link> optionalLink = linkService.findLinkById(linkId);

        if(!optionalLink.isPresent()) {
            log.error("Link Id " + linkId + " doesn't exist");
            throw new NoSuchEntityException("There is no link with ID: " +
                    linkId + " to be deleted in database");
        }

        linkService.deleteLinkById(optionalLink.get().getId());

        return ResponseEntity.noContent().build();
    }
}
