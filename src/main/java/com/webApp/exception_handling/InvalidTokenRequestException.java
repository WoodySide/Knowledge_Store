package com.webApp.exception_handling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class InvalidTokenRequestException extends RuntimeException{

    private final String tokenType;

    private final String token;

    private String message;

    public InvalidTokenRequestException(String tokenType, String token, String message) {
        super(String.format("%s: [%s] token: [%s] ", message,tokenType,token));
        this.tokenType = tokenType;
        this.token = token;
        this.message = message;
    }
}
