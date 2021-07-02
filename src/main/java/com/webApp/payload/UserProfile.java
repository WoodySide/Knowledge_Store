package com.webApp.payload;

import lombok.Data;

import java.util.Date;

@Data
public class UserProfile {

    private Long id;

    private String username;

    private String name;

    private Date joinedAt;

    public UserProfile(Long id, String username, String name, Date joinedAt) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.joinedAt = joinedAt;
    }
}
