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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class TitleRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private TitleRepository titleRepository;

    @Test
    public void whenFindTitleByName_thenReturnTitle() {

        //given
        Title title1 = Title.builder()
                .name("Subject")
                .build();

        //when
        titleRepository.save(title1);

        Title title2 = titleRepository.findTitleByName("Subject");
        assertNotNull(title1);

        //then
        assertEquals(title2.getName(), title1.getName());
    }

    @Test
    public void whenFindById_thenReturnTitle() {

        //given
        Title title =  Title.builder()
                .name("Subject")
                .build();
        testEntityManager.persist(title);
        testEntityManager.flush();

        //when
        Optional<Title> foundTitle = titleRepository.findById(title.getId());

        //then
        assertThat(foundTitle.get().getId()).isEqualTo(title.getId());
    }

    @Test
    public void whenFindAll_thenReturnTitleList() {
        //when
        List<Title> titles = titleRepository.findAll();

        //then
        assertThat(titles).hasSizeGreaterThan(0);
    }

    @Test
    public void whenDeleteTitle_thenReturnNull() {
        //given
        Title title =  Title.builder()
                .name("Subject")
                .build();
        testEntityManager.persist(title);
        testEntityManager.flush();

        //when
        titleRepository.save(title);

        //then
        titleRepository.delete(title);
    }

    @Test
    public void whenDeleteTitleById_thenReturnNull() {

        //given
        Title title =  Title.builder()
                .name("Subject")
                .build();
        testEntityManager.persist(title);
        testEntityManager.flush();

        //when
        titleRepository.save(title);

        //then
        titleRepository.deleteById(title.getId());
    }

}
