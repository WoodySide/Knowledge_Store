package com.webApp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    public void givenId_whenGetExistingPerson_thenStatus200andPersonReturned() throws Exception {

        Title title = Title.builder()
                .id(15L)
                .name("History")
                .build();
        Mockito.when(titleService.findTitleById(Mockito.any())).thenReturn(Optional.of(title));

        mockMvc.perform(
                get("/api/v1/titles/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("15"))
                .andExpect(jsonPath("$.name").value("History"));
    }

    @Test
    public void givenId_whenGetNotExistingPerson_thenStatus404anExceptionThrown() throws Exception {


        Mockito.when(titleService.findTitleById(Mockito.any())).
                thenReturn(Optional.empty());

        mockMvc.perform(
                get("/api/v1/titles/1"))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException()
                        .getClass()
                        .equals(EntityNotFoundException.class));

    }

    //TODO: to test the rest methods
}


