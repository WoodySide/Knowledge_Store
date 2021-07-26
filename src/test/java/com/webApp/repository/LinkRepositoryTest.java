package com.webApp.repository;


import com.webApp.model.Category;
import com.webApp.model.Link;
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
public class LinkRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private LinkRepository linkRepository;


    @Test
    public void whenRepositoryIsEmpty_thenReturnNull() {

        Iterable<Link> links = linkRepository.findAll();

        assertThat(links).isEmpty();
    }


    @Test
    public void whenFindByName_thenReturnLink() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();


        Title title = Title
                .builder()
                .name("Title")
                .build();

        title.setUser(user1);

        Category category = Category
                .builder()
                .name("Category")
                .build();

        Link link = Link
                .builder()
                .linkName("http://LinkName")
                .build();

        category.setTitle(title);
        link.setCategory(category);

        testEntityManager.persist(link);

        //when
        Link foundLink = linkRepository.findLinkByLinkName("http://LinkName");

        //then
        assertThat(foundLink.getLinkName()).isEqualTo("http://LinkName");
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

        Title title = Title
                .builder()
                .name("Title")
                .build();

        title.setUser(user1);

        Category category = Category
                .builder()
                .name("Category")
                .build();

        Link link = Link
                .builder()
                .linkName("http://LinkName")
                .build();

        category.setTitle(title);
        link.setCategory(category);

        testEntityManager.persist(link);

        //when
        List<Link> links = linkRepository.findAll();

        //then
        assertThat(links).hasSizeGreaterThan(0);
    }

    public void whenFindById_thenReturnLink() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();


        Title title = Title
                .builder()
                .name("Title")
                .build();

        title.setUser(user1);

        Category category = Category
                .builder()
                .name("Category")
                .build();

        Link link = Link
                .builder()
                .linkName("http://LinkName")
                .build();

        category.setTitle(title);
        link.setCategory(category);

        testEntityManager.persist(link);

        //when
        Link foundLink = linkRepository.findById(link.getId()).get();

        //then
        assertThat(foundLink).isEqualTo(link);
    }

    @Test
    public void whenDeleteLink_thenReturnNull() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();

        Title title1 = Title
                .builder()
                .name("Title1")
                .build();

        Title title2 = Title
                .builder()
                .name("Title2")
                .build();

        Title title3 = Title
                .builder()
                .name("Title3")
                .build();

        title1.setUser(user1);
        title2.setUser(user1);
        title3.setUser(user1);

        Category category1 = Category
                .builder()
                .name("Category1")
                .build();

        Category category2 = Category
                .builder()
                .name("Category2")
                .build();

        Category category3 = Category
                .builder()
                .name("Category3")
                .build();

        Link link1 = Link
                .builder()
                .linkName("http://LinkName1")
                .build();

        Link link2 = Link
                .builder()
                .linkName("http://LinkName2")
                .build();

        Link link3 = Link
                .builder()
                .linkName("http://LinkName3")
                .build();

        category1.setTitle(title1);
        category2.setTitle(title2);
        category3.setTitle(title3);
        link1.setCategory(category1);
        link2.setCategory(category2);
        link3.setCategory(category3);

        testEntityManager.persist(link1);
        testEntityManager.persist(link2);
        testEntityManager.persist(link3);

        linkRepository.deleteById(link2.getId());

        //when
        Iterable<Link> links = linkRepository.findAll();

        //then
        assertThat(links).hasSize(2).contains(link1,link3);
    }

    @Test
    public void whenDeleteAllLinks_thenReturnNoLinks() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();

        Title title1 = Title
                .builder()
                .name("Title1")
                .build();

        Title title2 = Title
                .builder()
                .name("Title2")
                .build();

        Title title3 = Title
                .builder()
                .name("Title3")
                .build();

        title1.setUser(user1);
        title2.setUser(user1);
        title3.setUser(user1);

        Category category1 = Category
                .builder()
                .name("Category1")
                .build();

        Category category2 = Category
                .builder()
                .name("Category2")
                .build();

        Category category3 = Category
                .builder()
                .name("Category3")
                .build();

        Link link1 = Link
                .builder()
                .linkName("http://LinkName1")
                .build();

        Link link2 = Link
                .builder()
                .linkName("http://LinkName2")
                .build();

        Link link3 = Link
                .builder()
                .linkName("http://LinkName3")
                .build();

        category1.setTitle(title1);
        category2.setTitle(title2);
        category3.setTitle(title3);
        link1.setCategory(category1);
        link2.setCategory(category2);
        link3.setCategory(category3);

        testEntityManager.persist(link1);
        testEntityManager.persist(link2);
        testEntityManager.persist(link3);

        //when
        linkRepository.deleteAll();

        //then
        assertThat(linkRepository.findAll()).isEmpty();
    }

    @Test
    public void whenFindLinkByCategoryId_thenReturnLink() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();

        Title title = Title
                .builder()
                .name("Title")
                .build();

        title.setUser(user1);

        Category category = Category
                .builder()
                .name("Category")
                .build();

        Link link = Link
                .builder()
                .linkName("http://LinkName")
                .build();

        category.setTitle(title);
        link.setCategory(category);

        testEntityManager.persist(link);

        List<Link> foundLinks = linkRepository.findByCategoryId(category.getId());

        assertThat(foundLinks).hasSizeGreaterThan(0);
    }

    @Test
    public void whenFindByCategoryIdAndLinkId_thenReturnLink() {
        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();

        Title title = Title
                .builder()
                .name("Title")
                .build();

        title.setUser(user1);

        Category category = Category
                .builder()
                .name("Category")
                .build();

        Link link = Link
                .builder()
                .linkName("http://LinkName")
                .build();

        category.setTitle(title);
        link.setCategory(category);

        testEntityManager.persist(link);

        Optional<Link> foundLink = linkRepository.findByIdAndCategoryId(category.getId(), link.getId());

        assertThat(foundLink.get()).isEqualTo(link);
    }
}


