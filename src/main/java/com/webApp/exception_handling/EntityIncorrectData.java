package com.webApp.exception_handling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class EntityIncorrectData {

    private Date timeStamp;

    private String details;

    private String message;
}
