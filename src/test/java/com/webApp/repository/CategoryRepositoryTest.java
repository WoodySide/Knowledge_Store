package com.webApp.repository;

import com.webApp.model.Category;
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
public class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    public void whenFindByName_thenReturnCategory() {

        //given
        Category category = Category.builder()
                .name("Podcasts")
                .build();
        testEntityManager.persist(category);
        testEntityManager.flush();

        //when
        Category foundCategory = categoryRepository.findCategoryByName(category.getName());

        assertThat(foundCategory.getName()).isEqualTo(category.getName());

    }

    @Test
    public void whenFindAll_thenReturnCategoryList() {
        //when
        List<Category> categoryList = categoryRepository.findAll();

        //then
        assertThat(categoryList).hasSize(9);
    }

}
