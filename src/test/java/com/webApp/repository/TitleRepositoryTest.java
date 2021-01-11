package com.webApp.repository;

import com.webApp.model.Title;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class TitleRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private TitleRepository titleRepository;

    @Test
    public void whenFindById_thenReturnTitle() {

        //given
        Title title =  Title.builder()
                .name("Biology")
                .build();
        testEntityManager.persist(title);
        testEntityManager.flush();

        //when
        Title foundTitle = titleRepository.findTitleByName(title.getName());

        assertThat(foundTitle.getName()).isEqualTo(title.getName());
    }

    @Test
    public void whenFindAll_thenReturnTitleList() {
        //when
        List<Title> titles = titleRepository.findAll();

        //then
        assertThat(titles).hasSize(3);
    }


}
