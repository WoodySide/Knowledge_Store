package com.webApp.service;

import com.webApp.model.Title;
import com.webApp.model.User;
import com.webApp.repository.TitleRepository;
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
import static org.mockito.BDDMockito.given;
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
                .name("Title")
                .build();

        List<Title> expectedTitles = Collections.singletonList(title);

        doReturn(expectedTitles).when(titleRepository).findAll();

        //when
        List<Title> actualTitles = titleService.findAllTitles();

        //then
        assertThat(actualTitles).isEqualTo(expectedTitles);
    }

    @Test
    public void whenCreateTitle_thenReturnCreatedOne() {
        Title toBeCreated = new Title(1L,"Title1");

        titleService.saveTitle(toBeCreated);

        assertThat(toBeCreated.getCategories()).hasSize(4);
    }

    @Test
    public void whenUpdateTitle_thenReturnUpdatedOne() {

        Title title = Title
                .builder()
                .name("Title")
                .build();

        given(titleRepository.save(title)).willReturn(title);

        Title expectedTitle = titleService.saveTitle(title);

        assertThat(expectedTitle).isNotNull();

        verify(titleRepository).save(any(Title.class));
    }

    @Test
    public void whenFindTitleById_thenReturnTitle() {

        Title title = Title
                .builder()
                .name("Title")
                .build();

        when(titleRepository.findById(1L))
                .thenReturn(Optional.of(title));

        Optional<Title> foundTitle = titleService.findTitleById(1L);

        assertEquals("Title", foundTitle.get().getName());
    }

    @Test
    public void whenDeleteTitleById_thenReturnNothing() {

        Title titleToBeDeleted = Title.builder()
                .id(1L)
                .name("Title")
                .build();
        titleService.deleteTitleById(titleToBeDeleted.getId());

        verify(titleRepository, times(1))
                .deleteById(titleToBeDeleted.getId());
    }

    @Test
    public void whenFindAllTitlesByUserId_returnListOfTitle() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();

        Title title1 = Title.builder()
                .id(1L)
                .name("Title1")
                .build();

        Title title2 = Title.builder()
                .id(2L)
                .name("Title2")
                .build();

        Title title3 = Title.builder()
                .id(3L)
                .name("Title3")
                .build();


        List<Title> expectedTitles = List.of(title1,title2,title3);

        expectedTitles.forEach(t -> t.setUser(user1));

        doReturn(expectedTitles).when(titleRepository).findAllByUserId(user1.getId());

        //when
        List<Title> actualTitles = titleService.findAllByUserId(user1.getId());

        //then
        assertThat(actualTitles).isEqualTo(expectedTitles);

    }
}
