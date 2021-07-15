package com.webApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class Mail {

    private String from;

    private String to;

    private String subject;

    private String content;

    private Map<String,String> model;

    public Mail() {
        model = new HashMap<>();
    }
}
