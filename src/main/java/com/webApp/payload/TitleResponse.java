package com.webApp.payload;

import lombok.Data;

@Data
public class TitleResponse {
    private Long id;
    private String name;

    public TitleResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
