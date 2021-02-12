package com.webApp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webApp.model.Category;
import com.webApp.model.Title;
import com.webApp.service.TitleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(TitleController.class)
public class TitleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TitleService titleService;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    public void givenAllTitles_whenGetTitles_thenStatus200andTitlesReturned() throws Exception {
        Title title = Title.builder()
                .name("Biology")
                .build();

        List<Title> titles = Collections.singletonList(title);
        Mockito.when(titleService.findAllTitles()).thenReturn(titles);

        mockMvc.perform(
                get("/api/v1/titles/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(title.getName())));
    }

    @Test
    public void givenId_whenGetExistingTitle_thenStatus200andTitleReturned() throws Exception {

        Title found = Title.builder()
                .id(15L)
                .name("History")
                .build();
        Mockito.when(titleService.findTitleById(Mockito.any())).thenReturn(Optional.of(found));

        mockMvc.perform(
                get("/api/v1/titles/{id}", found.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("15"))
                .andExpect(jsonPath("$.name").value("History"));
    }

    @Test
    public void givenId_whenGetNotExistingTitle_thenStatus404anExceptionThrown() throws Exception {


        Mockito.when(titleService.findTitleById(Mockito.any())).
                thenReturn(Optional.empty());

        mockMvc.perform(
                get("/api/v1/titles/1"))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException()
                        .getClass()
                        .equals(EntityNotFoundException.class));

    }

    @Test
    public void givenTitle_whenAdd_thenStatus201AndTitleReturned() throws Exception {
        Set<Category> categories = new HashSet<>();
        Title titleToBeAdded = Title.builder()
                .categories(categories)
                .name("Computer Science")
                .build();

        Mockito.when(titleService.saveTitle(Mockito.any())).thenReturn(titleToBeAdded);

        mockMvc.perform(
                post("/api/v1/titles/")
                .content(objectMapper.writeValueAsString(titleToBeAdded))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated()).andExpect(content().json(objectMapper.writeValueAsString(titleToBeAdded)));
    }

    @Test
    public void givenTitle_whenDeleteTitle_thenStatus204() throws Exception {

        Title titleToBeDeleted = Title.builder()
                .id(1L)
                .name("Archeology")
                .build();

        Mockito.when(titleService.findTitleById(Mockito.any())).thenReturn(Optional.of(titleToBeDeleted));

        mockMvc.perform(
                delete("/api/v1/titles/1"))
                .andExpect(status().isNoContent());

    }

    @Test
    public void givenTitle_whenUpdateTitle_thenStatus204() throws Exception {


        Title titleToBeUpdated = Title.builder()
                .id(15L)
                .name("IT")
                .build();

        Mockito.when(titleService.saveTitle(Mockito.any())).thenReturn(titleToBeUpdated);
        Mockito.when(titleService.findTitleById(Mockito.any())).thenReturn(Optional.of(titleToBeUpdated));

        mockMvc.perform(
                put("/api/v1/titles/{id}", titleToBeUpdated.getId())
                        .content(objectMapper.writeValueAsString(new Title("Biology")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}


