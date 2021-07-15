package com.webApp.exception_handling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
public class UserRegistrationException extends RuntimeException{

    private final String user;

    private final String message;

    public UserRegistrationException (String user, String message) {
        super(String.format("Failed to sing up User[%s]: '%s'", user, message));
        this.user = user;
        this.message = message;
    }
}
