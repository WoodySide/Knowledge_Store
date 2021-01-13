package com.webApp.service;

import com.webApp.model.Title;
import com.webApp.repository.TitleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TitleServiceTest {

    @Mock
    private TitleRepository titleRepository;

    @InjectMocks
    private TitleService titleService;

    @Test
    public void whenFindAllTitles_thenReturnTitleList() {
        Title title = Title.builder()
                .name("Economy")
                .build();

        List<Title> expectedTitles = Collections.singletonList(title);

        doReturn(expectedTitles).when(titleRepository).findAll();

        //when
        List<Title> actualTitles = titleService.findAllTitles();

        //then
        assertThat(actualTitles).isEqualTo(expectedTitles);
    }

    @Test
    public void whenFindTitleById_thenReturnTitle() {
        when(titleRepository.findById(1L))
                .thenReturn(Optional.of(new Title(1L, "History")));

        Optional<Title> title = titleService.findTitleById(1L);

        assertEquals("History", title.get().getName());
    }

    @Test
    public void whenCreateTitle_thenReturnCreatedOne() {

        Title titleToBeSaved = Title.builder()
                .name("Economy")
                .build();

        titleService.saveTitle(titleToBeSaved);

        verify(titleRepository, times(1)).save(titleToBeSaved);
    }

    @Test
    public void whenDeleteTitleById_thenReturnNothing() {

        Title titleToBeDeleted = Title.builder()
                .id(1L)
                .name("History")
                .build();

        titleService.deleteTitleById(titleToBeDeleted.getId());

        verify(titleRepository, times(1)).deleteById(titleToBeDeleted.getId());
    }
}
