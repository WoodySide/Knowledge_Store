package com.webApp.payload;

import lombok.Builder;
import lombok.Data;

@Data
public class UserSummary {

    private Long id;

    private String username;

    private String name;

    @Builder
    public UserSummary(Long id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
    }
}
