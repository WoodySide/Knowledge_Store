package com.webApp.controller;

import com.webApp.exception_handling.NoSuchEntityException;
import com.webApp.model.CustomUserDetails;
import com.webApp.model.Title;
import com.webApp.model.User;
import com.webApp.repository.TitleRepository;
import com.webApp.repository.UserRepository;
import com.webApp.security.CurrentUser;
import com.webApp.service.TitleService;
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

    private final TitleRepository titleRepository;

    private final UserRepository userRepository;

    @Autowired
    public TitleController(TitleService titleService, TitleRepository titleRepository, UserRepository userRepository) {
        this.titleService = titleService;
        this.titleRepository = titleRepository;
        this.userRepository = userRepository;
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
        return ResponseEntity.ok(titleRepository.findAllByUserId(customUserDetails.getId()));
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
        return userRepository.findById(customUserDetails.getId())
        .map(user -> {
            title.setUser(user);
            return titleRepository.save(title);
        })
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + customUserDetails));

    }

    @ApiOperation(value = "Delete title by ID ")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteTitleById(@ApiParam(value = "Title ID from which title object will be deleted from database", required = true)
                                                 @PathVariable(name = "id") Long titleId,
                                                 @CurrentUser CustomUserDetails customUserDetails) {

        Optional<User> user = Optional.ofNullable(userRepository.findById(customUserDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " +  customUserDetails)));

        return titleRepository.findById(titleId)
                .map(title -> {
                    title.setUser(user.get());
                    titleRepository.delete(title);
                    return ResponseEntity.ok().build();
                })
        .orElseThrow(() -> new NoSuchEntityException("Title not found with ID: " + titleId));
    }


    @ApiOperation(value = "Update title")
    @PutMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Title> updateTitleById(@ApiParam(value = "Current registered user", required = true)
                                                 @CurrentUser CustomUserDetails customUserDetails,
                                                 @ApiParam(value = "Update title object", required = true)
                                                 @Valid @RequestBody Title title) {

        return userRepository.findById(customUserDetails.getId())
                .map(user -> {
                    title.setUser(user);
                    titleRepository.save(title);
                    return ResponseEntity.ok(title);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + customUserDetails));
    }
}
