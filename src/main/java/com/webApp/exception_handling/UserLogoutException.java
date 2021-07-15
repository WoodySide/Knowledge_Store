package com.webApp.exception_handling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
public class UserLogoutException extends RuntimeException {

    private final String user;

    private final String message;

    public UserLogoutException (String user, String message) {
        super(String.format("Couldn't log out [%s]: [%s]", user, message));
        this.user = user;
        this.message = message;
    }
}
