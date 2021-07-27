package com.webApp.controller;

import com.webApp.exception_handling.NoSuchEntityException;
import com.webApp.model.CustomUserDetails;
import com.webApp.model.Title;
import com.webApp.model.User;
import com.webApp.security.CurrentUser;
import com.webApp.service.TitleService;
import com.webApp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Title", description = "Titles REST Api")
public class TitleController {

    private final TitleService titleService;

    private final UserService userService;

    @Autowired
    public TitleController(TitleService titleService, UserService userService) {
        this.titleService = titleService;
        this.userService = userService;
    }

    @Operation(summary = "Find all user's titles",
               tags = {"Title"},
               method = "GET",
               description =  "Method returns a list of titles",
               responses = {
                       @ApiResponse(responseCode = "200", description = "Successfully retrieved list of titles"),
                       @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                       @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                       @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")

    })
    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Title>> findAllTitlesByUserId(
                                                             @Parameter(description = "Current registered user", required = true)
                                                             @CurrentUser CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(titleService.findAllByUserId(customUserDetails.getId()));
    }


    @Operation(summary = "Find title by it's ID",
               tags = {"Title"},
               method = "GET",
               description = "Method returns title",
               responses = {
                       @ApiResponse(responseCode = "200", description = "Successfully retrieved title"),
                       @ApiResponse(responseCode = "404", description = "The title you were trying to reach is not found"),
                       @ApiResponse(responseCode = "400", description = "Id can't consists of symbols")
               })
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Title> getTitleById(@Parameter(description = "Title ID from which object title will be retrieved", required = true)
                                              @PathVariable(name = "id") Long titleId) {
        return titleService.findTitleById(titleId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NoSuchEntityException("Title not found with ID: " + titleId));
    }


    @Operation(summary = "Create a new title",
               tags = {"Title"},
               method = "POST",
               description = "Method creates a new title",
               responses = {
                           @ApiResponse(responseCode = "200", description = "A new title was successfully created"),
                           @ApiResponse(responseCode = "400", description = "Title name can't be null")
            })
    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public Title createTitle(@Parameter(description = "Title object store in database",required = true)
                             @Valid @RequestBody Title title,
                             @Parameter(description = "Current registered user",required = true)
                             @CurrentUser CustomUserDetails customUserDetails) {
        return userService.findById(customUserDetails.getId())
        .map(user -> {
            title.setUser(user);
            return titleService.saveTitle(title);
        })
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + customUserDetails));

    }

    @Operation(summary = "Delete title",
               tags = {"Title"},
               method = "DELETE",
               description = "Method deletes title by it's ID",
               responses = {
                    @ApiResponse(responseCode = "200", description = "Title was successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            })
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteTitleById(@Parameter(description = "Title ID from which title object will be deleted from database", required = true)
                                             @PathVariable(name = "id") Long titleId,
                                             @Parameter(description = "Current registered user", required = true)
                                             @CurrentUser CustomUserDetails customUserDetails) {
        Optional<User> user = Optional.ofNullable(userService.findById(customUserDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " +  customUserDetails)));

        return titleService.findTitleById(titleId)
                .map(title -> {
                    title.setUser(user.get());
                    titleService.deleteTitleById(title.getId());
                    return ResponseEntity.ok().build();
                })
        .orElseThrow(() -> new NoSuchEntityException("Title not found with ID: " + titleId));
    }


    @Operation(summary = "Updates title",
               tags = {"Title"},
               method = "PUT",
               description = "Method updates titles by it's ID",
               responses = {
                    @ApiResponse(responseCode = "200", description = "Title was successfully updated"),
                    @ApiResponse(responseCode = "404", description = "The title you were trying to update is not found")
            })
    @PutMapping(path = "/{titleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Title> updateTitleById(@Parameter(description = "Current registered user", required = true)
                                                 @CurrentUser CustomUserDetails customUserDetails,
                                                 @Parameter(description = "Title ID", required = true)
                                                 @PathVariable(value = "titleId") Long titleId,
                                                 @Parameter(description =  "Title object to be updated", required = true)
                                                 @Valid @RequestBody Title title) {

        Optional<Title> updatedTitle = titleService.findTitleById(titleId);

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
