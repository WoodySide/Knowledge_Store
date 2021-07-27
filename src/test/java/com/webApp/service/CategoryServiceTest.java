package com.webApp.service;

import com.webApp.model.Category;
import com.webApp.model.Title;
import com.webApp.model.User;
import com.webApp.repository.CategoryRepository;
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
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void whenFindAllCategories_thenReturnCategoryList() {
        //given
        Category category = Category.builder()
                .name("Videos")
                .build();

        List<Category> expectedCategories = Collections.singletonList(category);

        doReturn(expectedCategories).when(categoryRepository).findAll();

        //when
        List<Category> actualCategories = categoryService.findAllCategories();

        //then
        assertThat(actualCategories).isEqualTo(expectedCategories);
    }

    @Test
    public void whenFindCategoryById_thenReturnCategory() {

        when(categoryRepository.findById(1L)).
                thenReturn(java.util.Optional.of(new Category(1L, "Videos")));

        Optional<Category> category = categoryService.findCategoryById(1L);

        assertEquals("Videos", category.get().getName());
    }

    @Test
    public void whenCreateCategory_thenReturnCreatedOne() {
        Category categoryToBeSaved = new Category(1L, "Books");

        categoryService.saveCategory(categoryToBeSaved);

        verify(categoryRepository, times(1)).save(categoryToBeSaved);
    }

    @Test
    public void whenDeleteCategoryById_thenReturnNothing() {
        Category categoryToBeDeleted = new Category(1L, "Books");

        categoryService.deleteCategoryById(categoryToBeDeleted.getId());

        verify(categoryRepository, times(1)).deleteById(categoryToBeDeleted.getId());
    }

    @Test
    public void whenFindByTitleIdAndCategoryId_thenReturnCategory() {

        User user1 = User.builder()
                .username("username")
                .email("alex@gmail.com")
                .password("secret")
                .active(true)
                .isEmailVerified(true)
                .build();

        Title title1 = Title.builder()
                .id(1L)
                .name("Title1")
                .build();

        Category category = Category.builder()
                .name("Category")
                .build();

        title1.setUser(user1);
        category.setTitle(title1);

        when(categoryRepository.findByIdAndTitleId(category.getId(), title1.getId())).
                thenReturn(java.util.Optional.of(category));

        Optional<Category> foundCategory = categoryService.findByTitleIdAndCategoryId(category.getId(),title1.getId());

        assertEquals("Category", foundCategory.get().getName());
    }
}
