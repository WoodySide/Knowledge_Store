package com.webApp.repository;


import com.webApp.model.Link;
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
public class LinkRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private LinkRepository linkRepository;


    @Test
    public void whenFindByName_thenReturnLink() {

        //given
        Link link = Link.builder()
                .linkName("https://www.baeldung.com/spring-requestmapping")
                .build();
        testEntityManager.persist(link);
        testEntityManager.flush();

        //when
        Link foundLink = linkRepository.findLinkByLinkName(link.getLinkName());

        //then
        assertThat(foundLink.getLinkName()).isEqualTo(link.getLinkName());
    }


    @Test
    public void whenFindAll_thenReturnLinkList() {
        //when
        List<Link> linkList = linkRepository.findAll();

        //then
        assertThat(linkList).hasSize(10);
    }
}


