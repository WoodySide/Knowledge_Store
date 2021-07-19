package com.webApp.controller;

import com.webApp.exception_handling.NoSuchEntityException;
import com.webApp.model.CustomUserDetails;
import com.webApp.model.Title;
import com.webApp.model.User;
import com.webApp.security.CurrentUser;
import com.webApp.service.TitleService;
import com.webApp.service.UserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/user/titles")
@Api(tags = "{Titles}")
public class TitleController {

    private final TitleService titleService;

    private final UserService userService;

    @Autowired
    public TitleController(TitleService titleService, UserService userService) {
        this.titleService = titleService;
        this.userService = userService;
    }

    @ApiOperation(value = "View a list of available titles", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of titles"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Title>> findTitleByUserId(
            @ApiParam(value = "Current registered user", required = true)
            @CurrentUser CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(titleService.findAllByUserId(customUserDetails.getId()));
    }


    @ApiOperation(value = "Get a title by ID ")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Title> getTitleById(@ApiParam(value = "Title ID from which object title will be retrieved", required = true)
                                              @PathVariable(name = "id") Long titleId) {
        return titleService.findTitleById(titleId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NoSuchEntityException("Title not found with ID: " + titleId));
    }


    @ApiOperation(value = "Create a new title")
    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public Title createTitle(@ApiParam(value = "Title object store in database")
                             @CurrentUser CustomUserDetails customUserDetails,
                             @Valid @RequestBody Title title) {
        return userService.findById(customUserDetails.getId())
        .map(user -> {
            title.setUser(user);
            return titleService.saveTitle(title);
        })
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + customUserDetails));

    }

    @ApiOperation(value = "Delete title by ID ")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteTitleById(@ApiParam(value = "Title ID from which title object will be deleted from database", required = true)
                                                 @PathVariable(name = "id") Long titleId,
                                                 @CurrentUser CustomUserDetails customUserDetails) {

        Optional<User> user = Optional.ofNullable(userService.findById(customUserDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " +  customUserDetails)));

        return titleService.findByTitleId(titleId)
                .map(title -> {
                    title.setUser(user.get());
                    titleService.deleteTitleById(title.getId());
                    return ResponseEntity.ok().build();
                })
        .orElseThrow(() -> new NoSuchEntityException("Title not found with ID: " + titleId));
    }


    @ApiOperation(value = "Update title")
    @PutMapping(path = "/{titleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Title> updateTitleById(@ApiParam(value = "Current registered user", required = true)
                                                 @CurrentUser CustomUserDetails customUserDetails,
                                                 @PathVariable(value = "titleId") Long titleId,
                                                 @ApiParam(value = "Update title object", required = true)
                                                 @Valid @RequestBody Title title) {

        Optional<Title> updatedTitle = titleService.findByTitleId(titleId);

        if(!updatedTitle.isPresent()) {
            throw new NoSuchEntityException("There is not such title with ID: " + titleId);
        }

        return userService.findById(customUserDetails.getId())
                .map(user -> {
                    title.setUser(user);
                    title.setId(titleId);
                    titleService.saveTitle(title);
                    return ResponseEntity.ok(title);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + customUserDetails));
    }
}
