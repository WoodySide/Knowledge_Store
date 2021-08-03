package com.webApp.controller;

import com.webApp.event.OnGenerateResetLinkEvent;
import com.webApp.event.OnRegenerateEmailVerificationEvent;
import com.webApp.event.OnUserAccountChangeEvent;
import com.webApp.event.OnUserRegistrationCompleteEvent;
import com.webApp.exception_handling.*;
import com.webApp.model.CustomUserDetails;
import com.webApp.model.token.EmailVerificationToken;
import com.webApp.model.token.RefreshToken;
import com.webApp.payload.*;
import com.webApp.security.JwtTokenProvider;
import com.webApp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Authorization", description = "Defines endpoints that can be hit only when the user is not logged in. It's not secured by default." )
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public AuthController(AuthService authService, JwtTokenProvider tokenProvider, ApplicationEventPublisher applicationEventPublisher) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Checks is a given email is in use or not.
     */
    @Operation(summary = "Check email in use",
               tags = "Authorization",
               method = "GET",
               description = "Method checks whether email is in use or not")
    @GetMapping(path = "/checkEmailInUse")
    public ResponseEntity checkEmailInUse(@Parameter(description = "Email id to check against",required = true)
                                          @RequestParam("email") String email) {
        Boolean emailExists = authService.emailAlreadyExists(email);
        return ResponseEntity.ok(new ApiResponse(true, emailExists.toString()));
    }

    /**
     * Checks is a given username is in use or not.
     */
    @Operation(summary = "Check username in user",
               tags = "Authorization",
               method = "GET",
               description = "Method checks whether username is in use or not")
    @GetMapping(path = "/checkUsernameInUse")
    public ResponseEntity checkUsernameInUse(@Parameter(description = "Username to check against" ,required = true)
                                             @RequestParam(value = "username") String username) {
        Boolean usernameExists = authService.usernameAlreadyExists(username);
        return ResponseEntity.ok(new ApiResponse(true, usernameExists.toString()));
    }


    /**
     * Entry point for the user log in. Return the jwt auth token and the refresh token
     */
    @Operation(summary = "User login",
               tags = "Authorization",
               method = "POST",
               description = "Method logs the user in to the system and returns the auth token")
    @PostMapping(path = "/login")
    public ResponseEntity authenticateUser(@Parameter(description = "The LoginRequest payload" ,required = true)
                                           @Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authService.authenticateUser(loginRequest)
                .orElseThrow(() -> new UserLoginException("Couldn't login user [" + loginRequest + "]"));

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("Logged in User returned [API]: " + customUserDetails.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authService.createAndPersistRefreshTokenForDevice(authentication, loginRequest)
                .map(RefreshToken::getToken)
                .map(refreshToken -> {
                    String jwtToken = authService.generateToken(customUserDetails);
                    return ResponseEntity.ok(new JwtAuthenticationResponse(jwtToken, refreshToken, tokenProvider.getExpiryDuration()));
                })
                .orElseThrow(() -> new UserLoginException("Couldn't create refresh token for: [" + loginRequest + "]"));
    }

    /**
     * Entry point for the user registration process. On successful registration,
     * publish an event to generate email verification token
     */
    @Operation(summary = "Register user",
               tags = "Authorization",
               method = "POST",
               description = "Method registers the  user and publishes an event to generate the email verification")
    @PostMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity registerUser(@Parameter(description = "The RegistrationRequest payload", required = true)
                                       @Valid @RequestBody RegistrationRequest registrationRequest) {

        return authService.registerUser(registrationRequest)
                .map(user -> {
                    UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/auth/registrationConfirmation");
                    OnUserRegistrationCompleteEvent onUserRegistrationCompleteEvent = new OnUserRegistrationCompleteEvent(user, urlBuilder);
                    applicationEventPublisher.publishEvent(onUserRegistrationCompleteEvent);
                    log.info("Registered User returned [API[: " + user);
                    return ResponseEntity.ok(new ApiResponse(true, "User registered successfully. Check your email for verification"));
                })
                .orElseThrow(() -> new UserRegistrationException(registrationRequest.getEmail(), "Missing user object in database"));
    }

    /**
     * Receives the reset link request and publishes an event to send email id containing
     * the reset link if the request is valid. In future the deeplink should open within
     * the app itself.
     */
    @Operation(summary = "Send reset link to email",
               tags = "Authorization",
               method = "POST",
               description = "Method receives the reset link request and publish event to send mail containing " +
                       " reset link")
    @PostMapping(path = "/password/resetlink")
    public ResponseEntity resetLink(@Parameter(description = "The PasswordResetLinkRequest payload", required = true)
                                    @Valid @RequestBody PasswordResetLinkRequest passwordResetLinkRequest) {

        return authService.generatePasswordResetToken(passwordResetLinkRequest)
                .map(passwordResetToken -> {
                    UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentContextPath().path("/password/reset");
                    OnGenerateResetLinkEvent generateResetLinkMailEvent = new OnGenerateResetLinkEvent(passwordResetToken,
                            urlBuilder);
                    applicationEventPublisher.publishEvent(generateResetLinkMailEvent);
                    return ResponseEntity.ok(new ApiResponse(true, "Password reset link sent successfully"));
                })
                .orElseThrow(() -> new PasswordResetLinkException(passwordResetLinkRequest.getEmail(), "Couldn't create a valid token"));
    }

    /**
     * Receives a new passwordResetRequest and sends the acknowledgement after
     * changing the password to the user's mail through the event.
     */
    @Operation(summary = "Reset password",
              tags = "Authorization",
              method = "POST",
              description = "Method resets the password after verification ans publish an event to send the acknowledgement email")
    @PostMapping(path = "/password/reset")
    public ResponseEntity resetPassword(@Parameter(description = "The PasswordResetRequest payload", required = true)
                                        @Valid @RequestBody PasswordResetRequest passwordResetRequest) {

        return authService.resetPassword(passwordResetRequest)
                .map(changedUser -> {
                    OnUserAccountChangeEvent onPasswordChangeEvent = new OnUserAccountChangeEvent(changedUser, "Reset Password",
                            "Changed Successfully");
                    applicationEventPublisher.publishEvent(onPasswordChangeEvent);
                    return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
                })
                .orElseThrow(() -> new PasswordResetException(passwordResetRequest.getToken(), "Error in resetting password"));
    }

    /**
     * Confirm the email verification token generated for the user during
     * registration. If token is invalid or token is expired, report error.
     */
    @Operation(summary = "Send reset link to email",
               tags = "Authorization",
               method = "GET",
               description = "Confirms the email verification token that has been generated for the user during registration")
    @GetMapping(path = "/registrationConfirmation")
    public ResponseEntity confirmRegistration(@Parameter(description = "the token that was sent to the user email", required = true)
                                              @RequestParam(value = "token") String token) {

        return authService.confirmEmailRegistration(token)
                .map(user -> ResponseEntity.ok(new ApiResponse(true, "User verified successfully")))
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", token, "Failed to confirm. Please generate a new email verification request"));
    }

    /**
     * Resend the email registration mail with an updated token expiry. Safe to
     * assume that the user would always click on the last re-verification email and
     * any attempts at generating new token from past (possibly archived/deleted)
     * tokens should fail and report an exception.
     */
    @Operation(summary = "Resend registration token",
               tags = "Authorization",
               method = "GET",
               description = "Resend the email registration with an updated token expiry. Safe to " +
                          "assume that the user would always click on the last re-verification email and " +
                          "any attempts at generating new token from past (possibly archived/deleted)" +
                          "tokens should fail and report an exception")
    @GetMapping(path = "/resendRegistrationToken")
    public ResponseEntity resendRegistrationToken(@Parameter(description = "the initial token that was sent to the user email after registration",required = true)
                                                  @RequestParam(value = "token") String existingToken) {

        EmailVerificationToken newEmailToken = authService.recreateRegistrationToken(existingToken)
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", existingToken, "User is already registered. No need to re-generate token"));

        return Optional.ofNullable(newEmailToken.getUser())
                .map(registeredUser -> {
                    UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/auth/registrationConfirmation");
                    OnRegenerateEmailVerificationEvent regenerateEmailVerificationEvent = new OnRegenerateEmailVerificationEvent(registeredUser, urlBuilder, newEmailToken);
                    applicationEventPublisher.publishEvent(regenerateEmailVerificationEvent);
                    return ResponseEntity.ok(new ApiResponse(true, "Email verification resent successfully"));
                })
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", existingToken, "No user associated with this request. Re-verification denied"));
    }

    /**
     * Refresh the expired jwt token using a refresh token for the specific device
     * and return a new token to the caller
     */
    @Operation(summary = "Refresh jwt token",
               tags = "Authorization",
               method = "POST",
               description = "Refresh the expired jwt authentication by issuing a token refresh request and returns the" +
                    "updated response tokens")
    @PostMapping(path = "/refresh")
    public ResponseEntity refreshJwtToken(@Parameter(description = "The TokenRefreshRequest payload", required = true)
                                          @Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {

        return authService.refreshJwtToken(tokenRefreshRequest)
                .map(updatedToken -> {
                    String refreshToken = tokenRefreshRequest.getRefreshToken();
                    log.info("Created new Jwt Auth token: " + updatedToken);
                    return ResponseEntity.ok(new JwtAuthenticationResponse(updatedToken, refreshToken, tokenProvider.getExpiryDuration()));
                })
                .orElseThrow(() -> new TokenRefreshException(tokenRefreshRequest.getRefreshToken(), "Unexpected error during token refresh. Please logout and login again."));
    }
}