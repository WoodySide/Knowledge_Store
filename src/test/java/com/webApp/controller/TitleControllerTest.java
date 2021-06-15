package com.webApp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webApp.model.Title;
import com.webApp.service.TitleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TitleController.class)
@ActiveProfiles("test")
public class TitleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TitleService titleService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Title> titles;

    @BeforeEach
    public void setUp() {
        this.titles = new ArrayList<>();
        this.titles.add(Title.builder().id(1L).name("Title1").build());
        this.titles.add(Title.builder().id(2L).name("Title2").build());
        this.titles.add(Title.builder().id(3L).name("Title3").build());

    }

    @Test
    public void whenFetchAllTitle_thenReturnThem() throws Exception {

        given(titleService.findAllTitles()).willReturn(titles);

        this.mockMvc.perform(get("/api/v1/titles/"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()", is(titles.size())));
    }

    @Test
    public void whenFetchTitleById_thenReturnTitle() throws Exception {

        final Long titleId = 1L;
        final Title title = Title.builder()
                .id(1L)
                .name("Title")
                .build();

        given(titleService.findTitleById(titleId)).willReturn(Optional.of(title));

        this.mockMvc.perform(get("/api/v1/titles/{id}", titleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(title.getName())));
    }

    @Test
    public void whenNonExistingTitleFound_thenReturn404() throws Exception {

        final Long titleId = 1L;

        given(titleService.findTitleById(titleId)).willReturn(Optional.empty());

        this.mockMvc.perform(get("/api/v1/titles/{id}", titleId))
                .andExpect(status().isNotFound());

    }

    @Test
    public void whenCreateTitle_thenReturnCreatedOne() throws Exception {

        given(titleService.saveTitle(any(Title.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        Title title = Title.builder()
                .id(null)
                .name("Title")
                .build();

        this.mockMvc.perform(post("/api/v1/titles/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(title)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is(title.getName())));
    }

    @Test
    public void whenCreateNewUserWithoutName_thenReturn404() throws Exception {

        Title title = Title.builder()
                .id(null)
                .name(null)
                .build();

        this.mockMvc.perform(post("/api/v1/titles/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(title)))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string("Content-Type", is("application/json")))
                    .andReturn();
    }

    @Test
    public void whenUpdateNonExistingTitle_thenReturnU404() throws Exception {

        Long titleId = 1L;

        given(titleService.findTitleById(titleId)).willReturn(Optional.empty());

        Title title = Title.builder()
                .id(titleId)
                .name("Title")
                .build();

        this.mockMvc.perform(put("/api/v1/titles/{id}", titleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(title)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenDeleteTitle_thenReturnNone() throws Exception {

        Long titleId = 1L;
        Title title = Title.builder()
                .id(titleId)
                .name("Title")
                .build();

        given(titleService.findTitleById(titleId)).willReturn(Optional.of(title));
        doNothing().when(titleService).deleteTitleById(title.getId());

        this.mockMvc.perform(delete("/api/v1/titles/{id}", title.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void whenDeleteNonExistingTitle_thenReturn404() throws Exception {

        Long titleId = 1L;
        given(titleService.findTitleById(titleId)).willReturn(Optional.empty());

        this.mockMvc.perform(delete("/api/v1/titles/{id}", titleId))
                .andExpect(status().isNotFound());
    }
}


