package com.webApp.controller;

import com.webApp.event.OnUserAccountChangeEvent;
import com.webApp.event.OnUserLogoutSuccessEvent;
import com.webApp.exception_handling.UpdatePasswordException;
import com.webApp.model.CustomUserDetails;
import com.webApp.payload.ApiResponse;
import com.webApp.payload.LogOutRequest;
import com.webApp.payload.UpdatePasswordRequest;
import com.webApp.security.CurrentUser;
import com.webApp.service.AuthService;
import com.webApp.service.UserService;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "User", description = "Defines endpoints for the logged in user. It's secured by default")
public class UserController {

    private final AuthService authService;

    private final UserService userService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public UserController(AuthService authService, UserService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.authService = authService;
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Gets the current user profile of the logged in user
     */

    @Operation(summary = "Get user profile",
               tags = "User",
               method = "GET",
               description = "Returns the current user profile")
    @GetMapping(path = "/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity getUserProfile(@CurrentUser CustomUserDetails currentUser) {
        log.info(currentUser.getEmail() + " has role: " + currentUser.getRoles());
        return ResponseEntity.ok("Hello. This is about me");
    }

    /**
     * Returns all admins in the system. Requires Admin access
     */
    @Operation(summary = "Get admin profile",
               tags = "User",
               method = "GET",
               description = "Returns the list of configured admins. Requires ADMIN Access")
    @GetMapping(path = "/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity getAllAdmins() {
        log.info("Inside secured resource with admin");
        return ResponseEntity.ok("Hello. This is about admins");
    }

    /**
     * Updates the password of the current logged in user
     */

    @Operation(summary = "Update user password",
               tags = "User",
               method = "POST",
               description = "Allows the user to change his password once logged in by supplying the correct current " +
                       "password")
    @PostMapping(path = "/password/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity updateUserPassword(@CurrentUser CustomUserDetails customUserDetails,
                                             @ApiParam(value = "The UpdatePasswordRequest payload") @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {

        return authService.updatePassword(customUserDetails, updatePasswordRequest)
                .map(updatedUser -> {
                    OnUserAccountChangeEvent onUserPasswordChangeEvent = new OnUserAccountChangeEvent(updatedUser, "Update Password", "Change successful");
                    applicationEventPublisher.publishEvent(onUserPasswordChangeEvent);
                    return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
                })
                .orElseThrow(() -> new UpdatePasswordException("--Empty--", "No such user present."));
    }

    /**
     * Log the user out from the app/device. Release the refresh token associated with the
     * user device.
     */
    @Operation(summary = "Logout user",
               tags = "User",
               method = "POST",
               description = "Logs the specified user device and clears the refresh tokens associated with it")
    @PostMapping(path = "/logout")
    public ResponseEntity logoutUser(@CurrentUser CustomUserDetails customUserDetails,
                                     @ApiParam(value = "The LogOutRequest payload") @Valid @RequestBody LogOutRequest logOutRequest) {
        userService.logoutUser(customUserDetails, logOutRequest);
        Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();

        OnUserLogoutSuccessEvent logoutSuccessEvent = new OnUserLogoutSuccessEvent(customUserDetails.getEmail(), credentials.toString(), logOutRequest);
        applicationEventPublisher.publishEvent(logoutSuccessEvent);
        return ResponseEntity.ok(new ApiResponse(true, "Log out successful"));
    }
}
