package com.webApp.payload;

import com.webApp.model.Category;
import lombok.Data;

import java.util.Set;

@Data
public class TitleResponse {
    private Long id;
    private String name;
    private Set<Category>  categories;

    public TitleResponse(Long id, String name, Set<Category> categories) {
        this.id = id;
        this.name = name;
        this.categories = categories;
    }
}
