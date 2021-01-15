package com.webApp.service;

import com.webApp.model.Link;
import com.webApp.repository.LinkRepository;
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
public class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private LinkService linkService;

    @Test
    public void whenFindAllLinks_thenReturnListOfLinks() {
        //given
        Link link = Link.builder()
                .linkName("https://habr.com/ru/post/471140/")
                .build();

        List<Link> expectedLinks = Collections.singletonList(link);

        doReturn(expectedLinks).when(linkRepository).findAll();

        //when
        List<Link> actualLinks = linkService.findAllLinks();

        //then
        assertThat(actualLinks).isEqualTo(expectedLinks);
    }

    @Test
    public void whenFindLinkById_thenReturnLink() {
        when(linkRepository.findById(1L)).
                thenReturn(java.util.Optional.of(new Link(1L, "https://habr.com/ru/post/471140/")));

        Optional<Link> link = linkService.findLinkById(1L);

        assertEquals("https://habr.com/ru/post/471140/", link.get().getLinkName());
    }

    @Test
    public void whenCreateLink_thenReturnCreatedOne() {
        Link linkToBeCreated = new Link(1L, "https://habr.com/ru/post/471140/");

        linkService.saveLink(linkToBeCreated);

        verify(linkRepository, times(1)).save(linkToBeCreated);
    }

    @Test
    public void whenDeleteLink_theReturnNothing() {
        Link linkToBeDeleted = new Link(1L, "https://habr.com/ru/post/471140/");

        linkService.deleteLinkById(linkToBeDeleted.getId());

        verify(linkRepository, times(1)).deleteById(linkToBeDeleted.getId());
    }
}
