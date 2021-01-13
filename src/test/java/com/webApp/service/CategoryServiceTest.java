package com.webApp.service;

import com.webApp.model.Category;
import com.webApp.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
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
}
