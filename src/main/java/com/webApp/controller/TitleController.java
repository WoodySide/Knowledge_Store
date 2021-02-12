package com.webApp.controller;

import com.webApp.exception_handling.NoSuchEntityException;
import com.webApp.model.Title;
import com.webApp.service.TitleService;
import io.swagger.annotations.*;
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
@RequestMapping("/api/v1/titles/")
public class TitleController {

    private final TitleService titleService;

    @Autowired
    public TitleController(TitleService titleService) {
        this.titleService = titleService;
    }


    @ApiOperation(value = "View a list of available titles", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of titles"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource yoy were trying to reach is not found")
    })
    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Title>> getAllTitles() {
        return ResponseEntity.ok(titleService.findAllTitles());
    }


    @ApiOperation(value = "Get a title by ID ")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Title> getTitleById(@ApiParam(value = "Title ID from which object title will be retrieved", required = true)
                                                  @PathVariable(name = "id") Long titleId) {
        Optional<Title> optionalTitle = titleService.findTitleById(titleId);

        if(!optionalTitle.isPresent()) {
            log.error("Title ID: " + titleId + " doesn't exist");
            throw new NoSuchEntityException("There is no title with ID: " +
                    titleId + " in database");
        }

        return ResponseEntity.ok(optionalTitle.get());
    }


    @ApiOperation(value = "Create a new title")
    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Title> createTitle(@ApiParam(value = "Title object store in database")
                                             @Valid @RequestBody Title title) {
        Title savedTitle = titleService.saveTitle(title);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedTitle.getId()).toUri();

        return ResponseEntity.created(location).body(savedTitle);
    }

    @ApiOperation(value = "Delete a title by ID ")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Title> deleteTitleById(@ApiParam(value = "Title ID from which title object will be deleted from database", required = true)
                                                 @PathVariable(name = "id") Long titleId) {

        Optional<Title> optionalTitle = titleService.findTitleById(titleId);
        if(!optionalTitle.isPresent()) {
            log.error("Title Id: " + titleId + " doesn't exist");
            throw new NoSuchEntityException("There is no title with ID: " +
                    titleId + " to be deleted in database");
        }

       titleService.deleteTitleById(optionalTitle.get().getId());

       return ResponseEntity.noContent().build();
    }


    @ApiOperation(value = "Update a title by ID")
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Title> updateTitleById(@ApiParam(value = "Title ID to update title object", required = true)
                                                 @PathVariable(name = "id") Long titleId,
                                                 @ApiParam(value = "Update title object", required = true)
                                                 @Valid @RequestBody Title title) {

        Optional<Title> optionalTitle = titleService.findTitleById(titleId);

        if(!optionalTitle.isPresent()) {
            log.error("Title with ID: " + titleId + " doesn't exist");
            throw new NoSuchEntityException("There is no title ID: " +
                    titleId + " to be updated in database");
        }

        title.setId(optionalTitle.get().getId());
        titleService.saveTitle(title);

        return ResponseEntity.noContent().build();
    }

}
