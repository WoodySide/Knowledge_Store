package com.webApp.repository;

import com.webApp.model.Title;
import com.webApp.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

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

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();

        Title title = Title.builder()
                .name("Title1")
                .build();

        title.setUser(user1);

        testEntityManager.persist(title);

        //when
        Title foundTitle = titleRepository.findTitleByName("Title1");

        //then
        assertThat(foundTitle.getName()).isEqualTo("Title1");
    }

    @Test
    public void whenFindAll_thenReturnList() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();

        Title title = Title.builder()
                .name("Title1")
                .build();

        title.setUser(user1);
        testEntityManager.persist(title);

        //when
        List<Title> titles = titleRepository.findAll();

        //then
        assertThat(titles).hasSizeGreaterThan(0);
    }

    @Test
    public void whenFindById_thenReturnTitle() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();

        Title title = Title.builder().name("Title2").build();

        title.setUser(user1);

        testEntityManager.persist(title);

        Title foundTitle = titleRepository.findById(title.getId()).get();

        assertThat(foundTitle).isEqualTo(title);
    }

    @Test
    public void whenDeleteTitle_thenReturnNull() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();
        Title title1 = Title.builder().name("Title1").build();

        Title title2 = Title.builder().name("Title2").build();

        Title title3 = Title.builder().name("Title3").build();
        title1.setUser(user1);
        title2.setUser(user1);
        title3.setUser(user1);

        testEntityManager.persist(title1);
        testEntityManager.persist(title2);
        testEntityManager.persist(title3);

        titleRepository.deleteById(title1.getId());

        Iterable<Title> titles = titleRepository.findAll();

        assertThat(titles).hasSize(2).contains(title2,title3);
    }

    @Test
    public void whenDeleteAllTitles_thenReturnNoTitles() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();

        Title title1 = Title.builder()
                .name("Title1")
                .build();

        Title title2 = Title.builder()
                .name("Title2")
                .build();

        Title title3 = Title.builder()
                .name("Title3")
                .build();

        title1.setUser(user1);
        title2.setUser(user1);
        title3.setUser(user1);

        testEntityManager.persist(title1);
        testEntityManager.persist(title2);
        testEntityManager.persist(title3);

        titleRepository.deleteAll();

        assertThat(titleRepository.findAll()).isEmpty();
    }

    @Test
    public void whenFindTitlesByUserId_thenReturnAllTitles() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();

        Title title1 = Title.builder()
                .name("Title1")
                .build();

        Title title2 = Title.builder()
                .name("Title2")
                .build();

        Title title3 = Title.builder()
                .name("Title3")
                .build();

        title1.setUser(user1);
        title2.setUser(user1);
        title3.setUser(user1);

        testEntityManager.persist(title1);
        testEntityManager.persist(title2);
        testEntityManager.persist(title3);

        assertThat(titleRepository.findAllByUserId(user1.getId())).hasSize(3);
    }

    @Test
    public void whenFindTitleByUserId_thenReturnTitle() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();

        Title title1 = Title.builder()
                .name("Title1")
                .build();

        title1.setUser(user1);

        testEntityManager.persist(title1);

        Optional<Title> foundTitle = titleRepository.findByUserId(user1.getId());

        assertThat(foundTitle.get()).isEqualTo(title1);
    }
}
