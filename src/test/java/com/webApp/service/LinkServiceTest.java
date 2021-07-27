package com.webApp.service;

import com.webApp.model.Category;
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
                .linkName("https://link")
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
                thenReturn(java.util.Optional.of(new Link(1L, "https://link")));

        Optional<Link> link = linkService.findLinkById(1L);

        assertEquals("https://link", link.get().getLinkName());
    }

    @Test
    public void whenCreateLink_thenReturnCreatedOne() {
        Link linkToBeCreated = new Link(1L, "https://link");

        linkService.saveLink(linkToBeCreated);

        verify(linkRepository, times(1)).save(linkToBeCreated);
    }

    @Test
    public void whenDeleteLink_theReturnNothing() {
        Link linkToBeDeleted = new Link(1L, "https://link");

        linkService.deleteLinkById(linkToBeDeleted.getId());

        verify(linkRepository, times(1)).deleteById(linkToBeDeleted.getId());
    }

    @Test
    public void whenFindAllLinksByCategoryId_thenReturnListOfLinks() {

        Category category = Category.builder()
                .name("Category")
                .build();

        Link link1 = Link.builder()
                .linkName("https://link1")
                .category(category)
                .build();
        Link link2 = Link.builder()
                .linkName("https://link2")
                .build();
        Link link3 = Link.builder()
                .linkName("https://link3")
                .build();

        List<Link> expectedLinks = List.of(link1,link2,link3);

        expectedLinks.forEach(l -> l.setCategory(category));

        doReturn(expectedLinks).when(linkRepository).findByCategoryId(category.getId());

        //when
        List<Link> actualLinks = linkService.findByCategoryId(category.getId());

        //then
        assertThat(actualLinks).isEqualTo(expectedLinks);

    }

    @Test
    public void whenFindByCategoryIdAndByLinkId_thenReturnLink() {

        Category category = Category.builder()
                .name("Category")
                .build();

        Link link1 = Link.builder()
                .linkName("https://link1")
                .category(category)
                .build();

        link1.setCategory(category);


        when(linkRepository.findByIdAndCategoryId(link1.getId(),category.getId())).
                thenReturn(java.util.Optional.of(link1));

        Optional<Link> link = linkService.findByCategoryIdAndLinkId(link1.getId(),category.getId());

        assertEquals("https://link1", link.get().getLinkName());
    }
}
