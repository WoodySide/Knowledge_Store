package com.webApp.controller;

import com.webApp.exception_handling.NoSuchEntityException;
import com.webApp.model.Title;
import com.webApp.model.User;
import com.webApp.payload.TitleResponse;
import com.webApp.payload.UserIdentityAvailability;
import com.webApp.payload.UserProfile;
import com.webApp.payload.UserSummary;
import com.webApp.repository.TitleRepository;
import com.webApp.repository.UserRepository;
import com.webApp.security.CurrentUser;
import com.webApp.security.UserPrincipal;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@Slf4j
@Api(tags = "Users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TitleRepository titleRepository;

    @GetMapping(path = "/user/me")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
        return userSummary;
    }

    @GetMapping(path = "/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping(path = "/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping(path = "/users/{userId}")
    public UserProfile getUserProfileByUserId(@PathVariable(value = "userId") Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchEntityException("User not found"));

        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt());

        return userProfile;
    }

    @GetMapping(path = "/users/{userId}/titles")
    public TitleResponse getAllTitlesByUserId(@PathVariable(value = "userId") Long userId) {
        Optional<Title> title = titleRepository.findById(userId);

        TitleResponse titleResponse = new TitleResponse(title.get().getId(), title.get().getName());

        return titleResponse;
    }
}
