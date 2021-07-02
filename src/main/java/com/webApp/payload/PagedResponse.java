package com.webApp.payload;

import lombok.Data;

import java.util.List;

@Data
public class PagedResponse<T> {

    private List<T> content;

    private int page;

    private int size;

    private long totalElements;

    private boolean totalPages;

    private boolean last;
}
