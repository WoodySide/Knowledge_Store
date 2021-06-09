package com.webApp.repository;

import com.webApp.model.Category;
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
public class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void whenRepositoryIsEmpty_thenReturnNull() {
        Iterable<Category> categories = categoryRepository.findAll();

        assertThat(categories).isEmpty();
    }

    @Test
    public void whenFindByName_thenReturnCategory() {

        Title title = Title.builder()
                .name("Title")
                .build();

        Category category = Category.builder()
                .name("Category")
                .build();

        category.setTitle(title);

        testEntityManager.persist(category);

        //when
        Category foundCategory = categoryRepository
                .findCategoryByName("Category");

        //then
        assertThat(foundCategory.getName()).isEqualTo("Category");
    }

    @Test
    public void whenFindAll_thenReturnList() {

        Title title = Title.builder()
                .name("Title")
                .build();

        Category category = Category.builder()
                .name("Category")
                .build();

        category.setTitle(title);

        testEntityManager.persist(category);

        //when
        List<Category> categories = categoryRepository.findAll();

        //then
        assertThat(categories).hasSizeGreaterThan(0);
    }

    @Test
    public void whenFindById_thenReturnCategory() {

        Title title = Title.builder()
                .name("Title")
                .build();

        Category category = Category.builder()
                .name("Category")
                .build();

        category.setTitle(title);

        testEntityManager.persist(category);

        //when
        Category foundCategory = categoryRepository
                .findById(category.getId()).get();

        //then
        assertThat(foundCategory).isEqualTo(category);
    }

    @Test
    public void whenDeleteCategory_thenReturnNull() {

        Title title1 = Title.builder()
                .name("Title1")
                .build();

        Title title2 = Title.builder()
                .name("Title2")
                .build();

        Title title3 = Title.builder()
                .name("Title3")
                .build();

        Category category1 = Category.builder()
                .name("Category1")
                .build();

        category1.setTitle(title1);
        testEntityManager.persist(category1);

        Category category2 = Category.builder()
                .name("Category2")
                .build();
        category2.setTitle(title2);

        testEntityManager.persist(category2);

        Category category3 = Category.builder()
                .name("Category3")
                .build();
        category3.setTitle(title3);
        testEntityManager.persist(category3);

        categoryRepository.deleteById(category1.getId());

        //when
        Iterable<Category> categories = categoryRepository.findAll();

        //then
        assertThat(categories).hasSize(2).contains(category2,category3);
    }

    @Test
    public void whenDeleteAllCategories_thenReturnNoCategories() {

        Title title1 = Title.builder()
                .name("Title1")
                .build();

        Category category1 = Category.builder()
                .name("Category1")
                .build();

        category1.setTitle(title1);
        testEntityManager.persist(category1);

        Title title2 = Title.builder()
                .name("Title2")
                .build();

        Category category2 = Category.builder()
                .name("Category2")
                .build();

        category2.setTitle(title2);

        testEntityManager.persist(category2);

        //when
        categoryRepository.deleteAll();

        //then
        assertThat(categoryRepository.findAll()).isEmpty();
    }

    @Test
    public void whenFindByTitleId_thenReturnCategory() {
        Title title = Title.builder()
                .name("Title")
                .build();

        Category category = Category.builder()
                .name("Category")
                .build();

        category.setTitle(title);

        testEntityManager.persist(category);
    }
}