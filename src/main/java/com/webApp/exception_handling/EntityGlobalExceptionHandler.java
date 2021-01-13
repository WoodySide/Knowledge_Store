package com.webApp.exception_handling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class EntityGlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<EntityIncorrectData> handleException(NoSuchEntityException exception, WebRequest webRequest) {
        EntityIncorrectData data = new EntityIncorrectData(new Date(), exception.getMessage(), webRequest.getDescription(false));


        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<EntityIncorrectData> handleException(Exception exception, WebRequest webRequest) {
        EntityIncorrectData data = new EntityIncorrectData(new Date(), exception.getMessage(), webRequest.getDescription(false));

        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
}
