package com.webApp.controller;

import com.webApp.exception_handling.NoSuchEntityException;
import com.webApp.model.Title;
import com.webApp.service.TitleService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Title>> getAllTitles() {
        return ResponseEntity.ok(titleService.findAllTitles());
    }


    @ApiOperation(value = "Get a title by ID ")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Title> getTitleById(@ApiParam(value = "Title ID from which object title will be retrieved", required = true)
                                                  @PathVariable(name = "id") Long titleId) {
        return titleService.findTitleById(titleId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NoSuchEntityException("Title not found: " + titleId));
    }


    @ApiOperation(value = "Create a new title")
    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Title createTitle(@ApiParam(value = "Title object store in database")
                                             @Valid @RequestBody Title title) {
        return titleService.saveTitle(title);
    }

    @ApiOperation(value = "Delete a title by ID ")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Title> deleteTitleById(@ApiParam(value = "Title ID from which title object will be deleted from database", required = true)
                                                 @PathVariable(name = "id") Long titleId) {
        return titleService.findTitleById(titleId)
                .map(title ->  {
                    titleService.deleteTitleById(titleId);
                    return ResponseEntity.ok(title);
                })
                .orElseThrow(() -> new NoSuchEntityException("Title not found: " + titleId));
    }


    @ApiOperation(value = "Update a title by ID")
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Title> updateTitleById(@ApiParam(value = "Title ID to update title object", required = true)
                                                 @PathVariable(name = "id") Long titleId,
                                                 @ApiParam(value = "Update title object", required = true)
                                                 @Valid @RequestBody Title title) {
        Title checkTitle  = titleService.findTitleById(titleId)
                .orElseThrow(() -> new NoSuchEntityException("Title not found: " + titleId));

        checkTitle.setName(title.getName());
        final Title updatedTitle = titleService.saveTitle(title);
        return ResponseEntity.ok(updatedTitle);
    }
}
