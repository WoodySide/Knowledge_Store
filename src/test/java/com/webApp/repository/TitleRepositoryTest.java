package com.webApp.repository;

import com.webApp.model.Title;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@RunWith(SpringRunner.class)
public class TitleRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private TitleRepository titleRepository;


    @Test
    public void whenRepositoryIsEmpty_thenReturnNull() {
        Iterable<Title> titles = titleRepository.findAll();

        assertThat(titles).isEmpty();
    }


    @Test
    public void whenFindByName_thenReturnTitle() {

        Title title = Title.builder()
                .name("Title")
                .build();

        testEntityManager.persist(title);

        //when
        Title foundTitle = titleRepository.findTitleByName("Title");

        //then
        assertThat(foundTitle.getName()).isEqualTo("Title");
    }

    @Test
    public void whenFindAll_thenReturnList() {

        Title title = Title.builder()
                .name("Title")
                .build();

        testEntityManager.persist(title);
        //when
        List<Title> titles = titleRepository.findAll();

        //then
        assertThat(titles).hasSizeGreaterThan(0);
    }

    @Test
    public void whenFindById_thenReturnTitle() {

        Title title = Title.builder().name("Title2").build();

        testEntityManager.persist(title);

        Title foundTitle = titleRepository.findById(title.getId()).get();

        assertThat(foundTitle).isEqualTo(title);
    }

    @Test
    public void whenDeleteTitle_thenReturnNull() {
        Title title1 = Title.builder().name("Title1").build();
        testEntityManager.persist(title1);

        Title title2 = Title.builder().name("Title2").build();
        testEntityManager.persist(title2);

        Title title3 = Title.builder().name("Title3").build();
        testEntityManager.persist(title3);

        titleRepository.deleteById(title1.getId());

        Iterable<Title> titles = titleRepository.findAll();

        assertThat(titles).hasSize(2).contains(title2,title3);
    }

    @Test
    public void whenDeleteAllTitles_thenReturnNoTitles() {
        testEntityManager.persist(Title.builder().name("Title1").build());
        testEntityManager.persist(Title.builder().name("Title2").build());

        titleRepository.deleteAll();

        assertThat(titleRepository.findAll()).isEmpty();
    }
}
